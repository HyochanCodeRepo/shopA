package com.example.shop.dto;

import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestPageDTO {
    @Builder.Default
    private int page = 1; //기본적으로 1인데 빌더 디폴트를 사용해서 파라미터 수집 안될 시 1이 디폴트임


    @Builder.Default
    private int size = 10; //한페이지에 들어올 게시글 수

    private String type; //검색의 종류 t,c,w,tc,tw,twc
                         // 제목, 내용, 작성자, 제목&내용, 제목&작성자, 제목&내용&작성자 셀렉트 박스로
                         //if(type.equals("tc")) { 만약에 검색의 종류가 제목으로 검색한다면
                         //select * from table where title = :keyword or content :keyword}
    private String keyword; //검색어
    private String link; //a태그나 페이지 이동시 url을 쉽게 쓰기 위해서
    public String[] getTypes(){
        //검색을 위한 컬럼을 찾기위해서 사용
        if (type == null || type.isEmpty()) {
            return  null;
        }//현재 type의 데이터가 안들어왔다면 파라미터수집을 못했다면 null
        return type.split("");
        //현재 type가 tc라면 split메소드는 잘라서 배열에 담는 메소드
        //t, c 로 빈문자열로 잘라서 한 단어씩 넣는다.
    }

    //Pageable 페이징처리를 위한 Pageable을 만들어준다.
    public Pageable getPageable(String...props) {
        //컨트롤러에서 파라미터 수집한 page-1과 한페이지에 보여질 게시글수, 정렬("정렬컬럼"여러개).정렬순서
        return PageRequest.of(this.page - 1,this.size, Sort.by(props).descending());
    }


    public String getLink() {
        if (link == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("page=" + this.page);
            builder.append("&size=" + this.size);
            //page=3&size = 10

            if (type != null && type.length() > 0) {
                //검색시 제목 또는 내용 또는 기타 검색타입을 선택했다면
                //values 값이 있을것이고, 셀렉트박스 최상단에
                // !(-검색타입- value = "" 또는 넘어온데이터가 없다면)
                builder.append("&type=" + type);
            }
            if (keyword != null) {
                //검색 키워드도 수집이 되었다면
                //url 뒤에 &keyword = UTF-8로 인코딩된 검색어 입력
                try {
                    builder.append("&keyword=" + URLEncoder.encode(keyword, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    
                }

            }
            link = builder.toString(); //StringBuilder 객체로 만든 url을 toString으로 변환
        }
        return link;
    }
}
