package com.example.webservice_spring.config.auth;

import com.example.webservice_spring.config.auth.dto.OAuthAttributes;
import com.example.webservice_spring.config.auth.dto.SessionUser;
import com.example.webservice_spring.domain.user.User;
import com.example.webservice_spring.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException
    {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        //로그인 진행중인 서비스 구분
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        //OAuth 로그인 진행 시 키가 되는 필드값, 구글의 기본 코드는 sub
        String userNameAttributeName = userRequest.getClientRegistration().
                getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        //CustomOAuth2UserService를 통해 가져온 OAuth2User의 attribute를 담을 클래스
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName,
                oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);
        // 세션에 사용자 정보를 저장하기 위해 user를 직렬화 하는 sessionUser
        httpSession.setAttribute("user", new SessionUser(user));

        return new DefaultOAuth2User(
                Collections.singleton(new
                        SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());

    }

    private User saveOrUpdate(OAuthAttributes attributes)
    {
        // 기존 유저라면 update, 신규 유저라면 User를 생성
        // 기존 유저가 소셜서비스에서 이름을 바꾸거나 사진을 바꾸면 이를 적용
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());

        return userRepository.save(user);
    }
}
