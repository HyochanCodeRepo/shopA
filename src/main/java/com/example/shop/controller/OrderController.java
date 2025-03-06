package com.example.shop.controller;

import com.example.shop.dto.OrderDTO;
import com.example.shop.dto.OrderHistDTO;
import com.example.shop.dto.RequestPageDTO;
import com.example.shop.dto.ResponsePageDTO;
import com.example.shop.exception.OutOfStockException;
import com.example.shop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/order")
    public ResponseEntity order(@RequestBody @Valid OrderDTO orderDTO, BindingResult bindingResult, Principal principal) {
        log.info("주문하기 post진입 ");
        log.info("주문하기 post진입 ");
        log.info("주문하기 post진입 ");
        log.info("주문하기 post진입 ");

        log.info("들어온 값 체크 :" + orderDTO);

        if (bindingResult.hasErrors()) {
            log.info("유효성검사 count가 없다면");
            StringBuilder sb = new StringBuilder();
            //"홍길동" + "홍길동 append를 통해서 문자열을 추가적으로 저장 가능

            List<FieldError> fieldErrorList =
                    bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrorList) {
                sb.append(fieldError.getDefaultMessage());
            }
            log.info(sb.toString());

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);

        }

        if (principal == null) {
            log.info("로그인 안되있음");
            return new ResponseEntity<String>("", HttpStatus.UNAUTHORIZED); //차후에~
        }

        //주문을 하려면 부모인 주문엔티티 필요, 주문엔티티는 회원과 1:1 , 이메일로 주문 찾아오기
        String email = principal.getName();

        Long orderId = null;

        //수량부족에 대한 예외처리
        try {
            //주문아이템의 부모인 아이템 -> 입력받은 itemid 로 해결
            orderId = orderService.order(orderDTO, email);
        } catch (OutOfStockException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }


        return new ResponseEntity<Long>(orderId, HttpStatus.OK);


    }

    //requestMapping에서는 쿼리스트링으로 받았는데 이제 url로 받아서 주소에 변수로 넘김
    @GetMapping({"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page,
                            Principal principal, Model model, RequestPageDTO requestPageDTO) {
        //페이지 url로 받은 값이 없다면 1, 있다면 받는 값 할당
        requestPageDTO.setPage(page.isPresent() ? page.get() : 1);

        log.info("현재 페이지는 ? :" + (page.isPresent() ? page.get() : 1));

        //만약에 로그인이 안되어있다면 리다이렉트 하던가, 페이지 자체가 로그인이 되어야 한다.
        if (principal == null) {
            return "redirect:/";
        }
        ResponsePageDTO<OrderHistDTO> responsePageDTO =
                orderService.getOrderList(principal.getName(), requestPageDTO);

//        responsePageDTO.getDtoList().forEach(orderHistDTO -> log.info(orderHistDTO));

        model.addAttribute("responsePageDTO", responsePageDTO);

        return "order/orderHist";
    }

    @PostMapping("/order/{orderId}/cancel")
    public ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId, Principal principal) {

        if (!orderService.validateOrder(orderId, principal.getName())) {


            return new ResponseEntity<String>("주문취소 권한이 없습니다.", HttpStatus.FORBIDDEN);

        }

        log.info("받은 주문번호 " + orderId);
        log.info("받은 주문번호 " + orderId);
        log.info("받은 주문번호 " + orderId);
        log.info("받은 주문번호 " + orderId);

        orderService.cancelOrder(orderId);

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }




}
