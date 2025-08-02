package dev.hupaloo.service;

import dev.hupaloo.exception.twitter.TwitterApiException;
import dev.hupaloo.twitter.client.TwitterApi;
import dev.hupaloo.twitter.model.response.TwitterResponse;
import dev.hupaloo.twitter.state.TweetsSinceIdTracker;
import dev.hupaloo.twitter.state.TweetsTimeWindowTracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TweetFetcherService {

    private final TwitterApi twitterClient;
    private final TweetsTimeWindowTracker tweetsTimeWindowTracker;
    private final TweetsSinceIdTracker tweetsSinceIdTracker;

    public Optional<TwitterResponse> fetch(String searchQuery) {
        Optional<TwitterResponse> recentTweetsOpt;

        try {
            Pair<Instant, Instant> timeWindow = tweetsTimeWindowTracker.getNextWindow();
            long sinceId = tweetsSinceIdTracker.getSinceId();

            TwitterResponse recentTweets = twitterClient.getRecentTweets(searchQuery, sinceId, timeWindow.getLeft(), timeWindow.getRight());

            if (recentTweets != null
                    && recentTweets.getMeta() != null
                    && recentTweets.getMeta().getNewestId() != null) {
                long newestId = Long.parseLong(recentTweets.getMeta().getNewestId());
                if (newestId > tweetsSinceIdTracker.getSinceId()) {
                    tweetsSinceIdTracker.setSinceId(newestId);
                }
            }

            // todo: add metrics reporting
            recentTweetsOpt = Optional.ofNullable(recentTweets);
        } catch (TwitterApiException e) {
            log.error(
                    "Twitter API request failed: type={} status={} request={} response={}",
                    e.getErrorType(),
                    e.getStatusCode(),
                    e.getRequestSummary(),
                    e.getResponseBody(),
                    e
            );
            // todo: add metrics reporting
            recentTweetsOpt = Optional.empty();
        }

        return recentTweetsOpt;
    }
}
