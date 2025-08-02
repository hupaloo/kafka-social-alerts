package dev.hupaloo.twitter.client;

import dev.hupaloo.configuration.TwitterApiConfiguration;
import dev.hupaloo.configuration.TwitterQueryConfiguration;
import dev.hupaloo.exception.twitter.TwitterApiErrorType;
import dev.hupaloo.exception.twitter.TwitterApiException;
import dev.hupaloo.twitter.model.response.TwitterResponse;
import dev.hupaloo.twitter.util.TwitterTimeUtils;
import dev.hupaloo.util.JacksonUtils;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@Profile("prod")
public class TwitterClient implements TwitterApi {

    private static final String BASE_URL = "https://api.twitter.com/2/";
    private static final String SEARCH_TWEETS_ENDPOINT = "tweets/search/recent";

    private final WebClient webClient;
    private final TwitterApiConfiguration twitterApiConfiguration;
    private final TwitterQueryConfiguration twitterQueryConfiguration;

    public TwitterClient(TwitterApiConfiguration twitterApiConfiguration,
                         TwitterQueryConfiguration twitterQueryConfiguration) {

        this.twitterApiConfiguration = twitterApiConfiguration;
        this.twitterQueryConfiguration = twitterQueryConfiguration;
        this.webClient = getWebClient();
    }

    public TwitterResponse getRecentTweets(String searchQuery, long sinceId, Instant windowStart, Instant windowEnd) {
        UriComponentsBuilder searchTweetsUriBuilder = UriComponentsBuilder.fromUriString(SEARCH_TWEETS_ENDPOINT)
                .queryParam("query", searchQuery)
                .queryParam("max_results", twitterQueryConfiguration.getTweetsNumber())
                .queryParam("tweet.fields", twitterQueryConfiguration.getFields())
                .queryParam("start_time", TwitterTimeUtils.instantToFormattedString(windowStart))
                .queryParam("end_time", TwitterTimeUtils.instantToFormattedString(windowEnd));
        if (sinceId != 0) {
            searchTweetsUriBuilder.queryParam("since_id", sinceId);
        }

        URI searchTweetsUri = searchTweetsUriBuilder.build()
                .encode()
                .toUri();

        String responseString = webClient.get()
                .uri(searchTweetsUri)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> handleErrorResponse(response,
                        "GET " + searchTweetsUri))
                .bodyToMono(String.class)
                .block();
        return JacksonUtils.fromJson(responseString, TwitterResponse.class);
    }

    private Mono<Throwable> handleErrorResponse(ClientResponse response, String requestSummary) {
        int statusCode = response.statusCode().value();
        return response.bodyToMono(String.class)
                .flatMap(errorBody -> Mono.error(new TwitterApiException(
                        TwitterApiErrorType.fromHttpStatus(HttpStatus.resolve(statusCode)),
                        statusCode,
                        requestSummary,
                        errorBody)
                ));
    }

    private WebClient getWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofSeconds(5))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(5, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(5, TimeUnit.SECONDS))
                );

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + twitterApiConfiguration.getBearerToken())
                .baseUrl(BASE_URL)
                .build();
    }
}
