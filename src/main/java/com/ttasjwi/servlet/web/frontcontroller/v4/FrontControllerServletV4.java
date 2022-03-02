package com.ttasjwi.servlet.web.frontcontroller.v4;

import com.ttasjwi.servlet.web.frontcontroller.MyView;
import com.ttasjwi.servlet.web.frontcontroller.v4.controller.MemberFormControllerV4;
import com.ttasjwi.servlet.web.frontcontroller.v4.controller.MemberListControllerV4;
import com.ttasjwi.servlet.web.frontcontroller.v4.controller.MemberSaveControllerV4;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@WebServlet(name = "frontControllerServletV4", urlPatterns = "/front-controller/v4/*")
public class FrontControllerServletV4 extends HttpServlet {

    private Map<String, ControllerV4> controllerMap = new HashMap<>();

    public FrontControllerServletV4() {
        controllerMap.put("/front-controller/v4/members/new-form", new MemberFormControllerV4());
        controllerMap.put("/front-controller/v4/members/save", new MemberSaveControllerV4());
        controllerMap.put("/front-controller/v4/members", new MemberListControllerV4());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        ControllerV4 controller = controllerMap.get(requestURI);
        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Map<String, String> paramMap = createParamMap(request); // HttpServletRequest의 모든 Parameter를 뽑아서, Map으로 만듬
        Map<String, Object> model = new HashMap<>();

        String viewName = controller.process(paramMap, model); // paramMap을 기반으로 비즈니스 로직을 수행하고 model을 변형, view의 논리적 이름을 반환.

        MyView view = viewResolver(viewName);  // 논리이름을 경로명으로 변환한뒤 MyView 생성
        view.render(model, request, response); // 렌더링
    }

    private Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();

        request.getParameterNames().asIterator()
                .forEachRemaining(
                        paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }

    private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }
}
