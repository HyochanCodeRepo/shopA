package com.example.shop.entity;

import com.example.shop.constant.ItemSellStatus;
import com.example.shop.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@Table(name = "item")
public class Item extends BaseEntity {
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //상품코드 pk

    @Column(nullable = false, length = 50)
    private String itemNm; //상품명
    @Column(name = "price", nullable = false)
    private int price; //가격
    @Column(nullable = false)
    private int stockNumber; //재고 수량
    @Lob
    @Column(nullable = false)
    private String itemDetail; //상품 설명

    //상품 판매상태 대기      판매중/품절
    @Enumerated(EnumType.STRING)
    ItemSellStatus itemSellStatus;

    //    private LocalDateTime regTime;
//    private LocalDateTime updateTime;
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<ImgEntity> imgEntityList;
}
