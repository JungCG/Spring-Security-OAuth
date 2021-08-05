package com.cos.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

//@Controller : View를 리턴하겠다
@Controller
public class IndexController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/test/login")
	public @ResponseBody String testLogin(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails) { // DI (의존성 주입)
		System.out.println("/test/login ===========");
		// 세션 유저 정보-1. 다운 케스팅
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		System.out.println("authentication : "+principalDetails.getUser());
		
		// 세션 유저 정보-2. @AuthenticationPrincipal 사용
		System.out.println("userDetails : "+userDetails.getUser());
		return "세션 정보 확인하기";
	}
	
	@GetMapping("/test/oauth/login")
	public @ResponseBody String testOAuthLogin(Authentication authentication, @AuthenticationPrincipal OAuth2User oauth) { // DI (의존성 주입)
		System.out.println("/test/oauth/login ===========");
		// 세션 유저 정보-1. 다운 케스팅
		OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
		System.out.println("authentication : "+oauth2User.getAttributes());
		System.out.println("oauth2User : "+oauth.getAttributes());
		return "OAuth 세션 정보 확인하기";
	}
	
	@GetMapping({"","/"})
	public String index() {
		// mustache : 기본 폴더가 src/main/resoureces/
		// 뷰 리졸버 설정 : templates (prefix), .mustache (suffix)
		// src/main/resources/templates/index.mustache
		// -> config/WebMvcConfig.java 에서 Mustache 재설정
		return "index";
	}
	
	// OAuth 로그인을 해도 PrincipalDetails
	// 일반 로그인을 해도 PrincipalDetails
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("principalDetails : "+principalDetails.getUser());
		return "user";
	}
	
	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}
	
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}
	
	// "/login" 이라고 하면 스프링 시큐리티가 해당 주소를 낚아채버림
	// SecurityConfig 파일 생성 후 작동 안함.
	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}
	
	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}
	
	@PostMapping("/join")
	public String join(User user) {
		user.setRole("ROLE_USER");
//		userRepository.save(user); // 회원가입이 되지만 비밀번호 : 1234 => 시큐리티로 로그인할 수 없음. 이유는 패스워드가 암호화가 안되었기 때문에
		
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		userRepository.save(user);
		
		// redirect를 붙이면 /loginForm 으로 이동
		return "redirect:/loginForm";
	}
	
	// @Secured : 간단하게 메소드에 권한을 설정하고 싶을 때 사용
	@Secured("ROLE_ADMIN")
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인정보";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "데이터정보";
	}
}
