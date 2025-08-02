package dev.hupaloo.twitter.model.response;

import lombok.Data;

@Data
public class TweetMetadata {

    private String newestId;
    private String oldestId;
    private int resultCount;
    private String nextToken;
}
