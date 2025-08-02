package dev.hupaloo.twitter.state;

import dev.hupaloo.configuration.TweetsReaderConfiguration;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class TweetsTimeWindowTracker {

    private final Duration windowDelaySeconds;
    private final Duration windowIntervalSeconds;

    public TweetsTimeWindowTracker(TweetsReaderConfiguration tweetsReaderConfiguration) {
        this.windowDelaySeconds = Duration.ofSeconds(tweetsReaderConfiguration.getDelaySeconds());
        this.windowIntervalSeconds = Duration.ofSeconds(tweetsReaderConfiguration.getIntervalSeconds());
    }

    public Pair<Instant, Instant> getNextWindow() {
        Instant now = Instant.now();
        long alignedEpoch = now.getEpochSecond() / windowIntervalSeconds.getSeconds() * windowIntervalSeconds.getSeconds();
        Instant windowEnd = Instant.ofEpochSecond(alignedEpoch).minus(windowDelaySeconds);
        Instant windowStart = windowEnd.minus(windowIntervalSeconds);

        return Pair.of(windowStart, windowEnd);
    }
}
