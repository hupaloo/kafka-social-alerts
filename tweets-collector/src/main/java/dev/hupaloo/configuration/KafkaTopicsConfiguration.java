package dev.hupaloo.configuration;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties("kafka.topics")
@Validated
@Getter
@Setter
public class KafkaTopicsConfiguration {

    @NotNull
    private String tweetsRaw;
}
