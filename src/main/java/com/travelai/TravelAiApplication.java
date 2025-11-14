package com.travelai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.travelai.mapper")
public class TravelAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelAiApplication.class, args);
    }

}
