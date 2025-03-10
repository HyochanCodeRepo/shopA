package com.example.shop.repository;

import com.example.shop.entity.Board;
import com.example.shop.entity.QBoard;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch{

    public BoardSearchImpl() {
        super(Board.class); //어떤 entity를 가지고 만들것이냐? 테이블에 해당하는 entity
    }

    @Override
    public Page<Board> search1(Pageable pageable) {
        //샘플
        //기능은 쿼리문이 잘 실행되는지 확인
        //조건은 직접입력, 파라미터 받지 않음
        QBoard board = QBoard.board;
        JPQLQuery<Board> query = from(board); //select * from board 란 뜻임
        System.out.println("쿼리문 : " + query);


        if (false) {
            //select * from board + 조건에 따라서 쿼리문을 작성한다.
            query.where(board.title.contains("1")); //where title like %1%
            System.out.println("쿼리문 : " + query);
        }





        query.fetch(); //구문 실행 excute query
        System.out.println("총 row수 : " + query.fetchCount());

        return null;
    }

    @Override
    public Page<Board> search(String[] types, String keyword, Pageable pageable) {

        //select * from board
        QBoard board = QBoard.board;
        JPQLQuery<Board> query = from(board);
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
                if (type.equals("t")) {
                    booleanBuilder.or(board.title.contains(keyword));
                }else if(type.equals("c")) {
                    booleanBuilder.or(board.content.contains(keyword));
                }else if(type.equals("w")) {
                    booleanBuilder.or(board.member.name.contains(keyword));
                }
            }
            //위에 만들어진 booleanBuilder를 가지고
            query.where(booleanBuilder);

        }
        System.out.println("쿼리문 where추가 : " + query);
        //추가적인 쿼리문
        //and num > 0; // 그냥 넣어봄 *and 예제임
        query.where(board.num.gt(0L));
        System.out.println("쿼리문 where추가 : " + query);

        //페이징
        this.getQuerydsl().applyPagination(pageable, query);

        //리스트
        List<Board> boardList=
            query.fetch();
        //총게시물
        long count =
            query.fetchCount();

        return new PageImpl<>(boardList, pageable, count);
    }

}
