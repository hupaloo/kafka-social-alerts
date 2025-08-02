package dev.hupaloo.twitter.query;

import dev.hupaloo.configuration.MonitoredTopicsConfiguration;
import dev.hupaloo.configuration.TwitterQueryConfiguration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TwitterQueryBuilder {

    private final List<String> twitterSearchQueries;

    private static final int QUERY_CHARS_LIMIT = 512;
    private static final String QUERY_PREFIX = "(";
    private static final String QUERY_SUFFIX = ")";
    private static final String QUERY_KEYWORDS_SEPARATOR = " OR ";
    private static final String QUERY_KEYWORDS_AND_FILTERS_SEPARATOR = " ";

    public TwitterQueryBuilder(MonitoredTopicsConfiguration monitoredTopicsConfiguration,
                               TwitterQueryConfiguration twitterQueryConfiguration) {

        this.twitterSearchQueries = List.copyOf(buildAndGetTwitterSearchQueries(monitoredTopicsConfiguration,
                twitterQueryConfiguration));
    }

    public List<String> getTwitterSearchQueries() {
        return twitterSearchQueries;
    }

    private List<String> buildAndGetTwitterSearchQueries(MonitoredTopicsConfiguration monitoredTopicsConfiguration,
                                                         TwitterQueryConfiguration twitterQueryConfiguration) {

        String filters = twitterQueryConfiguration.getFilters().trim();
        List<String> monitoredTopics = monitoredTopicsConfiguration.getMonitoredTopics()
                .values()
                .stream()
                .flatMap(List::stream)
                .distinct()
                .map(this::escapeWhenMultipleWords)
                .toList();


        List<String> twitterSearchQueries = new ArrayList<>();
        StringBuilder searchQueryBuilder = new StringBuilder();

        for (String topic : monitoredTopics) {
            String prefix = !searchQueryBuilder.isEmpty() ? QUERY_KEYWORDS_SEPARATOR : "";
            String addition = prefix + topic;
            int projectedLength = QUERY_PREFIX.length() + searchQueryBuilder.length() + addition.length() +
                    QUERY_SUFFIX.length() + QUERY_KEYWORDS_AND_FILTERS_SEPARATOR.length() + filters.length();

            if (projectedLength > QUERY_CHARS_LIMIT) {
                twitterSearchQueries.add(buildAndGetSearchQuery(searchQueryBuilder, filters));
                searchQueryBuilder = new StringBuilder(topic);
            } else {
                searchQueryBuilder.append(addition);
            }
        }

        if (!searchQueryBuilder.isEmpty()) {
            twitterSearchQueries.add(buildAndGetSearchQuery(searchQueryBuilder, filters));
        }

        return twitterSearchQueries;
    }

    // query example: (apple OR iphone OR "vision pro") lang:en place_country:US -is:retweet has:images -is:reply
    private String buildAndGetSearchQuery(StringBuilder searchQueryBuilder, String filters) {
        return QUERY_PREFIX + searchQueryBuilder + QUERY_SUFFIX + QUERY_KEYWORDS_AND_FILTERS_SEPARATOR + filters;
    }

    private String escapeWhenMultipleWords(String keyword) {
        return keyword.contains(" ") ? "\"" + keyword + "\"" : keyword;
    }
}
