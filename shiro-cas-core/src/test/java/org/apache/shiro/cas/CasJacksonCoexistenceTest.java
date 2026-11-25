package org.apache.shiro.cas;

import java.util.List;
import java.util.Map;

import org.apereo.cas.client.validation.Assertion;
import org.apereo.cas.client.validation.TicketValidationException;
import org.apereo.cas.client.validation.json.Cas30JsonServiceTicketValidator;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 验证 CAS SDK 内部 Jackson 2 与项目 Jackson 3 共存时的真实验票和 JSON 边界。 */
class CasJacksonCoexistenceTest {

    @Test
    void validatedCasIdentityCanBeWrittenAndReadWithJackson3() throws Exception {
        Assertion assertion = new JsonTicketValidator().parse("""
                {"serviceResponse":{"authenticationSuccess":{
                    "user":"alice","attributes":{"roles":["reader","auditor"],"active":true}
                }}}
                """);
        JsonMapper mapper = JsonMapper.builder().build();
        String applicationJson = mapper.writeValueAsString(Map.of(
                "user", assertion.getPrincipal().getName(),
                "attributes", assertion.getPrincipal().getAttributes()));
        JsonNode application = mapper.readTree(applicationJson);

        assertEquals("alice", application.get("user").asString());
        assertEquals(List.of("reader", "auditor"), assertion.getPrincipal().getAttributes().get("roles"));
        assertTrue(application.get("attributes").get("active").asBoolean());
    }

    @Test
    void rejectedCasTicketRemainsRejectedWithJackson3OnClasspath() {
        TicketValidationException failure = assertThrows(TicketValidationException.class,
                () -> new JsonTicketValidator().parse("""
                        {"serviceResponse":{"authenticationFailure":{
                            "code":"INVALID_TICKET","description":"Ticket has expired"
                        }}}
                        """));

        assertTrue(failure.getMessage().contains("INVALID_TICKET"));
        JsonMapper mapper = JsonMapper.builder().build();
        assertEquals("denied", mapper.readTree(mapper.writeValueAsString(Map.of("result", "denied")))
                .get("result").asString());
    }

    /** 公开受保护的响应解析入口，测试不调用远程 CAS 服务。 */
    private static final class JsonTicketValidator extends Cas30JsonServiceTicketValidator {

        private JsonTicketValidator() {
            super("https://cas.example.test");
        }

        private Assertion parse(String response) throws TicketValidationException {
            return parseResponseFromServer(response);
        }
    }
}
