package dev.hupaloo.service;

import dev.hupaloo.configuration.MonitoredTopicsConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MonitoredTopicsService {

    private final MonitoredTopicsConfiguration monitoredTopicsConfiguration;

    public Map<String, List<String>> getMonitoredTopics() {
        return monitoredTopicsConfiguration.getMonitoredTopics();
    }
}
