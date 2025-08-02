package dev.hupaloo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.hupaloo.service.MonitoredTopicsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TweetsCollectorController.class)
@Import(TweetsCollectorControllerWebTest.MockConfig.class)
class TweetsCollectorControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MonitoredTopicsService monitoredTopicsService;

    @Test
    void should_return_monitored_topics() throws Exception {
        // given
        Map<String, List<String>> expectedMonitoredTopics = Map.of(
                "AAPL", List.of("apple", "iphone"),
                "TSLA", List.of("elon", "tesla")
        );

        // when
        when(monitoredTopicsService.getMonitoredTopics()).thenReturn(expectedMonitoredTopics);

        // then
        mockMvc.perform(get("/api/v1/monitoredTopics"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedMonitoredTopics)));
    }

    @TestConfiguration
    static class MockConfig {
        @Bean
        public MonitoredTopicsService monitoredTopicsService() {
            return mock(MonitoredTopicsService.class);
        }
    }
}
