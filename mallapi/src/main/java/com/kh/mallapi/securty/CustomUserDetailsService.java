package com.kh.mallapi.securty;

import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kh.mallapi.domain.Member;
import com.kh.mallapi.dto.MemberDTO;
import com.kh.mallapi.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//인증 인가 정책 가져온 데이터 
		Member member = memberRepository.getWithRoles(username);
		
		//해당된 이메일이 멤버 테이블에 없다.
		if (member == null) {
			throw new UsernameNotFoundException("Not Found");
		}
		
		//인증완료, 인가 부여 (User 객체에 등록)
		MemberDTO memberDTO = new MemberDTO(member.getEmail(), 
				member.getPw(), 
				member.getNickname(), 
				member.isSocial(),
				member.getMemberRoleList()
				.stream().map(memberRole -> memberRole.name()).collect(Collectors.toList()));
		log.info(memberDTO);
		return memberDTO;
	}

}
