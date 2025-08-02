package dev.hupaloo.twitter.client.mock;

import dev.hupaloo.twitter.client.TwitterApi;
import dev.hupaloo.twitter.model.response.TwitterResponse;
import dev.hupaloo.util.FileReaderUtils;
import dev.hupaloo.util.JacksonUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Profile("local")
public class FakeTwitterClient implements TwitterApi {

    private final AtomicInteger recentTweetsCounter;
    private final List<TwitterResponse> twitterResponses;

    private static final List<String> FAKE_TWITTER_RESPONSE_FILE_PATHS = List.of(
            "fake_data/twitter-api-fake-response-1.json",
            "fake_data/twitter-api-fake-response-2.json");

    public FakeTwitterClient() {
        this.recentTweetsCounter = new AtomicInteger(0);
        this.twitterResponses = getFakeTwitterResponses();
    }

    @Override
    public TwitterResponse getRecentTweets(String searchQuery, long sinceId, Instant windowStart, Instant windowEnd) {
        int counter = recentTweetsCounter.incrementAndGet();

        TwitterResponse fakeTwitterResponse = twitterResponses.get(counter % 2);
        fakeTwitterResponse.getData()
                .forEach(tweetData -> tweetData.setCreatedAt(windowStart));

        return fakeTwitterResponse;
    }

    private static List<TwitterResponse> getFakeTwitterResponses() {
        return FAKE_TWITTER_RESPONSE_FILE_PATHS.stream()
                .map(fakeTwitterResponseFilePath -> {
                    String fakeTwitterResponseFileContent = FileReaderUtils.readFileContent(fakeTwitterResponseFilePath);
                    return JacksonUtils.fromJson(fakeTwitterResponseFileContent, TwitterResponse.class);
                })
                .toList();
    }
}
