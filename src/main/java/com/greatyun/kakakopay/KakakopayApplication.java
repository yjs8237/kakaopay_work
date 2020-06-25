package com.greatyun.kakakopay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing  // JPA LocalDateTime 자동 등록 및 수정될 수 있도록 추가
public class KakakopayApplication {

    public static void main(String[] args) {
        SpringApplication.run(KakakopayApplication.class, args);
    }

}
