package com.starterkit.springboot;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.starterkit.springboot")
@EnableJpaRepositories(basePackages = "com.starterkit.springboot")
public class SalAveiroApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalAveiroApplication.class, args);
    }
}
