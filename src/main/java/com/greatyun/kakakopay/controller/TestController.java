package com.greatyun.kakakopay.controller;

import com.greatyun.kakakopay.domain.Member;
import com.greatyun.kakakopay.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class TestController {

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/")
    public ResponseEntity test() {
        List<Member> members = memberRepository.findAll();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(members);
    }
}
