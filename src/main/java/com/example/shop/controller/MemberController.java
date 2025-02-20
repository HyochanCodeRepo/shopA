package com.example.shop.controller;

import com.example.shop.dto.MemberDTO;
import com.example.shop.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/user")
public class MemberController {

    private final MemberService memberService;


    @GetMapping("/signUp")
    public String signUp(MemberDTO memberDTO){

        return "user/signUp";

    }

    @PostMapping("/signUp")
    public String signUpPost(@Valid MemberDTO memberDTO, BindingResult bindingResult){
        log.info("회원가입 포스트 진입");
        log.info(memberDTO);

        //에러 있으면 어디로 보낼래~
        if (bindingResult.hasErrors()) {
            log.info("유효성 검사 에러 발생!");
            log.info(bindingResult.getAllErrors());

            return "user/signUp";

        }


        try {
            memberService.signUp(memberDTO);

        } catch (IllegalStateException e) {
            e.printStackTrace();
            return "user/signUp";
        }

        return "redirect:/user/signUp";

    }

    @GetMapping("/login")
    public String login(){


        return "user/login";

    }



}
