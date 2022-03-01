package com.ttasjwi.servlet.basic.response;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "responseHeaderServlet", urlPatterns = "/response-header")
public class ResponseHeaderServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // [status-line]
        response.setStatus(HttpServletResponse.SC_OK); // 200

        // [response-headers]
        response.setHeader("Content-Type", "text/plain;charset=utf-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("my-header", "hello");

        // [response-Header 편의 메서드]
        content(response);
        cookie(response);
        redirect(response);


        // [message-body]
        PrintWriter writer = response.getWriter();
        writer.print("ok");
    }


    private void content(HttpServletResponse response) {
        // Content-Type: text/plain;charset=utf-8

        //response.setHeader("Content-Type", "text/plain;charset=utf-8");

        response.setContentType("text/plain"); // 미디어 타입
        response.setCharacterEncoding("utf-8"); // 문자 인코딩
        // Content-Length: 2
        // response.setContentLength(2); 생략 시 자동 생성
    }

    private void cookie(HttpServletResponse response) {
        //Set-Cookie: myCookie=good; Max-Age=600
        //response.setHeader("Set-Cookie", "good; Max-Age=600");
        Cookie cookie = new Cookie("myCookie", "good"); // 쿠키명, 값
        cookie.setMaxAge(600); // 유효 시간 - 600초, 600초 경과시 쿠키 삭제
        response.addCookie(cookie); // response에 쿠키 포함
    }


    private void redirect(HttpServletResponse response) throws IOException {
        //Status Code 302
        //Location: /basic/hello-form.html

        // response.setStatus(HttpServletResponse.SC_FOUND); // 302
        // response.setHeader("Location", "/basic/hello-form.html");
        response.sendRedirect("/basic/hello-form.html"); // 이하의 리소스로 리다이렉션
    }

}
