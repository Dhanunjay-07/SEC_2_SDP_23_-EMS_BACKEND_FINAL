package com.election.evm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.net.URI;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/")
    public ResponseEntity<Void> redirectRoot() {
        // Automatically redirect to the Vercel frontend for project evaluators
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("https://emsfsad.vercel.app"))
                .build();
    }

    @GetMapping({"/health", "/api/health"})
    public Map<String, String> health() {
        return Map.of(
                "status", "ok",
                "service", "EVM Backend"
        );
    }
}
