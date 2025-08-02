package dev.hupaloo.twitter.matcher;

import dev.hupaloo.configuration.MonitoredTopicsConfiguration;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TweetKeywordsMatcher {

    private final MonitoredTopicsConfiguration monitoredTopicsConfiguration;

    public List<String> getMatchedKeywords(String tweetText) {
        return monitoredTopicsConfiguration.getMonitoredTopics()
                .values()
                .stream()
                .flatMap(List::stream)
                .distinct()
                .filter(topic -> StringUtils.containsIgnoreCase(tweetText, topic))
                .toList();
    }

    public List<String> getMatchedTopics(List<String> matchedKeywords) {
        return monitoredTopicsConfiguration.getMonitoredTopics()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue()
                        .stream()
                        .anyMatch(matchedKeywords::contains))
                .map(Map.Entry::getKey)
                .toList();
    }
}
