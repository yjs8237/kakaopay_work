package com.greatyun.kakakopay.repository;

import com.greatyun.kakakopay.domain.Money;
import com.greatyun.kakakopay.domain.MoneyResult;
import com.greatyun.kakakopay.enumuration.EnumFinishYn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MoneyResultRepository extends JpaRepository<MoneyResult , Long> {

    Page<MoneyResult> findAllByMoney_PkidAndFinishYn(Long moneyId , EnumFinishYn enumFinishYn , Pageable pageable);

    Optional<MoneyResult> findByMoneyAndRecvMemberId(Money money , Long memberId);

    List<MoneyResult> findAllByMoneyAndFinishYn(Money money , EnumFinishYn enumFinishYn);

}
