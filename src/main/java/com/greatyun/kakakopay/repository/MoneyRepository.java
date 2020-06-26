package com.greatyun.kakakopay.repository;

import com.greatyun.kakakopay.domain.Member;
import com.greatyun.kakakopay.domain.Money;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MoneyRepository extends JpaRepository<Money , Long> {

    Optional<Money> findByRoomIdAndToken(Long roomId , String token);

    Optional<Money> findByRoomIdAndTokenAndMember(Long roomId , String token , Member member);



}
