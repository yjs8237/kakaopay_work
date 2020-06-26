package com.greatyun.kakakopay.domain;

import com.greatyun.kakakopay.domain.base.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 뿌리기 금액 받은 결과를 나타내는 엔티티
 */
@Entity
@Table(name = "tb_money")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class MoneyResult extends BaseEntity {

    private String name;

}
