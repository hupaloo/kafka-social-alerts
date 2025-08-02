package dev.hupaloo.twitter.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Data
@Builder
@Jacksonized
public class TweetData {

    private String id;
    private String text;
    private String lang;
    private Instant createdAt;
    private TweetPlace place;
    private TweetPublicMetrics publicMetrics;
}
