package com.ttasjwi.servlet.web.springmvc.v1;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

//@Component
//@RequestMapping
@Controller // RequestMappingHandlerMapping은 클래스레벨에 스프링 빈에 대하여, @RequestMapping 어노테이션 또는 @Controller가 붙어있는 클래스에 대해서만 매핑 정보로 인식한다.
public class SpringMemberFormControllerV1 {

    @RequestMapping("/springmvc/v1/members/new-form")
    public ModelAndView process() {
        return new ModelAndView("new-form");
    }
}




