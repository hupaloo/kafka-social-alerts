package dev.hupaloo.service;

import dev.hupaloo.kafka.model.KafkaTweetPayload;
import dev.hupaloo.twitter.matcher.TweetKeywordsMatcher;
import dev.hupaloo.twitter.model.response.TweetData;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TweetProcessorServiceTest {

    @Mock
    private TweetKeywordsMatcher tweetKeywordsMatcher;

    private TweetProcessorService tweetProcessorService;

    @BeforeEach
    void setUp() {
        tweetProcessorService = new TweetProcessorService(tweetKeywordsMatcher);
    }

    @Test
    void should_return_tweet_payload_for_matched_tweet() {
        // given
        TweetData tweetData = TweetData.builder()
                .id("1947707000000000005")
                .text("Interest rates have remained unchanged. The Fed seems to be in wait-and-see mode.")
                .lang("en")
                .createdAt(Instant.parse("2025-08-02T10:39:20Z"))
                .build();
        List<String> matchedKeywords = List.of("interest rates");
        List<String> matchedTopics = List.of("Inflation");

        KafkaTweetPayload expectedPayload = KafkaTweetPayload.toKafkaTweetPayload(
                tweetData, matchedTopics, matchedKeywords
        );

        when(tweetKeywordsMatcher.getMatchedKeywords(tweetData.getText())).thenReturn(matchedKeywords);
        when(tweetKeywordsMatcher.getMatchedTopics(matchedKeywords)).thenReturn(matchedTopics);

        // when
        Optional<KafkaTweetPayload> actual = tweetProcessorService.process(tweetData);

        // then
        assertTrue(actual.isPresent());
        assertEquals(expectedPayload, actual.get());
    }

    @Test
    void shouldReturnEmptyWhenNoKeywordsMatch() {
        // given
        TweetData tweetData = TweetData.builder()
                .id("1947706999999999999")
                .text("Interest rates have remained unchanged. The Fed seems to be in wait-and-see mode.")
                .lang("en")
                .createdAt(Instant.parse("2025-08-02T10:40:00Z"))
                .build();

        when(tweetKeywordsMatcher.getMatchedKeywords(tweetData.getText())).thenReturn(List.of());

        // when
        Optional<KafkaTweetPayload> actual = tweetProcessorService.process(tweetData);

        // then
        assertTrue(actual.isEmpty());
        verify(tweetKeywordsMatcher, never()).getMatchedTopics(any());
    }
}
