package dev.hupaloo.configuration;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties("twitter.api")
@Validated
@Getter
@Setter
public class TwitterApiConfiguration {

    @NotNull
    private String bearerToken;
}
