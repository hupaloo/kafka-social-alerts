package dev.hupaloo.twitter.model.response;

import lombok.Data;

import java.util.List;

@Data
public class TwitterResponse {

    private List<TweetData> data;
    private TweetMetadata meta;
}
