package com.ttasjwi.servlet.basic.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttasjwi.servlet.basic.HelloData;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "responseJsonServlet",urlPatterns = "/response-json")
public class ResponseJsonServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Content-Type : application/json
        response.setContentType("application/json");
         response.setCharacterEncoding("utf-8");

        HelloData helloData = new HelloData();
        helloData.setUsername("땃쥐");
        helloData.setAge(20);

        //{"username":"kim", "age":20}
        String result = objectMapper.writeValueAsString(helloData); // 객체 -> Json 문자열
        response.getWriter().write(result);
    }
}
