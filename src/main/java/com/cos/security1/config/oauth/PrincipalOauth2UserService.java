package com.cos.security1.config.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRository;
	
	// 구글로부터 받은 userRequest 데이터에 대한 후처리되는 함수
	// 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		// 서버의 기본 정보, registrationId로 어떤 OAuth로 로그인 했는지 확인 가능.
		System.out.println("getClientRegistration : "+userRequest.getClientRegistration());
		
		System.out.println("getAccessToken : "+userRequest.getAccessToken().getTokenValue());
		
		OAuth2User oauth2User = super.loadUser(userRequest);
		// 구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인을 완료 -> code를 리턴(OAuth-Client 라이브러리 -> AccessToken 요청)
		// userRequest 정보 -> loadUser 함수 호출 -> 구글로부터 회원 프로필 받아준다.
		
		// sub, name, given_name, family_name, picture, email, email_verified, locale 정보가 담겨있음.
		// 위 정보를 가지고 회원가입을 바로 진행
		// username = "google_{sub}"
		// password = "암호화(임의의 값)" <- 아이디 비밀번호를 입력해서 로그인을 진행하는 것이 아니기 때문
		// email = {email}
		// role = ROLE_USER
		System.out.println("getAttributes : "+oauth2User.getAttributes());
		
		// 회원가입 강제 진행
		String provider = userRequest.getClientRegistration().getClientId(); // google
		String providerId = oauth2User.getAttribute("sub");
		String username = provider+"_"+providerId; // google_~~~~
		String password = bCryptPasswordEncoder.encode("구글회원가입");
		String email = oauth2User.getAttribute("email");
		String role = "ROLE_USER";
		
		User userEntity = userRository.findByUsername(username);
		
		if(userEntity == null) {
			System.out.println("구글 로그인이 최초입니다.");
			userEntity = User.builder()
					.username(username)
					.password(password)
					.email(email)
					.role(role)
					.provider(provider)
					.providerId(providerId)
					.build();
			
			userRository.save(userEntity);
		}else {
			System.out.println("구글 로그인을 이미 한적이 있습니다. 당신은 자동회원가입이 되어 있습니다.");
		}
		
		return new PrincipalDetails(userEntity, oauth2User.getAttributes());
	}
}
