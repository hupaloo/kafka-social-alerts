package dev.hupaloo.twitter.model.response;

import lombok.Data;

@Data
public class TweetPublicMetrics {

    private int retweetCount;
    private int replyCount;
    private int likeCount;
    private int quoteCount;
    private int bookmarkCount;
    private int impressionCount;
}
