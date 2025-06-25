package example.apilimittest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiLimitController {
    @GetMapping("/api/limit")
    public ResponseEntity<?> getApiLimit() {
        return ResponseEntity.ok("API limit reached");
    }
}
