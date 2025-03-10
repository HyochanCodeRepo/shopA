package com.example.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder//new를 쓰지않고 static으로 new를 써서 반환해줌
@AllArgsConstructor
public class BoardDTO {

    private Long num;

//    @NotNull //null (x)
//    @NotEmpty //null(x) "" 빈문자열 x
//    @NotBlank //null(x) " " 스페이스공백 x
    @NotBlank(message = "제목은 필수로 작성해주세요")
    @Size(min = 3, max = 50, message = "최소 3~50로 작성")
    private String title;

    @NotBlank(message = "잘써라")
    @Size(min = 2, max = 2000, message = "2~2000까지 작성가능")
    private String content;

    @NotBlank(message = "작성자 꼭 써야함")
    @Size(min = 1, max = 20, message = "20자 이내로 작성")
    private String writer;

    //이미지 들어올 자리 생성
    @Builder.Default
    List<ImgDTO> imageDTOList = new ArrayList<>();

    private LocalDateTime regDate; //등록일자
    private LocalDateTime modDate; //수정일자

}
