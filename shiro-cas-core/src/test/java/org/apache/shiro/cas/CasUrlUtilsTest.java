package org.apache.shiro.cas;

import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.shiro.cas.spring.CasClientProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 验证 CAS 客户端迁移后对外生成的重定向与服务地址。 */
class CasUrlUtilsTest {

    @Test
    void constructsLoginRedirectWithEncodedServiceAndRenew() {
        CasClientProperties properties = new CasClientProperties();
        properties.setServerName("https://app.example.test:8443");
        properties.setCasServerLoginUrl("https://sso.example.test/cas/login");
        properties.setServiceParameterName("service");
        properties.setRenew(true);

        URI redirect = URI.create(CasUrlUtils.constructLoginRedirectUrl(properties, "/console/", "/callback"));
        Map<String, String> parameters = Arrays.stream(redirect.getRawQuery().split("&"))
                .map(parameter -> parameter.split("=", 2))
                .collect(Collectors.toMap(parameter -> parameter[0],
                        parameter -> URLDecoder.decode(parameter[1], StandardCharsets.UTF_8)));

        assertEquals("https", redirect.getScheme());
        assertEquals("sso.example.test", redirect.getHost());
        assertEquals("/cas/login", redirect.getPath());
        assertEquals("https://app.example.test:8443/console/callback", parameters.get("service"));
        assertEquals("true", parameters.get("renew"));
    }

    @Test
    void normalizesRootContextForCallbacks() {
        assertEquals("/callback", CasUrlUtils.constructCallbackUrl("/", "/callback"));
        assertEquals("/console/callback", CasUrlUtils.constructCallbackUrl("/console/", "/callback"));
    }

    @Test
    void readsServiceUrlFromJakartaRequestWhenServerNameIsAbsent() {
        HttpServletRequest request = (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                (proxy, method, arguments) -> {
                    if ("getRequestURL".equals(method.getName())) {
                        return new StringBuffer("https://app.example.test/console/callback");
                    }
                    throw new UnsupportedOperationException(method.getName());
                });

        assertEquals("https://app.example.test/console/callback",
                CasUrlUtils.constructServiceUrl(request, null, new CasClientProperties()));
    }
}

