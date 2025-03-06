package com.example.shop.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CartDetailDTO {

    private Long cartItemId; //장바구니 상품 아이디
    private String itemNm; //상품명
    private int price; //상품 가격
    private int count; //상품수량
    private String imgName; //상품의 이름, 경로 /images/로 대체


}
