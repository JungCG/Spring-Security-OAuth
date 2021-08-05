package com.cos.security1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 된다.
@EnableGlobalMethodSecurity(securedEnabled=true, prePostEnabled = true) // secured 어노테이션 활성화, @Secured와 함께 사용하여 컨트롤러 메소드에 권한을 설정하고 싶을 때 사용 / prePostEnabled : PreAuthorize, PostAuthorize 어노테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	
	// 해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다.
	@Bean
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests()
		.antMatchers("/user/**").authenticated() // 인증만 되면 들어갈 수 있는 주소
		.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
		.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
		.anyRequest().permitAll()
		.and()
		.formLogin()
		.loginPage("/loginForm")
		.loginProcessingUrl("/login") // /login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해준다. -> login Controller를 만들지 않아도 된다.
		.defaultSuccessUrl("/")
		.and()
		.oauth2Login()
		.loginPage("/loginForm") // 구글 로그인이 완료된 뒤의 후처리가 필요함. Tip. 승인 코드만 X, (엑세스 토큰 + 사용자 프로필 정보를 한번에 O)
		.userInfoEndpoint()
		.userService(principalOauth2UserService); // 후처리되는 함수 : /config/oauth/PrincipalOauth2UserService 에 loadUser
		
		// 1. 코드 받기(인증), 2. 엑세스 토큰(권한)
		// 3. 사용자프로필 정보를 가져오고
		// 4-1. 그 정보를 토대로 회원가입을 자동으로 진행시키기도 함
		// 4-2. 프로필 정보가 부족할 경우, 추가적인 회원가입 창을 띄움
	}
	
}
