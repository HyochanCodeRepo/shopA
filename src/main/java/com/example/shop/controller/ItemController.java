package com.example.shop.controller;


import com.example.shop.dto.ItemDTO;
import com.example.shop.dto.RequestPageDTO;
import com.example.shop.dto.ResponsePageDTO;
import com.example.shop.entity.Item;
import com.example.shop.entity.Member;
import com.example.shop.repository.ItemRepository;
import com.example.shop.repository.MemberRepository;
import com.example.shop.service.ItemService;
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

import java.security.Principal;

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
                               RedirectAttributes redirectAttributes, MultipartFile[] multipartFile) {

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


        itemDTO =
                itemService.register(itemDTO);

        redirectAttributes.addFlashAttribute("itemNm", itemDTO.getItemNm());

        return "redirect:/admin/item/list";
    }

    @GetMapping("/list")
    public String list(RequestPageDTO requestPageDTO, Model model,Principal principal) {
        
        
        //아이템 서비스에서 list불러오기
        ResponsePageDTO<ItemDTO> responsePageDTO =
            itemService.list(principal.getName(), requestPageDTO);
        
        //model을 통해서 데이터 보내기
        model.addAttribute("responsePageDTO", responsePageDTO);


        
        //뷰파일 열기
        return "item/list";
        
        
    }



}
