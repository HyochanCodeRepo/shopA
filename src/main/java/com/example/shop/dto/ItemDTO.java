package com.example.shop.dto;

import com.example.shop.constant.ItemSellStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ItemDTO {

    private Long id; //상품코드 pk

    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String itemNm; //상품명

    @NotNull(message = "가격은 필수입니다.")
    @PositiveOrZero(message = "가격은 0보다 커야합니다.")
    private int price; //가격

    @NotBlank(message = "상품 상세설명은 필수 입력 값입니다.")
    private String itemDetail; //상품 설명

    @NotNull(message = "재고는 필수입니다.")
    @PositiveOrZero(message = "가격은 0보다 커야합니다.")
    private int stockNumber; //재고 수량

    //상품 판매상태 대기      판매중/품절
    ItemSellStatus itemSellStatus;
    private LocalDateTime regTime;
    private LocalDateTime updateTime;

    private String createdBy;
}
