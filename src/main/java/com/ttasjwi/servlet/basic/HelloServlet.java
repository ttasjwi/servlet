package com.ttasjwi.servlet.basic;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "helloServlet", urlPatterns = "/hello") // "/hello" 요청이 오면 실행됨
public class HelloServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("HelloServlet.service");
        System.out.println("request = " + request);
        System.out.println("response = " + response);

        String username = request.getParameter("username"); // 요청의 QueryParameter를 가져옴
        System.out.println("username = " + username);

        response.setContentType("text/plain"); // 응답 : 단순문자열 -> HttpResponse Header
        response.setCharacterEncoding("utf-8"); // 문자 인코딩 : utf-8 -> HttpResponse Header
        response.getWriter().write("hello, " + username); // HttpResponse Body에 데이터 전달
    }
}
