package com.example.shop.service;

import com.example.shop.constant.OrderStatus;
import com.example.shop.dto.*;
import com.example.shop.entity.*;
import com.example.shop.exception.OutOfStockException;
import com.example.shop.repository.ItemRepository;
import com.example.shop.repository.MemberRepository;
import com.example.shop.repository.OrdersRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    public Long order(OrderDTO orderDTO, String email) {
        //참조될 아이템
        Item item = itemRepository.findById(orderDTO.getItemId()).orElseThrow(EntityNotFoundException::new);

        //참조될 회원
        Member member = memberRepository.findByEmail(email);



        Orders orders = new Orders();
        //부모인 orders set
        orders.setMember(member);                   //누구의 주문
        orders.setOrderStatus(OrderStatus.ORDER);   //주문상태

        //담을 아이템
        List<OrderItem> orderItemList = new ArrayList<>();

        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item); //입력받은 아이템
        orderItem.setCount(orderDTO.getCount()); //입력받은 주문수량
        orderItem.setOrderPrice(item.getPrice());

        orderItem.setOrders(orders);
        orderItemList.add(orderItem);

        if (item.getStockNumber() - orderDTO.getCount() < 0) {
            throw new OutOfStockException("상품 재고가 부족합니다. (현재수량 : " + item.getStockNumber() + ")");
        }

        //주문수량만큼 아이템수량 변경
        item.setStockNumber(item.getStockNumber() - orderDTO.getCount());



        orders.setOrderItems(orderItemList);        //오더아이템

        Orders ordersA =
            ordersRepository.save(orders);


        return ordersA.getId();

    }

    //상품 주문내역
    public ResponsePageDTO getOrderList(String email, RequestPageDTO requestPageDTO) {

        Page<Orders> ordersPage =
            ordersRepository.findOrders(email, requestPageDTO.getPageable("id"));

        List<Orders> ordersList=
                ordersPage.getContent();

        //주문목록 dto 변환

        List<OrderHistDTO> orderHistDTOList = new ArrayList<>();//뷰페이지로 가는 객체. 밑에 걸러진게 여기로 들어감


        for (Orders orders : ordersList) {
            OrderHistDTO orderHistDTO = new OrderHistDTO(orders);

            //주문아이템들을 꺼내와서 dto로 변환
            List<OrderItem> orderItemList = orders.getOrderItems();
            //꺼내온걸
            for (OrderItem entity : orderItemList) {
                //주문아이템의 아이템에 달려있는 이미지들을 가져와서
                List<ImgEntity> imgEntityList =
                        entity.getItem().getImgEntityList();
                //꺼내온걸~
                for (ImgEntity imgEntity : imgEntityList) {
                    //대표이미지라면 orderItemDTO로 변환
                    if (imgEntity.getRepImgYn() != null && imgEntity.getRepImgYn().equals("Y")) {
                        OrderItemDTO orderItemDTO =
                                new OrderItemDTO(entity, imgEntity.getImgName());

                        orderHistDTO.addOrderItemDTO(orderItemDTO);

                    }
                }

            }
            orderHistDTOList.add(orderHistDTO);


        }
        return new ResponsePageDTO(requestPageDTO, orderHistDTOList, (int)ordersPage.getTotalElements());


    }





}
