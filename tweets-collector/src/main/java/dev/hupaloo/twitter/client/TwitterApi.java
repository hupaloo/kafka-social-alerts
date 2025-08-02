package dev.hupaloo.twitter.client;

import dev.hupaloo.twitter.model.response.TwitterResponse;

import java.time.Instant;

public interface TwitterApi {

    TwitterResponse getRecentTweets(String searchQuery, long sinceId, Instant windowStart, Instant windowEnd);
}
