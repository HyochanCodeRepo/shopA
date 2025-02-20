package com.example.shop.service;

import com.example.shop.dto.ItemDTO;
import com.example.shop.dto.RequestPageDTO;
import com.example.shop.dto.ResponsePageDTO;
import com.example.shop.entity.Item;
import com.example.shop.repository.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class ItemService {
    //싱글톤패턴
    private final ItemRepository itemRepository;
    ModelMapper modelMapper = new ModelMapper();


    //등록
    public ItemDTO register(ItemDTO itemDTO) {
        Item item = modelMapper.map(itemDTO, Item.class);

        //이런식으로 해도됨
//        itemDTO =
//                modelMapper.map(itemRepository.save(item), ItemDTO.class);


        item = itemRepository.save(item);
        itemDTO = modelMapper.map(item, ItemDTO.class);

        return itemDTO;


    }

    public ResponsePageDTO<ItemDTO> list(String email,RequestPageDTO requestPageDTO) {

        //정렬조건
        Pageable pageable =
            requestPageDTO.getPageable("id");
        Page<Item> itemPage =
            itemRepository.search(requestPageDTO.getTypes(), requestPageDTO.getKeyword(), email, pageable);

        //Item으로 변환
        List<Item> itemList = itemPage.getContent();

        //dto로 변환
        List<ItemDTO> itemDTOList =itemList.stream()
                .map(item -> modelMapper.map(item, ItemDTO.class))
                .collect(Collectors.toList());

        itemDTOList.forEach(itemDTO -> log.info(itemDTO ));

        //총 게시글 수
        int total = (int) itemPage.getTotalElements();

        //ResponsePageDTO 타입으로 생성자 생성
        ResponsePageDTO<ItemDTO> responsePageDTO = new ResponsePageDTO<>(requestPageDTO, itemDTOList, total);


        return responsePageDTO;

    }
}
