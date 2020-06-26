package com.greatyun.kakakopay.repository;

import com.greatyun.kakakopay.domain.Money;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoneyRepository extends JpaRepository<Money , Long> {
}
