package com.hansonwang99.serviceb;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ServiceBContorller {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CloseableHttpClient httpClient;


    @GetMapping("/serviceb")
    public String serviceb() {
        try {
            Thread.sleep( 3000 );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return restTemplate.getForObject("http://localhost:8883/servicec", String.class);
    }

    @GetMapping("servicebb")
    public String servicebb() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String name = jdbcTemplate.queryForObject( "SELECT name FROM user WHERE id = 1", String.class );
        return "Welcome " + name;
        //return "Welcome " + name;
    }

    @GetMapping("/httpclient")
    public String callHttpClient() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HttpGet get = new HttpGet("http://localhost:8883/serviceh");
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(get);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @GetMapping("servicebforbonus")
    public Map<String, Object> servicebforbonus() {
        Map<String, Object> result = new HashMap<>();
        result.put("key", "hello world");
        result.put("boolen", "1");
        String name = jdbcTemplate.queryForObject( "SELECT name FROM user_test WHERE id = 1", String.class );
        result.put("name", name);
        return result;
    }
}
