package com.example.aram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableConfigurationProperties
@EnableFeignClients
public class AramApplication {

    public static void main(String[] args) {
        SpringApplication.run(AramApplication.class, args);
    }

}
