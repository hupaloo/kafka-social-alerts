package dev.hupaloo.configuration;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties("twitter")
@Validated
@Getter
@Setter
public class MonitoredTopicsConfiguration {

    @NotEmpty
    private Map<String, List<String>> monitoredTopics;
}
