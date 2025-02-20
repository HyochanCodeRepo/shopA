package com.example.shop.repository;

import com.example.shop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    //email로 정보 찾기, pk로 찾을수 있지만 session 에서는 기본적으로 email을 저장되기 때문에
    public Member findByEmail(String email);
}
