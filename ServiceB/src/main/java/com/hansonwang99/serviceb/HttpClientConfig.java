package com.hansonwang99.serviceb;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class HttpClientConfig {

    @Bean
    @ConditionalOnMissingBean({HttpClient.class})
    public CloseableHttpClient httpClient() {
        return HttpClients.createDefault();
    }
}
