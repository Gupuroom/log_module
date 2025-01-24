package com.log_module;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.log_module.test.controller.TestController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestController.class)
class LoggingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        listAppender = new ListAppender<>();
        listAppender.start();

        Logger logger = (Logger) LoggerFactory.getLogger("com.example");
        logger.addAppender(listAppender);
    }

    @Test
    void testGetWithRequestParam() throws Exception {
        mockMvc.perform(get("/api/logging").param("userId", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("SUCCESS"));

        // 로그 검증
        assertLogContains("Received GET request with userId: 123");
    }

    @Test
    void testPostWithRequestBody() throws Exception {
        mockMvc.perform(post("/api/logging")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":123}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("SUCCESS"));

        // 로그 검증
        assertLogContains("Received POST request with body: {userId=123}");
    }

    @Test
    void testGetWithPathVariableAndRequestParam() throws Exception {
        mockMvc.perform(get("/api/logging/{id}", 1).param("userId", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("SUCCESS"));

        // 로그 검증
        assertLogContains("Received GET request with PathVariable id: 1 and userId: 123");
    }

    @Test
    void testPostWithPathVariable() throws Exception {
        mockMvc.perform(post("/api/logging/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("SUCCESS"));

        // 로그 검증
        assertLogContains("Received POST request with PathVariable id: 1");
    }

    @Test
    void testPostWithPathVariableAndRequestBody() throws Exception {
        mockMvc.perform(post("/api/logging/{id}/data", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"data\":\"value\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("SUCCESS"));

        // 로그 검증
        assertLogContains("Received POST request with PathVariable id: 1 and body: {data=value}");
    }

    private void assertLogContains(String expectedMessage) {
        // 로그가 리스트에 포함되어 있는지 확인
        boolean logFound = listAppender.list.stream()
                .anyMatch(loggingEvent -> loggingEvent.getFormattedMessage().contains(expectedMessage));
        assert logFound : "Log with message [" + expectedMessage + "] not found!";
    }
}
