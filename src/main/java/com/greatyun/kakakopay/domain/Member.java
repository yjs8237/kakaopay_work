package com.greatyun.kakakopay.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tb_member")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class Member extends BaseEntity{

    private String name;

}
