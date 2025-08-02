package dev.hupaloo.configuration;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties("twitter.query")
@Validated
@Getter
@Setter
public class TwitterQueryConfiguration {

    private String filters;

    private String fields;

    @NotNull
    @Max(100)
    @Min(10)
    private int tweetsNumber;
}
