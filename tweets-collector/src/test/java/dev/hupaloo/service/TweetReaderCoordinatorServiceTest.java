package dev.hupaloo.service;

import dev.hupaloo.kafka.model.KafkaTweetPayload;
import dev.hupaloo.twitter.model.response.TweetData;
import dev.hupaloo.twitter.model.response.TwitterResponse;
import dev.hupaloo.twitter.query.TwitterQueryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TweetReaderCoordinatorServiceTest {

    @Mock
    private TwitterQueryBuilder queryBuilder;

    @Mock
    private TweetFetcherService fetcher;

    @Mock
    private TweetProcessorService processor;

    @Mock
    private KafkaTweetPublisherService publisher;

    private TweetReaderCoordinatorService coordinator;

    @BeforeEach
    void setUp() {
        coordinator = new TweetReaderCoordinatorService(queryBuilder, fetcher, processor, publisher);
    }

    @Test
    void shouldFetchProcessAndPublishTweets() {
        // given
        String query = "inflation";
        TweetData tweetData = TweetData.builder().id("1").text("Inflation is high").build();
        KafkaTweetPayload payload = KafkaTweetPayload.toKafkaTweetPayload(tweetData, List.of("Inflation"), List.of("inflation"));
        TwitterResponse response = new TwitterResponse();
        response.setData(List.of(tweetData));

        when(queryBuilder.getTwitterSearchQueries()).thenReturn(List.of(query));
        when(fetcher.fetch(query)).thenReturn(Optional.of(response));
        when(processor.process(tweetData)).thenReturn(Optional.of(payload));

        // when
        coordinator.readAndPublishMonitoredTopics();

        // then
        verify(fetcher).fetch(query);
        verify(processor).process(tweetData);
        verify(publisher).publishTweetsRaw(payload);
    }
}
