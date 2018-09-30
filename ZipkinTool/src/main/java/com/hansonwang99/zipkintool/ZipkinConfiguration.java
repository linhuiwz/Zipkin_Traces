package com.hansonwang99.zipkintool;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.http.DefaultSpanNameProvider;
import com.github.kristofa.brave.http.HttpRequest;
import com.github.kristofa.brave.http.SpanNameProvider;
import com.github.kristofa.brave.httpclient.BraveHttpRequestInterceptor;
import com.github.kristofa.brave.httpclient.BraveHttpResponseInterceptor;
import com.github.kristofa.brave.mysql.MySQLStatementInterceptorManagementBean;
import com.github.kristofa.brave.spring.BraveClientHttpRequestInterceptor;
import com.github.kristofa.brave.spring.ServletHandlerInterceptor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import zipkin.Span;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.Reporter;
import zipkin.reporter.Sender;
import zipkin.reporter.kafka08.KafkaSender;
import zipkin.reporter.okhttp3.OkHttpSender;
//import zipkin.reporter.okhttp3.OkHttpSender;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@ComponentScan(basePackages = "com.hansonwang99.zipkintool")
@Import({RestTemplate.class, BraveClientHttpRequestInterceptor.class, ServletHandlerInterceptor.class})
public class ZipkinConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private ZipkinProperties zipkinProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private BraveClientHttpRequestInterceptor clientInterceptor;

    @Autowired
    private ServletHandlerInterceptor serverInterceptor;

    @Bean
    public Sender sender() {
        //return OkHttpSender.create(zipkinProperties.getEndpoint());
        return KafkaSender.builder().bootstrapServers(zipkinProperties.getKafkaServers()).build();

    }

    @Bean
    public Reporter<Span> reporter() {
        return AsyncReporter.builder(sender()).build();
    }

    @Bean
    public Brave brave() {
        return new Brave.Builder(zipkinProperties.getService()).reporter(reporter()).build();
    }

    @Bean
    public SpanNameProvider spanNameProvider() {
        return new SpanNameProvider() {
            @Override
            public String spanName(HttpRequest httpRequest) {
                return String.format(
                        "%s %s",
                        httpRequest.getHttpMethod(),
                        httpRequest.getUri().getPath()
                );
            }
        };
    }

    @Bean
    public MySQLStatementInterceptorManagementBean mySQLStatementInterceptorManagementBean() {
        return new MySQLStatementInterceptorManagementBean(brave().clientTracer());
    }

    @PostConstruct
    public void init() {
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        interceptors.add(clientInterceptor);
        restTemplate.setInterceptors(interceptors);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(serverInterceptor);
    }

    @Bean
    public CloseableHttpClient httpClient(Brave brave) {
        return HttpClients.custom()
                .addInterceptorFirst(BraveHttpRequestInterceptor.create(brave))
                .addInterceptorFirst(BraveHttpResponseInterceptor.create(brave)).build();
    }
}
