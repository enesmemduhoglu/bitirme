package com.scrable.bitirme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BitirmeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BitirmeApplication.class, args);
    }

}
