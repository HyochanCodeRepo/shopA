package com.example.shop.repository;

import com.example.shop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("select c from Cart c join Member m on m.num = c.member.num where m.email=:email")
    public Cart selectEmail(String email);

    public Cart findByMemberEmail(String email);



}
