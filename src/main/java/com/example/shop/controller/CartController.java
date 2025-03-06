package com.example.shop.controller;


import com.example.shop.dto.CartDetailDTO;
import com.example.shop.dto.CartItemDTO;
import com.example.shop.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class CartController {
    private final CartService cartService;

    //ResponseEntity 붙이면 REST 방식
    @PostMapping("/cart")
    public ResponseEntity order(@Valid CartItemDTO cartItemDTO
            , BindingResult bindingResult
            , Principal principal) {

        log.info(cartItemDTO);
        log.info(cartItemDTO);
        log.info(cartItemDTO);
        log.info(cartItemDTO);

        if (bindingResult.hasErrors()) {
            log.info("장바구니 유효성검사 에러");
            log.info(bindingResult.getAllErrors());

            List<FieldError> fieldErrorList = bindingResult.getFieldErrors();

            StringBuilder stringBuilder = new StringBuilder();

            for (FieldError error : fieldErrorList) {
                //StringBuilder 객체에 에러 메세지를 담는다.
                stringBuilder.append(error.getDefaultMessage());
            }
            //입력된 에러를 다시 보여주기위해 반환값으로 에러 내용을 반환해준다.
            return new ResponseEntity<String>(stringBuilder.toString(), HttpStatus.BAD_REQUEST);

        }
        //로그아웃되어서 principal이 null
        if (principal == null) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        String email = principal.getName();
        Long cartItemId = null;
        try {
            cartItemId =
                    cartService.addCart(cartItemDTO, email);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }


    @GetMapping("/cart")
    public String cartHist(Principal principal, Model model) {

//        List<CartDetailDTO> cartDetailDTOList =
//            cartService.getCartList(principal.getName());
//
//        model.addAttribute("cartDetailDTOList", cartDetailDTOList);
        //상과 동일하지만 줄여서 이렇게 씀
        model.addAttribute("cartDetailDTOList", cartService.getCartList(principal.getName()));

        return "cart/cartList";

    }

    @PatchMapping("/cartItem/{cartItemId}/{count}")
    public ResponseEntity updateCount(
            @PathVariable("cartItemId") Long cartItemId,
            @PathVariable("count") Long count,Principal principal){

        log.info("장바구니 아이템 번호 : " +cartItemId);
        log.info("장바구니 아이템 번호 : " +cartItemId);
        log.info(cartItemId);
        log.info(cartItemId);

        log.info("수량 :" +  count);
        log.info(count);
        log.info(count);
        if (count <= 0) {
            return new ResponseEntity<String>("최소 1개이상 담아주세요",
                    HttpStatus.BAD_REQUEST);
        } else{

        }
        
        //카트 상품에 대한 접근 제한해야됨 //접근이 가능하다면 DB에 장바구니 아이템 대한 수량을 변경한다.



        return new ResponseEntity<String>(HttpStatus.OK);
    }







}
