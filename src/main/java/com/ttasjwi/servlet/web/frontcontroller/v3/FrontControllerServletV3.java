package com.ttasjwi.servlet.web.frontcontroller.v3;

import com.ttasjwi.servlet.web.frontcontroller.ModelView;
import com.ttasjwi.servlet.web.frontcontroller.MyView;
import com.ttasjwi.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import com.ttasjwi.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import com.ttasjwi.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@WebServlet(name = "frontControllerServletV3", urlPatterns = "/front-controller/v3/*")
public class FrontControllerServletV3 extends HttpServlet {

    private Map<String, ControllerV3> controllerMap = new HashMap<>();

    public FrontControllerServletV3() {
        controllerMap.put("/front-controller/v3/members/new-form", new MemberFormControllerV3());
        controllerMap.put("/front-controller/v3/members/save", new MemberSaveControllerV3());
        controllerMap.put("/front-controller/v3/members", new MemberListControllerV3());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        ControllerV3 controller = controllerMap.get(requestURI);
        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // HttpServletRequest의 모든 Parameter를 뽑아서, Map으로 만듬
        Map<String, String> paramMap = createParamMap(request);

        // paramMap을 기반으로 비즈니스 로직을 수행하고, ModelView에 view의 논리적 이름과 Model을 담아 반환
        ModelView mv = controller.process(paramMap);

        String viewName = mv.getViewName();
        MyView view = viewResolver(viewName);  // 논리이름을 경로명으로 변환한뒤 MyView 생성
        view.render(mv.getModel(), request, response); // 렌더링
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
