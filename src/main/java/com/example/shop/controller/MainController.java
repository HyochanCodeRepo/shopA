package com.example.shop.controller;

import com.example.shop.dto.ItemDTO;
import com.example.shop.dto.RequestPageDTO;
import com.example.shop.dto.ResponsePageDTO;
import com.example.shop.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MainController {

    private final ItemService itemService;


    @GetMapping("/")
    public String mainPage(RequestPageDTO requestPageDTO, Model model){
        ResponsePageDTO<ItemDTO> responsePageDTO =
            itemService.mainlist(requestPageDTO);
        model.addAttribute("responsePageDTO", responsePageDTO);



        return "main";
    }

    @GetMapping("/item/read")
    public String read(RequestPageDTO requestPageDTO, Long id, Model model, RedirectAttributes redirectAttributes) {

        //id를 통해서 상품정보 가져오기
        if (id == null) {
            return "redirect:/";
        }

        try {
            //정보가져오기
            ItemDTO itemDTO =
                    itemService.read(id);
            //가져온 상품 보기
            log.info("상품정보 " + itemDTO);


            model.addAttribute("itemDTO", itemDTO);

            return "item/itemOrder";

        } catch (EntityNotFoundException e) {

            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 상품입니다.");
            return "redirect:/";

        }


    }


}
