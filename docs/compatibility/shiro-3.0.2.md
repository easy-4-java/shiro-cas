# Shiro 3.0.2 适配契约与验收

本次变更以用户确认的版本矩阵为事实源，沿用现有分支，不安装或初始化新的规格工具。

| 分支 | Shiro | Java | Jackson | Servlet | Spring Framework |
| --- | --- | --- | --- | --- | --- |
| feature/2.0.x | 3.0.2 | 17 | 2 | Jakarta | 6.2 |
| feature/3.0.x | 3.0.2 | 21 | 3 | Jakarta | 7.0 |

当前仓库：`shiro-cas`；当前版本线：`feature/2.0.x`。

## 范围与兼容性

- 所有 Shiro 模块必须解析到 3.0.2，不混用 1.x/2.x 或 Jakarta classifier。
- 同系列 easy4j 公共组件使用当前版本线，禁止 2.0.x 引用 3.0.x。
- Servlet 接口迁移到 jakarta.servlet；不替换 JDK 中仍合法的 javax.crypto、javax.naming 等命名空间。
- 2.0.x 保留 Jackson 2 API；3.0.x 使用 tools.jackson，Jackson 注解仍使用 com.fasterxml.jackson.annotation。
- 保留认证、授权、缓存及会话语义，外部 CAS/LDAP/微信等协议不以桩实现代替。
- 不修改 feature/1.0.x，不提交、推送或发布，不覆盖已有用户修改。

## 执行与验收

- [ ] 核对 Maven 有效模型、依赖树、JDK 和分支。
- [x] 对齐 Shiro、Jackson、Spring、Servlet 和公共组件的依赖声明（传递依赖树尚未验证）。
- [x] 使用目标 JDK 调用 Maven 测试流程，记录实际失败及阻塞。
- [ ] 依据编译及回归证据修正源码和测试中的接口兼容问题。
- [ ] 使用目标 JDK 运行全部现有测试，记录测试数、失败与跳过。
- [x] 静态检查没有残留 javax.servlet 导入或错误 Jackson 主版本声明；旧第三方桥接的反射类名保留。
- [ ] 对没有自动化测试、依赖缺失或外部服务未验证项明确保留未完成状态。

## 证据与状态

状态：适配进行中。编译成功、依赖静态检查或零测试执行均不能单独证明适配完成。

参考：[Shiro 迁移指南](https://shiro.apache.org/migration-guide.html)、[Jackson 3 迁移指南](https://github.com/FasterXML/jackson/blob/main/jackson3/MIGRATING_TO_JACKSON_3.md)。

## 本次执行证据（2026-11-17）

声明/导入静态检查：**通过**；分支：`feature/2.0.x`；目标 Java：`17`。

Maven 测试流程退出码：`1`。完整日志：[maven-test.log](evidence/maven-test.log)；结构化结果：[verification.json](evidence/verification.json)。


旧 CAS client 的 Javax 边界已通过原生 Jakarta 客户端坐标/API 迁移处理，2.0.x 已补客户端声明。URL 测试已通过；完整认证、票据、SAML 和登出尚未验收。原有 mvnw/mvnw.cmd 用户修改保留。

当前执行环境无法解析 Maven Central 域名，且所需缓存不完整。公共 shiro-extension 的依赖/本地 SNAPSHOT 父 POM 缺失使大量下游模块尚未进入编译；不得把此状态判为适配成功。

## 续接修复：CAS 原生 Jakarta 客户端

采用 `org.apereo.cas.client:cas-client-core:4.0.4`，同步迁移包名，保留 CAS1/2/3 与 SAML11 支持；SAML 依赖使用同版本 `cas-client-support-saml`。修正 URL 工具缺失导入，新增 URL 重定向与 Jakarta 请求回归测试。该迁移需要完整模块和协议测试通过后才能验收。

## 最新验收状态

Java 17 下 URL 回归测试 **3/3 通过**，测试覆盖实际回调地址、编码的 service 参数、renew 与 Jakarta 请求读取。证据：[resume-focused-tests.log](evidence/resume-focused-tests.log)。完整 Maven test 仍受缺失的 SAML/公共 SNAPSHOT 等依赖阻塞。

用户已于 2026-11-18 确认：3.0.x 允许 CAS SDK 内部保留 Jackson 2。项目源码和公共 API 仍使用 Jackson 3；CAS SDK 的 JSON 票据验证器使用受控 Jackson 2 依赖，不进行排除或删减。该例外不扩大到其他组件或项目公共 API。

## CAS SDK 内部 Jackson 2 例外（已确认）

- 仅 feature/3.0.x 的 CAS SDK 内部允许使用 Jackson 2；生产源码禁止引入 Jackson 2 core/databind 类型，公共 API 不暴露这些类型。共享注解仍使用 com.fasterxml.jackson.annotation。
- 3.0.x 保留 tools.jackson BOM 3.2.1，并通过单独的 CAS Jackson 2 BOM 属性固定 SDK 依赖为 2.22.3；不继承 SDK 默认的 Jackson 2.16.1。
- Java 21 下同一 classpath 的 CAS JSON 成功/拒绝解析与项目 Jackson 3 JSON 处理已通过；连同 URL 回归合计 5/5。实际父 POM 的有效模型及最小消费项目依赖树已通过；完整 CAS Maven 依赖树仍待执行。
- feature/2.0.x 继续使用 Java 17/Jackson 2，本次例外不引入 Jackson 3。

本次例外实现与补充证据位于 feature/3.0.x 的 shiro-cas/docs/compatibility/evidence/。本分支不引入 Jackson 3 依赖或共存测试。

## 2026-11-18 全量矩阵复验

`mvn clean verify` 与 Maven 依赖树通过。现有测试 3 项，失败 0，错误 0，跳过 0。SDK 内部 Jackson 2 例外通过实际依赖树验证，真实 CAS 服务集成待验证。

[Maven 验证日志与依赖树](evidence/maven-verify-shiro-3.0.2.log)。
