package com.hansonwang99.servicec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class ServiceCContorller {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/servicec")
    public String servicec() {
//        try {
//            Thread.sleep( 3000 );
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return "Now, we reach the terminal call: servicec !";
        String name = jdbcTemplate.queryForObject( "SELECT name FROM user WHERE id = 1", String.class );
        return "Welcome " + name;
    }

    @GetMapping("/mysqltest")
    public String mysqlTest() {
        String name = jdbcTemplate.queryForObject( "SELECT name FROM user WHERE id = 1", String.class );
        return "Welcome " + name;
    }

    @GetMapping("serviceh")
    public String serviceh() {
        return "hello world";
    }

    @PostMapping("/testRestPost")
    public String handlePostFromImooc() {
        try {
            Thread.sleep( 1000 );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return restTemplate.getForObject("http://localhost:8882/servicebb", String.class);
    }

    @PostMapping("/testFromBonus")
    public Map handlePostFromBonus() {
        try {
            Thread.sleep( 1000 );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return restTemplate.getForObject("http://localhost:8882/servicebforbonus", Map.class);
    }
}
