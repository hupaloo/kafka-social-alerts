package dev.hupaloo.service;

import dev.hupaloo.configuration.KafkaTopicsConfiguration;
import dev.hupaloo.kafka.TwitterKafkaProducer;
import dev.hupaloo.kafka.model.KafkaTweetPayload;
import dev.hupaloo.util.JacksonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaTweetPublisherService {

    private final TwitterKafkaProducer kafkaProducer;
    private final KafkaTopicsConfiguration topicsConfig;

    public void publishTweetsRaw(KafkaTweetPayload payload) {
        String topic = topicsConfig.getTweetsRaw();
        String json = JacksonUtils.toJson(payload);

        kafkaProducer.send(topic, "", json);
        log.info("Published tweet ID={} to topic={}", payload.getId(), topic);
    }
}
