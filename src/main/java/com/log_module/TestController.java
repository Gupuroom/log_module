package com.log_module;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/logging/test")
public class TestController {

    @GetMapping
    public ResponseEntity<?> getTest(@RequestParam(value = "userId", required = false) String userId) {
        log.info("Received GET request with userId: {}", userId);

        return ResponseEntity.ok("Why do we use it It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).");
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
        throw new RuntimeException("Simulating an error in GET request");
    }

    @PostMapping("/error")
    public ResponseEntity<?> postError() {
        log.error("Simulating an error in POST request");
        throw new RuntimeException("Simulating an error in POST request");
    }
}
