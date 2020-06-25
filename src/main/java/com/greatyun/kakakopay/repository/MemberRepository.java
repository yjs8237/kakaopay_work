package com.greatyun.kakakopay.repository;

import com.greatyun.kakakopay.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member , Long> {

}
