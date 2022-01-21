package com.example.webservice_spring.config;

import com.example.webservice_spring.config.auth.LoginUserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    //이 리졸버를 스프링에서 인식할 수 있게 설정 파일에 추가
    private final LoginUserArgumentResolver loginUserArgumentResolver;

    //항상 이 함수를 통해 HandleMethodArgumentResolver를 추가해야함
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolver) {

        argumentResolver.add(loginUserArgumentResolver); //이 리졸버를 추가

    }
}
