package com.example.shop.controller;


import com.example.shop.dto.ItemDTO;
import com.example.shop.dto.RequestPageDTO;
import com.example.shop.dto.ResponsePageDTO;
import com.example.shop.entity.Item;
import com.example.shop.entity.Member;
import com.example.shop.repository.ItemRepository;
import com.example.shop.repository.MemberRepository;
import com.example.shop.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/admin/item")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/register")
    public String register(ItemDTO itemDTO, Principal principal) {


        return "item/register";
    }

    @PostMapping("/register")
    public String registerPost(@Valid ItemDTO itemDTO, BindingResult bindingResult,
                               RedirectAttributes redirectAttributes, MultipartFile mainimg, MultipartFile[] multipartFile, Model model) {

        //들어온 파일 보기 로그
        if (mainimg != null) {
            log.info(mainimg);
            log.info(mainimg.getOriginalFilename());
            log.info(mainimg.getSize());

        }

        //들어온 파일 보기 로그
        if (multipartFile != null) {
            for (MultipartFile a : multipartFile) {
                log.info(a);
                log.info(a.getOriginalFilename());
                log.info(a.getSize());

            }
        }

        log.info("아이템 등록 post : " + itemDTO);

        if (bindingResult.hasErrors()) {
            log.info("아이템 유효성검사간 에러");
            log.info(bindingResult.getAllErrors());

            return "item/register";
        }


        //대표이미지는 꼭 있어야한다라는 조건.
        if (mainimg.isEmpty()) {

            model.addAttribute("msg", "대표이미지는 꼭 있어야 합니다.");

            return "item/register";
        }

        //본문저장
        try {
            itemDTO =
                    itemService.register(itemDTO, mainimg, multipartFile);

        } catch (IOException e) {
            model.addAttribute("msg", "이미지 저장이 실패했습니다.");

            return "item/register";

        }


        redirectAttributes.addFlashAttribute("itemNm", itemDTO.getItemNm());

        return "redirect:/admin/item/list";
    }

    @GetMapping("/list")
    public String list(RequestPageDTO requestPageDTO, Model model, Principal principal) {


        //아이템 서비스에서 list불러오기
        ResponsePageDTO<ItemDTO> responsePageDTO =
                itemService.list(principal.getName(), requestPageDTO);

        //model을 통해서 데이터 보내기
        model.addAttribute("responsePageDTO", responsePageDTO);


        //뷰파일 열기
        return "item/list";


    }

    @GetMapping("/read")
    public String read(Long id, Model model, Principal principal, RedirectAttributes redirectAttributes) {

        log.info("상품정보 페이지");
        log.info("상품정보 페이지");
        log.info("상품정보 페이지");

        if (id == null) {
            return "redirect:/admin/item/list";
        }

        try {
            //정보가져오기
            ItemDTO itemDTO =
                    itemService.read(id);

            //가져온 상품보기
            log.info("상품정보 : " + itemDTO);
            log.info("상품정보 : " + itemDTO);

            if (!itemDTO.getCreatedBy().equals(principal.getName())) {
                log.info("리스트로 이동");
                return "redirect:/admin/item/list";
            }

            model.addAttribute("itemDTO", itemDTO);

            return "item/read";

        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 상품입니다.");
            return "redirect:/admin/item/list";

        }


    }

    @GetMapping("/update")
    public String updateGet(Long id, Model model, Principal principal,
                            RedirectAttributes redirectAttributes) {

        log.info("상품정보 페이지");
        if (id == null) {
            return "redirect:/admin/item/list";
        }

        //정보가져오기
        try {
            ItemDTO itemDTO =
                    itemService.read(id);

            //가져온 상품 보기
            log.info("상품정보" + itemDTO);

            if (!itemDTO.getCreatedBy().equals(principal.getName())) {
                log.info("리스트로 이동");
                return "redirect:/admin/item/list";
            }

            model.addAttribute("itemDTO", itemDTO);

            return "item/update";
        } catch (EntityNotFoundException e) {

            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 상품입니다.");
            return "redirect:/admin/item/list";
        }


    }

    @PostMapping("/update")
    public String updatePost(ItemDTO itemDTO, MultipartFile[] multipartFile, MultipartFile mainimg, Long[] delino) {

        log.info("아ㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣ");

        System.out.println("=====================================");

        if (!mainimg.isEmpty()) {
            log.info(mainimg.getOriginalFilename());
        } else {
            log.info("등록된 메인이미지가 없습니다.");
        }

        if (multipartFile != null) {
            for (MultipartFile file : multipartFile) {

                if (!file.isEmpty()) {
                    log.info("들어온 상세이미지");
                    log.info(file.getOriginalFilename());
                }

            }
        } else {
            log.info("등록된 메인이미지가 없습니다.");
        }

        System.out.println("=====================================");


        if (delino != null && delino.length != 0) {
            log.info(Arrays.toString(delino));
        }
        System.out.println("=====================================");


        log.info(itemDTO);
        log.info(itemDTO);

        //서비스에서 수정
        try {
            itemService.update(itemDTO, multipartFile, mainimg, delino);

        } catch (IOException e) {
            e.printStackTrace();
            log.info("이미지 수정 실패!");
            //redirect attribute 추가할 예정 msg 이미지 수정 실패
            return "redirect:/admin/item/read?id=" + itemDTO.getId();
        }

        return "redirect:/admin/item/read?id=" + itemDTO.getId();

    }
}
