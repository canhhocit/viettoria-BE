package com.viettoria.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;

@Component
@ConditionalOnProperty(name = "app.keep-alive.enabled", havingValue = "true", matchIfMissing = true)
public class SelfKeepAliveScheduler {
    private static final Logger logger = LoggerFactory.getLogger(SelfKeepAliveScheduler.class);

    private final RestClient restClient;
    private final String pingUrl;

    public SelfKeepAliveScheduler(@Value("${app.keep-alive.url}") String pingUrl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(3));

        this.restClient = RestClient.builder().requestFactory(requestFactory).build();
        this.pingUrl = pingUrl;
    }

    @Scheduled(
            fixedDelayString = "${app.keep-alive.interval-ms:300000}",
            initialDelayString = "${app.keep-alive.initial-delay-ms:300000}"
    )
    public void pingSelf() {
        try {
            restClient.get().uri(pingUrl).retrieve().toBodilessEntity();
            logger.debug("Self keep-alive ping succeeded");
        } catch (RestClientException exception) {
            logger.warn("Self keep-alive ping failed; backend or database health check is unavailable");
        }
    }
}
