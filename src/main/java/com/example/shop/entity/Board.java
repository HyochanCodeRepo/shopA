package com.example.shop.entity;

import com.example.shop.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Table(name = "tbl_board")
@NoArgsConstructor
@Builder//new를 쓰지않고 static으로 new를 써서 반환해줌
@AllArgsConstructor
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_num")
    private Long num;

    @Column(length = 50, nullable = false)//컬럼길이, null허용여부
    private String title;

    @Column(length = 2000, nullable = true)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num")
    private Member member;




}
