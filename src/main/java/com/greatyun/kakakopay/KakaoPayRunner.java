package com.greatyun.kakakopay;

import com.greatyun.kakakopay.domain.Member;
import com.greatyun.kakakopay.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class KakaoPayRunner implements CommandLineRunner {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public void run(String... args) throws Exception {
        Member member = Member.builder()
                .name("jisang")
                .build();
        memberRepository.save(member);
    }
}
