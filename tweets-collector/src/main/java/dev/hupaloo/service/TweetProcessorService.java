package dev.hupaloo.service;

import dev.hupaloo.kafka.model.KafkaTweetPayload;
import dev.hupaloo.twitter.matcher.TweetKeywordsMatcher;
import dev.hupaloo.twitter.model.response.TweetData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TweetProcessorService {

    private final TweetKeywordsMatcher tweetKeywordsMatcher;

    public Optional<KafkaTweetPayload> process(TweetData tweetData) {
        List<String> matchedKeywords = tweetKeywordsMatcher.getMatchedKeywords(tweetData.getText());

        if (matchedKeywords.isEmpty()) {
            // todo: add metrics tracking for unmatched tweets
            log.warn("No matched keywords for tweet ID: {}, text: {}", tweetData.getId(), tweetData.getText());
            return Optional.empty();
        }

        List<String> matchedTopics = tweetKeywordsMatcher.getMatchedTopics(matchedKeywords);

        if (matchedTopics.isEmpty()) {
            // todo: add metrics tracking for unmatched tweets
            log.warn("No matched topics for tweet ID: {}, matched keywords: {}", tweetData.getId(), matchedKeywords);
            return Optional.empty();
        }

        KafkaTweetPayload kafkaTweetPayload = KafkaTweetPayload.toKafkaTweetPayload(
                tweetData,
                matchedTopics,
                matchedKeywords
        );

        return Optional.of(kafkaTweetPayload);
    }
}
