package dev.hupaloo.twitter.state;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class TweetsSinceIdTracker {

    private final AtomicLong sinceId = new AtomicLong(0);

    public long getSinceId() {
        return sinceId.get();
    }

    public void setSinceId(long sinceId) {
        this.sinceId.set(sinceId);
    }
}
