package com.example.webservice_spring.config.auth;

import com.example.webservice_spring.config.auth.dto.SessionUser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final HttpSession httpSession;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //@LoginUser라는 어노테이션이 붙은 파라미터에 대한 작업 수행
        boolean isLoginUserAnnotation = parameter.getParameterAnnotation(LoginUser.class) != null;
        //해당 파라미터의 클래스 타입이 SessionUser인지
        boolean isUserClass = SessionUser.class.equals(parameter.getParameterType());
        return isLoginUserAnnotation && isUserClass;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        //실질적 로직 처리 - user라는 이름이 붙은 세션값 리턴
        return httpSession.getAttribute("user");
    }
}
