package dev.hupaloo.controller;

import dev.hupaloo.service.MonitoredTopicsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TweetsCollectorController {

    private final MonitoredTopicsService monitoredTopicsService;

    @GetMapping("/monitoredTopics")
    public Map<String, List<String>> getMonitoredTopics() {
        return monitoredTopicsService.getMonitoredTopics();
    }
}
