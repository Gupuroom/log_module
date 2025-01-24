package com.log_module.test.controller;

import com.log_module.exception.CommonException;
import com.log_module.test.type.TestExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/logging")
public class TestController {

    @GetMapping
    public ResponseEntity<?> getTest(@RequestParam(value = "userId", required = false) String userId) {
        log.info("Received GET request with userId: {}", userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWithPathVariableAndRequestParam(
            @PathVariable("id") String id,
            @RequestParam(value = "userId", required = false) String userId
    ) {
        log.info("Received GET request with PathVariable id: {} and userId: {}", id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<?> postWithRequestBody(@RequestBody Map<String, Object> requestBody) {
        log.info("Received POST request with body: {}", requestBody);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> postWithPathVariable(@PathVariable("id") String id) {
        log.info("Received POST request with PathVariable id: {}", id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/data")
    public ResponseEntity<?> postWithPathVariableAndRequestBody(
            @PathVariable("id") String id,
            @RequestBody Map<String, Object> requestBody
    ) {
        log.info("Received POST request with PathVariable id: {} and body: {}", id, requestBody);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/error")
    public ResponseEntity<?> getError() {
        log.error("Simulating an error in GET request");
        throw new CommonException(TestExceptionCode.GET_TEST_ERROR);
    }

    @PostMapping("/error")
    public ResponseEntity<?> postError() {
        log.error("Simulating an error in POST request");
        throw new CommonException(TestExceptionCode.POST_TEST_ERROR);
    }
}
