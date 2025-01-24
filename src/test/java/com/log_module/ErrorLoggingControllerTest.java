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
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestController.class)
class ErrorLoggingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        // ListAppender 초기화 및 Logger에 연결
        listAppender = new ListAppender<>();
        listAppender.start();

        // com.example 패키지의 Logger에 ListAppender를 추가
        Logger logger = (Logger) LoggerFactory.getLogger("com.example");
        logger.addAppender(listAppender);
    }

    @Test
    void testGetError() throws Exception {
        mockMvc.perform(get("/api/logging/error"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("301"))
                .andExpect(jsonPath("$.message").value("GET_TEST_ERROR"));

        // 에러 로그 검증
        assertLogContains("Simulating an error in GET request");
        assertLogContains("Business exception occurred: code: 301, message: GET_TEST_ERROR");
    }

    @Test
    void testPostError() throws Exception {
        mockMvc.perform(post("/api/logging/error"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("302"))
                .andExpect(jsonPath("$.message").value("POST_TEST_ERROR"));

        // 에러 로그 검증
        assertLogContains("Simulating an error in POST request");
        assertLogContains("Business exception occurred: code: 302, message: POST_TEST_ERROR");
    }

    private void assertLogContains(String expectedMessage) {
        // 로그가 리스트에 포함되어 있는지 확인
        boolean logFound = listAppender.list.stream()
                .anyMatch(loggingEvent -> loggingEvent.getFormattedMessage().contains(expectedMessage));
        assert logFound : "Log with message [" + expectedMessage + "] not found!";
    }
}
