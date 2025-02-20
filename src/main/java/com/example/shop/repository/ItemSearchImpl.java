package com.example.shop.repository;

import com.example.shop.constant.ItemSellStatus;
import com.example.shop.entity.Item;
import com.example.shop.entity.QItem;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ItemSearchImpl extends QuerydslRepositorySupport implements ItemSearch{
    /**
     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
     *
     * @param domainClass must not be {@literal null}.
     */
    public ItemSearchImpl() {
        super(Item.class); //어떤 entity를 가지고 만들것이냐? 테이블에 해당하는 entity
    }



    @Override
    public Page<Item> search(String[] types, String keyword,String email, Pageable pageable) {

        //select * from board
        QItem item = QItem.item;
        JPQLQuery<Item> query = from(item);
        System.out.println("쿼리문 1 : " + query);

        //types에 따른 where 문

        if ((types != null && types.length > 0) && keyword != null) {
            //검색조건에 따라서 검색 타입이 있고, 검색어가 있다면
            //조건에 따라서 booleanBuilder에 추가
            BooleanBuilder booleanBuilder = new BooleanBuilder();

            //types를 가지고 배열의 값들을 가져와서 조회 후(쿼리문작성) 추가
            //tw > {t,w} > 반복문에서 t 한번 박복, w한번 반복
            for (String type : types) {
                //가져온 타입에 따라
                if (type.equals("p")) {
                    booleanBuilder.or(item.price.gt(Integer.parseInt(keyword)));
                }else if(type.equals("n")) {
                    booleanBuilder.or(item.itemNm.contains(keyword));
                }else if(type.equals("d")) {
                    booleanBuilder.or(item.itemDetail.contains(keyword));
                }else if(type.equals("s")) {
                    booleanBuilder.or(item.itemSellStatus.eq(ItemSellStatus.valueOf(keyword)));
                }
            }
            //위에 만들어진 booleanBuilder를 가지고
            query.where(booleanBuilder);

        }
        System.out.println("쿼리문 where추가 : " + query);
        //추가적인 쿼리문
        //and num > 0; // 그냥 넣어봄 *and 예제임
        query.where(item.id.gt(0L));
        query.where(item.createdBy.eq(email));
        System.out.println("쿼리문 where추가 : " + query);

        //페이징
        this.getQuerydsl().applyPagination(pageable, query);

        //리스트
        List<Item> itemList=
            query.fetch();
        //총게시물
        long count =
            query.fetchCount();

        return new PageImpl<>(itemList, pageable, count);
    }

}
