package com.example.shop.service;

import com.example.shop.constant.OrderStatus;
import com.example.shop.dto.*;
import com.example.shop.entity.*;
import com.example.shop.exception.OutOfStockException;
import com.example.shop.repository.CartItemRepository;
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
    private final CartItemRepository cartItemRepository;

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

    //주문취소
    public void cancelOrder(Long orderId) {
        
        //pk로 주문취소하려는 주문을 불러오기
        Orders orders =
            ordersRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

        //주문상태를 취소 상태로 변경!
        if (orders.getOrderStatus() == OrderStatus.ORDER) {
            orders.setOrderStatus(OrderStatus.CANCEL);
        }

        //취소되는 주문아이템들의 수량만큼 재고에 더해준다
        List<OrderItem> orderItemList = orders.getOrderItems();

        for (OrderItem orderItem : orderItemList) {
//            orderItem.getCount(); //주문수량
//            orderItem.getItem().getStockNumber(); //재고수량
            orderItem.getItem().setStockNumber(
                    orderItem.getItem().getStockNumber()
                    +orderItem.getCount()
            );
        }
        
    }


    //주문유효성 검사
    //자신이 주문한 내역인지 확인하는 메소드
    public boolean validateOrder(Long orderId, String email) {
        Member member =
            memberRepository.findByEmail(email); //로그인한사람 사람 찾아오기


        Orders orders =
            ordersRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new); //주문목록을 찾아온다

        Member saveMember =
            orders.getMember(); //주문 참조하는 회원(구매자)

        //현재 로그인 사용자와 주문의 참조하는 회원이 같지 않다면
        if (!member.getEmail().equals(saveMember.getEmail())) {
            return false;
        }
        return true;
    }

    public void orders(List<Long> cartItemIdList, String email) {
        //참조될 회원찾기
        Member member = memberRepository.findByEmail(email);

        List<OrderItem> orderItemList = new ArrayList<>();

        //주문을 만들자
        Orders orders = new Orders(); //주문
        orders.setMember(member); //주문이 참조하는 회원
        orders.setOrderStatus(OrderStatus.ORDER); //주문상태
        
        //아이템을 찾기위해서 CartItem을 찾자
        for (Long cartItemId : cartItemIdList) {
            CartItem cartItem =
                cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);
            Item item =
                cartItem.getItem();//카트아이템이 참조하는 아이템

            //주문아이템 entity만들기
            OrderItem orderItem = new OrderItem();
            //참조하는 아이템
            orderItem.setItem(item);
            //주문수량은 카트아이템의 수량
            orderItem.setCount(cartItem.getCount());
            //주문가격
            orderItem.setOrderPrice(item.getPrice());


            //참조하는 주문
            orderItem.setOrders(orders);

            //주문을 넣었따면 카트의 아이템은 비워주자
            cartItemRepository.delete(cartItem);
            
            //주문리스트에 넣어준다
            orderItemList.add(orderItem);

            //아이템의 재고수량을 변경해야한다.
            //아이템을 장바구니에 넣었을때는 산게 아니고 아이템을 주문하기를 한다면
            //기존 재고를 변경해야한다.
            item.orderStockNumber(cartItem.getCount());
            //entity의 값이 변경이 되었기 때문에 update 수행



        }
        orders.setOrderItems(orderItemList);

        ordersRepository.save(orders);

    }




}
