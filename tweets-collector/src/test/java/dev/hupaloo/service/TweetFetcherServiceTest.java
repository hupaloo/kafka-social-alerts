package dev.hupaloo.service;

import dev.hupaloo.exception.twitter.TwitterApiErrorType;
import dev.hupaloo.exception.twitter.TwitterApiException;
import dev.hupaloo.twitter.client.TwitterApi;
import dev.hupaloo.twitter.model.response.TweetData;
import dev.hupaloo.twitter.model.response.TweetMetadata;
import dev.hupaloo.twitter.model.response.TwitterResponse;
import dev.hupaloo.twitter.state.TweetsSinceIdTracker;
import dev.hupaloo.twitter.state.TweetsTimeWindowTracker;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TweetFetcherServiceTest {

    @Mock
    private TwitterApi twitterApi;

    @Mock
    private TweetsTimeWindowTracker timeWindowTracker;

    @Mock
    private TweetsSinceIdTracker sinceIdTracker;

    private TweetFetcherService tweetFetcherService;

    @BeforeEach
    void setUp() {
        tweetFetcherService = new TweetFetcherService(twitterApi, timeWindowTracker, sinceIdTracker);
    }

    @Test
    void should_return_expected_twitter_response() {
        // given
        String searchQuery = "inflation";
        Instant from = Instant.parse("2025-08-02T10:00:00Z");
        Instant to = Instant.parse("2025-08-02T10:05:00Z");
        long sinceId = 12345L;

        TweetMetadata metadata = new TweetMetadata();
        metadata.setNewestId(String.valueOf(sinceId));

        TweetData tweet = TweetData.builder()
                .id("1947707000000000005")
                .text("Interest rates have remained unchanged.")
                .lang("en")
                .createdAt(Instant.parse("2025-08-02T10:02:00Z"))
                .build();

        TwitterResponse expectedResponse = new TwitterResponse();
        expectedResponse.setMeta(metadata);
        expectedResponse.setData(List.of(tweet));

        when(timeWindowTracker.getNextWindow()).thenReturn(Pair.of(from, to));
        when(sinceIdTracker.getSinceId()).thenReturn(sinceId);
        when(twitterApi.getRecentTweets(searchQuery, sinceId, from, to)).thenReturn(expectedResponse);

        // when
        Optional<TwitterResponse> actual = tweetFetcherService.fetch(searchQuery);

        // then
        assertTrue(actual.isPresent());
        assertEquals(expectedResponse, actual.get());
        verify(sinceIdTracker, never()).setSinceId(anyLong());
    }

    @Test
    void should_fetch_tweets_and_update_since_id() {
        // given
        String searchQuery = "java";
        Instant from = Instant.parse("2025-08-02T10:00:00Z");
        Instant to = Instant.parse("2025-08-02T10:05:00Z");
        long previousSinceId = 1000L;
        long newSinceId = 2000L;

        TweetMetadata metadata = new TweetMetadata();
        metadata.setNewestId(String.valueOf(newSinceId));

        TwitterResponse response = new TwitterResponse();
        response.setData(List.of(mock(TweetData.class)));
        response.setMeta(metadata);

        when(timeWindowTracker.getNextWindow()).thenReturn(Pair.of(from, to));
        when(sinceIdTracker.getSinceId()).thenReturn(previousSinceId);
        when(twitterApi.getRecentTweets(searchQuery, previousSinceId, from, to)).thenReturn(response);

        // when
        Optional<TwitterResponse> result = tweetFetcherService.fetch(searchQuery);

        // then
        assertTrue(result.isPresent());
        assertEquals(response, result.get());
        verify(sinceIdTracker).setSinceId(newSinceId);
    }

    @Test
    void should_return_empty_when_twitter_api_fails() {
        // given
        String searchQuery = "fail";
        Instant from = Instant.now();
        Instant to = from.plusSeconds(60);
        long sinceId = 1000L;

        when(timeWindowTracker.getNextWindow()).thenReturn(Pair.of(from, to));
        when(sinceIdTracker.getSinceId()).thenReturn(sinceId);
        when(twitterApi.getRecentTweets(searchQuery, sinceId, from, to))
                .thenThrow(new TwitterApiException(TwitterApiErrorType.SERVER_ERROR, 500, "", ""));

        // when
        Optional<TwitterResponse> result = tweetFetcherService.fetch(searchQuery);

        // then
        assertTrue(result.isEmpty());
        verify(sinceIdTracker, never()).setSinceId(anyLong());
    }

    @Test
    void should_not_update_since_id_if_newest_id_is_older() {
        // given
        String searchQuery = "taxes";
        Instant from = Instant.now();
        Instant to = from.plusSeconds(60);
        long currentSinceId = 2000L;
        long returnedSinceId = 1000L;

        TweetMetadata metadata = new TweetMetadata();
        metadata.setNewestId(String.valueOf(returnedSinceId));

        TwitterResponse response = new TwitterResponse();
        response.setData(List.of(mock(TweetData.class)));
        response.setMeta(metadata);

        when(timeWindowTracker.getNextWindow()).thenReturn(Pair.of(from, to));
        when(sinceIdTracker.getSinceId()).thenReturn(currentSinceId);
        when(twitterApi.getRecentTweets(searchQuery, currentSinceId, from, to)).thenReturn(response);

        // when
        Optional<TwitterResponse> result = tweetFetcherService.fetch(searchQuery);

        // then
        assertTrue(result.isPresent());
        assertEquals(response, result.get());
        verify(sinceIdTracker, never()).setSinceId(anyLong());
    }
}
