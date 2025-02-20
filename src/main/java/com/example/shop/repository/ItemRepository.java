package com.example.shop.repository;

import com.example.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemSearch {
    //읽기 기능 조건 : 상품명으로 검색

    public List<Item> findByItemNm(String itemNm);

    //가격으로 검색
    public List<Item> findByPriceGreaterThanEqual(int price);


    //자신이 판매하고 있는 상품 목록 보기
    public List<Item> findByCreatedBy(String email);
    //상품명과 상품상세설명으로 검색

    public List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);

    //like문 이용해서 상품명 or 상세설명 포함된 글자 해당되는 제품찾기
    public List<Item> findByItemNmContainingOrItemDetailContaining(String itemNm, String itemDetail);

    //상과 동일
    @Query("select i from Item i where i.itemNm like '%'||:itemNm||'%' or i.itemDetail like '%'||:itemDetail||'%'")
    public List<Item> selectItemNmItemDetail(String itemNm, String itemDetail);



    //상과 동일하지만 keyword 한개로 두개 쓸수있음, 마이바티스에서는 @Param썼었음
    @Query("select i from Item i where i.itemNm like '%'||:keyword||'%' or i.itemDetail like '%'||:keyword||'%'")
    public List<Item> selectItemNmItemDetail(String keyword);






}
