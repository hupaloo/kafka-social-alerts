package dev.hupaloo.kafka.model;

import dev.hupaloo.twitter.model.response.TweetData;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class KafkaTweetPayload {

    private String id;
    private String text;
    private String lang;
    private Instant createdAt;
    private String countryCode;
    private List<String> matchedTopics;
    private List<String> matchedKeywords;

    public static KafkaTweetPayload toKafkaTweetPayload(TweetData tweetData,
                                                        List<String> matchedTopics,
                                                        List<String> matchedKeywords) {

        KafkaTweetPayloadBuilder kafkaTweetPayloadBuilder = KafkaTweetPayload.builder()
                .id(tweetData.getId())
                .text(tweetData.getText())
                .lang(tweetData.getLang())
                .createdAt(tweetData.getCreatedAt())
                .matchedTopics(matchedTopics)
                .matchedKeywords(matchedKeywords);

        if (tweetData.getPlace() != null) {
            kafkaTweetPayloadBuilder.countryCode(tweetData.getPlace().getCountryCode());
        }

        return kafkaTweetPayloadBuilder.build();
    }
}
