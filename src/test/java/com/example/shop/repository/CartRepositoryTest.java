package com.example.shop.repository;

import com.example.shop.entity.Cart;
import com.example.shop.entity.Member;
import groovy.transform.Pure;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class CartRepositoryTest {

    @Autowired
    CartRepository cartRepository;
    @Autowired
    MemberRepository memberRepository;


    @Test
    public void insertTest(){
        //부모인 회원테이블에서 특정 회원을 가져와서 set해준다
        //그걸 통해서 부모를 가지고 있게된다.
//        Member member =
//            memberRepository.findById(6L).get();
        Member member =
                memberRepository.findByEmail("hyo@a.a");

        //참조값을 넣으면 참조하는거고 없으면 null로 들어간다



        Cart cart = new Cart();
        cart.setMember(member);
        cartRepository.save(cart);

    }

    @Test
    public void findByMemberEmail(){
        //부모인 회원테이블에서 특정 회원을 가져와서 set해준다
        //그걸 통해서 부모를 가지고 있게된다.

        Cart cart =
            cartRepository.selectEmail("hyo@a.a");

        if (cart == null) {
            log.info("장바구니 만들수 있음");
        } else {
            log.info("장바구니 못만듬");
        } 
        
        

    }

    @Test
    @Transactional
    public void findById(){

        Cart cart =
            cartRepository.findById(1L).get();

        log.info(cart.getMember());



    }



}