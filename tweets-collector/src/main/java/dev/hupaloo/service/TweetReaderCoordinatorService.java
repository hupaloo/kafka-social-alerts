package dev.hupaloo.service;

import dev.hupaloo.kafka.model.KafkaTweetPayload;
import dev.hupaloo.twitter.model.response.TwitterResponse;
import dev.hupaloo.twitter.query.TwitterQueryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class TweetReaderCoordinatorService {

    private final TwitterQueryBuilder twitterQueryBuilder;
    private final TweetFetcherService tweetFetcherService;
    private final TweetProcessorService tweetProcessorService;
    private final KafkaTweetPublisherService kafkaTweetPublisherService;

    @Scheduled(fixedRateString = "#{@tweetsReaderConfiguration.intervalSeconds}", timeUnit = TimeUnit.SECONDS)
    public void readAndPublishMonitoredTopics() {
        List<String> twitterSearchQueries = twitterQueryBuilder.getTwitterSearchQueries();
        twitterSearchQueries.parallelStream().forEach(searchQuery -> {
            Optional<TwitterResponse> recentTweetsOpt = tweetFetcherService.fetch(searchQuery);

            recentTweetsOpt.ifPresent(twitterResponse -> {
                twitterResponse.getData().forEach(tweetData -> {
                    Optional<KafkaTweetPayload> kafkaTweetPayloadOpt = tweetProcessorService.process(tweetData);
                    kafkaTweetPayloadOpt.ifPresent(kafkaTweetPublisherService::publishTweetsRaw);
                });
            });
        });
    }
}
