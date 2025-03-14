package com.example.shop.service;

import com.example.shop.dto.ImgDTO;
import com.example.shop.entity.ImgEntity;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@Log4j2
public class FileService {

    @Value("${imgLocation}")
    String imgLocation;

    //사진 물리적으로 저장
    public ImgDTO register(MultipartFile multipartFile) throws IOException {
        //사진을 물리적으로 저장 후 저장한 내용을 바탕으로 dto 만들어서 반환
            log.info(multipartFile.getOriginalFilename());


            String oriImgName = multipartFile.getOriginalFilename()
                    .substring(multipartFile.getOriginalFilename().lastIndexOf("/") + 1);
            //짱구.png
            log.info("확장자를 포함한 실제 이미지명 : " + oriImgName);

            //uuid를 포함해서 파일명이 겹치지 않도록 만들기
        //서로 다른 객체를 구별하기 위해서 이름을 부여할때 사용
        //실제 사용시 중복될 가능성이 거의 없기 때문에
        UUID uuid = UUID.randomUUID();
        //물리적인 파일이름
        String imgName = uuid.toString() + "_" + oriImgName;

        String fileuploadpath = imgLocation + imgName;
        //물리적인 저장
        FileOutputStream fos = new FileOutputStream(fileuploadpath);

        fos.write(multipartFile.getBytes());

        fos.close();

        ImgDTO imgDTO = new ImgDTO();
        imgDTO.setOriImgName(oriImgName);
        imgDTO.setImgName(imgName);
        imgDTO.setImgUrl(imgLocation);

        return imgDTO;

    }

    public void del(String path) {
        //입력받은 데이터로 물리적 파일 삭제

        //파일의 경로

        //파일 삭제
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }


    }
    
}
