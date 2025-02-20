package com.example.shop.service;

import com.example.shop.constant.Role;
import com.example.shop.dto.MemberDTO;
import com.example.shop.entity.Member;
import com.example.shop.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService, UserDetailsService {

    private final MemberRepository memberRepository;
    private ModelMapper modelMapper = new ModelMapper();
    private final PasswordEncoder passwordEncoder;

    @Override

    public String signUp(MemberDTO memberDTO) {

        //signup진입시 이메일로 가입여부 확인 먼저 실행!
        validateDuplcateMember(memberDTO.getEmail());

        //dto를 entity로 변환작업
        Member member = modelMapper.map(memberDTO, Member.class);

        member.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
        log.info("패스워드 반환값" +  member.getPassword());

        
        //유저일 경우에는 role을 유저로
        
        //어드민일 경우에는 role을 어드민으로 지정
        member.setRole(Role.ADMIN);
//        member.setRole(Role.USER);


        member =
            memberRepository.save(member);

        memberDTO = modelMapper.map(member, MemberDTO.class);


        return memberDTO.getName();
    }







    //회원가입이 이미 되어있는지 여부 확인용
    private void validateDuplcateMember(String email) {
        Member member =
                memberRepository.findByEmail(email);

        if (member != null) {
            log.info("이미 가입된 회원입니다.");
            throw new IllegalStateException("예외처리 ::이미 가입된 회원입니다.");
        } else {
            log.info("가입되지 않은 회원입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        //입력받은 email을 통해서 entity찾기

        Member member =
            memberRepository.findByEmail(email);

        if (member == null) {
            log.info("현재 입력하신 email로는 가입된 정보가 없습니다.");
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다."); //이건 차후에 try/catch 처리
        }
        log.info("로그인 시도하는 사람 : " + member);

        log.info("로그인 하는 사람의 권한 : " + member.getRole().name());

        //권한을 담을 변수
        String role = "";

        if (member.getRole().name().equals(Role.ADMIN.name())) {
            log.info("관리자 로그인 시도중...");
            //현재 로그인 시도하는 사람의 롤을 가져와서 name() 메소드로 toString값을
            //변수 role에 할당한다.
            role = member.getRole().name();

        }else {
            log.info("일반유저 로그인 시도중...");
            role = member.getRole().name();
        }

        //시큐리티에서 username는 사용자 이름이 아니라
        //인증을 하는 필드 email 또는 user_id등을 말한다.
        //디비에 있는 password 비밀버호를 userDetails 객체에 담아서 보내면
        //호출한 Controller에서 해당 객체를 받아 브라우저에서 입력한 비밀번호와 비교하여 로그인 시도
        UserDetails user =
        User.builder().username(
                 member.getEmail())
                .password(member.getPassword())
                .roles(role)
                .build();



        return user;
    }
}
