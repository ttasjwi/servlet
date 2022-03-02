
# Servlet
- 서블릿 학습을 위한 프로젝트

---

## Servlet은?

- HTTP 요청이 들어오면 소켓 연결 작업, 요청 메시지에 대한 여러가지 귀찮은 파싱작업, 비즈니스 로직 실행 과정, 응답메시지 작성, 소켓 종료 작업 등등의 모든 과정을 거쳐서 응답을 보냄.
- 이 중 의미있는건 비즈니스 로직 실행 과정이고, 나머지가 너무 귀찮음.
- Servlet을 통해 서버단에서 처리해야하는 업무를 간소화

---

## 프로젝트 생성

```groovy
plugins {
	id 'org.springframework.boot' version '2.6.4'
	id 'io.spring.dependency-management' version '1.0.11.RELEASE'
	id 'java'
	id 'war'
}

group = 'com.ttasjwi'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

configurations {
	compileOnly {
		extendsFrom annotationProcessor
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-web'
	compileOnly 'org.projectlombok:lombok'
	annotationProcessor 'org.projectlombok:lombok'
	providedRuntime 'org.springframework.boot:spring-boot-starter-tomcat'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
	useJUnitPlatform()
}
```
- Project : Gradle Project, Spring Boot
- Java: 11
- Dependencies : Spring Web, Lombok
  - Spring Web : Apache Tomcat 내장
  - Lombok : 어노테이션을 기반으로 getter, setter, Constructor(생성자), ... 을 편리하게 컴파일 시 생성
- **Packaging : War**
  - jsp를 실행하기 위함

---

## application.properties
`src/main/resources/application.properties`

```properties
# HTTP 요청 메시지를 로그로 확인 가능
logging.level.org.apache.coyote.http11=debug
```
- Http 요청메시지를 콘솔에 띄우도록 설정
- 운영 서버에서 사용하면, 모든 요청정보를 콘솔에 띄우게 되므로 성능 저하가 발생할 수 있음. 개발 단계에서만 사용 권장

---

## Main (ServletApplication)

```java
@ServletComponentScan // 서블릿 자동 등록
@SpringBootApplication
public class ServletApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServletApplication.class, args);
	}

}
```
- `@ServletComponentScan` : 서블릿을 스캔해서 싱글톤으로 등록함
- 실행하면 내장 톰캣 서버가 실행됨.

---

## HelloServlet

<details>
<summary>소스코드 및 분석</summary>
<div markdown="1">

```java
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
```

- `HttpServlet` 추상 클래스를 상속
- `@WebServlet` : 서블릿
    - name: 서블릿 명
    - urlPattern : 클라이언트의 요청 url
- `HttpServletRequest`, `HttpServletResponse` : 표준 요청/응답 인터페이스
  - 실제 요청이 들어오면 각 서버의 사양에 맞게 구현체가 생성됨
- `request.getParameter(...)` : HttpRequest의 QueryParameter의 값을 가져옴
- `response.set...` : HttpResponse의 Header부분
  - `setContentType` : 미디어 타입. 여기서는 `test/plain`을 줬는데 단순 문자열을 넘긴다는 뜻이다.
  - `setCharacterEncoding` : 문자 인코딩. 여기서는 `utf-8`을 줬는데 응답으로 `utf-8`로 문자 인코딩을 지정한다는 뜻이다.
- `response.getWriter().write(...)` : HttpResponse의 Body에 문자열을 넘김

</div>
</details>

---

## 페이지 구조

1. main/webapp/index.html
   - welcome 페이지

2. main/webapp/basic.html
   - 학습할 내용들

3. main/webapp/basic/hello-form.html
   - hello-form : form의 submit 버튼을 눌렀을 때 폼에 적힌 입력값을 post방식으로 전송. 요청 리소스는 "/request-param"

---

## RequestHeaderServlet

HttpServletRequest 구현체의 메서드를 실습하기 위한 Servlet

```java
// http://localhost:8080/request-header?username=hello
@WebServlet(name = "requestHeaderServlet", urlPatterns = "/request-header")
public class RequestHeaderServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        printStartLine(request);
        printHeaders(request);
        printHeaderUtils(request);
        printEtc(request);

        response.getWriter().write("ok");
    }
    // 중략
}
```
- Http 요청의 StartLine에 대한 정보를 출력하기 위한 `printStartLine(request)`메서드
- Http 요청의 Header에 담긴 정보를 모두 출력하기 위한 `printHeaders(request)`메서드
- Http 요청의 Header의 특정 정보들을 출력하는 편의 메서드들만 확인하기 위한 `printHeaderUtils(request)` 메서드
- Http 요청의 기타 정보 `printEtc(request)` : 요청측(Remote) / 서버측(Local)에 대한 정보들

### printStartLine

<details>
<summary>소스코드 및 분석</summary>
<div markdown="1">

```java
// http://localhost:8080/request-header?username=hello
@WebServlet(name = "requestHeaderServlet", urlPatterns = "/request-header")
public class RequestHeaderServlet extends HttpServlet {
    
    // 생략
    
    //start line 정보
    private void printStartLine(HttpServletRequest request) {
        System.out.println("--- REQUEST-LINE - start ---");
        System.out.println("request.getMethod() = " + request.getMethod()); // HTTP 메서드 - GET
        System.out.println("request.getProtocal() = " + request.getProtocol()); // 프로토콜 - HTTP/1.1
        System.out.println("request.getScheme() = " + request.getScheme()); // scheme (사용할 프로토콜 - http)

        // 요청 URL(http://localhost:8080/request-header)
        System.out.println("request.getRequestURL() = " + request.getRequestURL());
        // 요청 URI(/request-test)
        System.out.println("request.getRequestURI() = " + request.getRequestURI());
        // QueryParameter (username=hi)
        System.out.println("request.getQueryString() = " + request.getQueryString());

        System.out.println("request.isSecure() = " + request.isSecure()); // https 사용 유무
        System.out.println("--- REQUEST-LINE - end ---");
        System.out.println();
    }
    
    // 생랴
}
```

#### 1. HTTP 메시지 StartLine : **(요청일 때) 요청 메시지** / (응답일 때) 상태 메시지
> 메서드 SP request-target SP HTTP-version CRLF
   - Http메서드
   - 요청 대상
   - HTTP 버전

#### 2. 서블릿에서 지원하는 주요 메서드들
- Http 메서드 : `getMethod()`
- 요청 대상
  - 요청 URI : `getRequestURI()`
  - 쿼리 : `getQueryString()`
- HTTP 버전(프로토콜) : `getProtocol()`

#### 3. 이 부분에서 추가적으로 다룬 메서드들
- 요청 URL : `getRequestURL()`
- scheme : `getScheme()`
- https 사용유무 : `isSecure()`

</div>
</details>

### printHeaders / printHeaderUtils

<details>
<summary>소스코드 및 분석</summary>
<div markdown="1">

```java
// http://localhost:8080/request-header?username=hello
@WebServlet(name = "requestHeaderServlet", urlPatterns = "/request-header")
public class RequestHeaderServlet extends HttpServlet {

  //Header 모든 정보
  private void printHeaders(HttpServletRequest request) {
    System.out.println("--- Headers - start ---");

        /*
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
        String headerName = headerNames.nextElement();
        System.out.println(headerName + ": " + request.getHeader(headerName));
        }

        아래와 같은 코드
        */

    request.getHeaderNames().asIterator()
            .forEachRemaining(headerName -> System.out.println(headerName + ": " + request.getHeader(headerName)));

    System.out.println("--- Headers - end ---");
    System.out.println();
  }

  //Header 편리한 조회
  private void printHeaderUtils(HttpServletRequest request) {
    System.out.println("--- Header 편의 조회 start ---");

    System.out.println("[Host 편의 조회]");
    System.out.println("request.getServerName() = " + request.getServerName()); // Host 헤더(호스트명)
    System.out.println("request.getServerPort() = " + request.getServerPort()); // Host 헤더(포트명)
    System.out.println();

    System.out.println("[Accept-Language 편의 조회]");
    request.getLocales().asIterator()
            .forEachRemaining(locale -> System.out.println("locale = " + locale));
    System.out.println("request.getLocale() = " + request.getLocale());
    System.out.println();

    System.out.println("[cookie 편의 조회]");
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        System.out.println(cookie.getName() + ": " + cookie.getValue());
      }
    }
    System.out.println();

    System.out.println("[Content 편의 조회]");
    System.out.println("request.getContentType() = " + request.getContentType());
    System.out.println("request.getContentLength() = " + request.getContentLength());
    System.out.println("request.getCharacterEncoding() = " + request.getCharacterEncoding());

    System.out.println("--- Header 편의 조회 end ---");
    System.out.println();
  }
}
```
#### 1. HTTP 메시지 Header : HTTP 전송에 필요한 모든 부가정보
- 메시지 BODY의 내용, 메시지 BODY의 크기/압축/인증, 요청 클라이언트(브라우저) 정보, 서버 애플리케이션 정보, 캐시 관리 정보, ...
- "헤더명: ..."의 형태로 전송됨

#### 2. 서블릿의 HTTPRequest Header 관련 메서드
- `getHeaderNames()` : 헤더명들을 싹 얻어와 이를 기반으로 요청에 관한 정보를 얻어올 수 있음
- `getHeader("...")` : 원하는 헤더명으로 가져오기
- Host 조회 메서드
  - 호스트명 : `getServerName()`
  - 호스트 포트 : `getServerPort()`
- 네고시에이션 메서드 - Accept Language
  - `getLocales()` : 브라우저 설정상에 등록된 선호 자연어들 전체
  - `getLocale()` : 제일 우선순위가 높은 요구 로케일
- 쿠키 관련
  - `getCookies()` : 쿠키들 가져옴
- Content 메서드
  - `getContentType()` : 전송된 미디어 타입
  - `getContentLength()` : 요청의 문자인코딩


</div>
</details>


### printEtc

<details>
<summary>소스코드 및 분석</summary>
<div markdown="1">

```java
// http://localhost:8080/request-header?username=hello
@WebServlet(name = "requestHeaderServlet", urlPatterns = "/request-header")
public class RequestHeaderServlet extends HttpServlet {
    
  //기타 정보
  private void printEtc(HttpServletRequest request) {
    System.out.println("--- 기타 조회 start ---");

    System.out.println("[Remote 정보]");
    System.out.println("request.getRemoteHost() = " + request.getRemoteHost()); // 요청(클라이언트)측에 대한 정보
    System.out.println("request.getRemoteAddr() = " + request.getRemoteAddr()); //
    System.out.println("request.getRemotePort() = " + request.getRemotePort()); //
    System.out.println();

    System.out.println("[Local 정보]");
    System.out.println("request.getLocalName() = " + request.getLocalName()); // 서버 측에 대한 정보
    System.out.println("request.getLocalAddr() = " + request.getLocalAddr()); //
    System.out.println("request.getLocalPort() = " + request.getLocalPort()); //

    System.out.println("--- 기타 조회 end ---");
    System.out.println();
  }
}
```
그 외 메서드들
- getRemoteHost,Addr,Port : 요청 측 ip(디폴트 ipv6), 포트
- getLocalName,Addr,Prot : 서버 이름, ip, Port

</div>
</details>

---

## RequestParamServlet

요청데이터 - 쿼리 파라미터를 받아올 때

```java
@WebServlet(name = "requestParamServlet", urlPatterns = "/request-param")
public class RequestParamServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("[전체 파라미터 조회] - start");

        System.out.println("RequestParamServlet.service");
        request.getParameterNames().asIterator()
                        .forEachRemaining(paramName -> System.out.println(paramName + "=" + request.getParameter(paramName)));

        System.out.println("[전체 파라미터 조회] - end");
        System.out.println();

        System.out.println("[단일 파라미터 조회] - start");
        String username = request.getParameter("username");
        String age = request.getParameter("age");

        System.out.println("username = " + username);
        System.out.println("age = " + age);

        System.out.println("[단일 파라미터 조회] - end");
        System.out.println();

        System.out.println("[이름이 같은 복수 파라미터 조회] - start");
        String[] usernames = request.getParameterValues("username");
        for (String name : usernames) {
            System.out.println("username = " + name);
        }
        System.out.println("[이름이 같은 복수 파라미터 조회] - end");

        response.getWriter().write("ok!");
    }
}
```
- 전체 쿼리 파라미터 접근 : `request.getParameterNames().asIterator().forEachRemaining`
- 단일 파라미터 조회
  - `request.getParameter("파라미터명")` : 해당 파라미터명에 대응하는 값을 가져옴. 복수의 값이 있을 경우 제일 첫번째 값을 가져옴
  - `request.getParameterValues("파라미터명")` : 해당 파라미터명에 대응되는 모든 값을 배열로 가져옴

---

## RequestBodyStringServlet
```java
@WebServlet (name = "requestBodyStringServlet", urlPatterns = "/request-body-string")
public class RequestBodyStringServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletInputStream inputStream = request.getInputStream(); // http 요청을 바이트코드로 바로 얻어올 수 있음
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);// 바이트코드를 문자열로 바꾸기 위해서, UTF-8 인코딩으로 명시해야함

        System.out.println("messageBody = " + messageBody);

        response.getWriter().write("ok");
    }
}
```
- HttpRequest 메시지 바디에 단순히 순수 텍스트만 전송됐을 때
- `getInputStream`을 통해 바이트코드 inputStream으로 변환후, `StreamUtils.copyToString`을 통해 문자열로 변환
  - 이때, 문자인코딩 UTF-8을 지정하여야 제대로 UTF-8에 맞게 파싱하여 문자열로 만들어준다.

---

## RequestBodyJsonServlet

```java
@WebServlet(name = "requestBodyJsonServlet", urlPatterns = "/request-body-json")
public class RequestBodyJsonServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        System.out.println("messageBody = " + messageBody);

        HelloData helloData = objectMapper.readValue(messageBody, HelloData.class);

        System.out.println("helloData.username = " + helloData.getUsername());
        System.out.println("helloData.age = " + helloData.getAge());

        response.getWriter().write("ok");
    }
}
```
- json도 결국은 문자데이터. 가져오는 것은 `getInputStream()`, `StreamUtils.copyToString(...)`로 같다.
- 그런데 스프링부트는 Jackson 라이브러리를 통해 `ObjectMapper`를 함께 제공함
- ObjectMapper의 readValue를 통해, json 문자열을 객체로 변환할 수 있음

### (참고)
- html POST 방식 Form을 통해 들어온 바디데이터를, `getInputStream()`, `StreamUtils.copyToString(...)` 을 통해 문자열로 바꿀 수는 있음
- 하지만 ObjectMapper는 Json 데이터에 대해서만 객체 변환을 지원함.
- get방식의 queryParameter 또는 html form의 post 방식 요청데이터는 `getParameter`를 통해 처리하는게 좋음

---

## ResponseHeaderServlet
```java
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
```
- HttpServletResponse 구현체의 메서드를 실습하기 위한 Servlet
- Http 응답 상태코드 설정 : setStatus(`HttpServletResponse.SC_~`)
  - `HttpServletResponse.SC_~` : 상태 코드에 해당하는 static 상수
- Http 응답 헤더 설정 : setHeader("헤더명", "값")
- Http 응답 편의 메서드
  1. content~
     - setContentType("값") : 미디어 타입 지정
     - setCharacterEncoding("값") : 문자 인코딩 지정.주로 utf-8 지정
  2. 쿠키
     - Cookie 생성 : new Cookie("쿠키명", "값")
     - Cookie 생명주기 : cookie.setMaxAge(값)
     - 쿠키 추가 : response.addCookie(쿠키)
  3. 리다이렉트
     - sendRedirect("로케이션") : 로케이션 위치로 리다이렉션

---

## ResponseHtmlServlet

```java
@WebServlet(name = "responseHtmlServlet", urlPatterns = "/response-html")
public class ResponseHtmlServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Content-Type : text/html;charset=utf-8
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();
        writer.println("<html>");
        writer.println("<body>");
        writer.println("  <div>안녕?</div>");
        writer.println("</body>");
        writer.println("</html>");
    }
}
```
- 미디어 타입을 "test/html"으로 지정, 데이터를 `html`으로 전송함

---
## ResponseJsonServlet
```java
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
```
- 응답을 json 타입으로 하려면 Content-Type을 "application/json"으로 하여 보내야함.
- 객체를 json으로 반환하기 위해, ObjectMapper의 `WriteValueAsString(객체)`으로 객체를 json문자열로 반환 받기
- json문자열을 `response.getWriter().write(json문자열)`을 통해 Body에 넣어 보냄

---

# 회원관리 웹 애플리케이션 도메인, Repository

- Member : 회원 도메인
- MemberRepository : 회원 저장, 조회 담당 (메모리)


## Ver 1 - 순수 서블릿
- 서블릿에서 요청을 받아 repository에 접근하여 비즈니스 로직을 수행
- 결과를 java단에서 직접 바로 html 코드를 작성해서 반환함.
- 한계
  - html을 작성하는 코드가 너무 길어져버림.


## Ver 2 - jsp(java Server Pages)
```
<%@ page import="com.ttasjwi.servlet.domain.Member" %>
<%@ page import="com.ttasjwi.servlet.domain.MemberRepository" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    //request, response 사용 가능.

    MemberRepository memberRepository = MemberRepository.getInstance();

    System.out.println("MemberSaveServlet.service");
    String username = request.getParameter("username");
    int age = Integer.parseInt(request.getParameter("age"));

    Member member = new Member(username, age);
    memberRepository.save(member);
%>
<html>
<head>
```
- html에 자바 코드를 작성하여, 비즈니스 로직과 view를 모두 수행
- 서버에서 실행되면 jsp가 서블릿으로 변환됨
- 한계
  - jsp에 주요 비즈니스 로직이 모두 노출됨.
  - view에서 비즈니스 로직까지 전부 수행하는 상황. 유지보수가 매우 힘들어짐.

## Ver 3 - MVC 패턴 적용(servlet + jsp)
```java
@WebServlet(name = "mvcMemberSaveServlet", urlPatterns = "/servlet-mvc/members/save")
public class MvcMemberSaveServlet extends HttpServlet {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("MvcMemberSaveServlet.service");

        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        // model에 데이터 보관
        request.setAttribute("member", member);

        String viewPath = "/WEB-INF/views/save-result.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}
```
- jsp파일에 파일명으로 접근할 수 없도록 `/WNB-INF`의 하위 경로에 저장
- url을 통해, 컨트롤러에 거치고, 리소스에 접근함
- `request.setAttribute`를 통해 요청을 `model`로 삼음. 이곳에 view에 전달할 데이터를 담는다.
- requestDispatcher의 forward 메서드를 통해, 제어의 주도권을 다음 jsp파일로 넘김.
- java단(`controller`)에서 순전히 비즈니스 로직을 수행하고, jsp에서는 `view`의 역할만 수행하도록 함.


### (ver 3) 한계
#### 포워드 중복
```java
RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
dispatcher.forward(request, response);
```
- `dispatcher`를 가져오는 코드, forward 메서드 호출이 계속 중복됨.
#### ViewPath 중복
```java
String viewPath = "/WEB-INF/views/save-result.jsp";
```
- `/WEB-INF/views/` 중복
- `.jsp` 중복 : 나중에 다른 view(thymeleaf, ...)으로 교체하면 jsp 사용한 전체 코드를 싹 다 변경해야함
#### 불필요한 코드
```java
HttpServletRequest request, HttpServletResponse response
```
- 쓸 때도 있고 안 쓸 때도 있음.
- response는 현재 코드에서 아예 안 쓰이고 있음
- `HttpServletRequest`, `HttpServletResponse`를 사용한 코드는 테스트가 어려움
#### 공통처리가 어려움
- 공통으로 처리할 메서드를 따로 분리하면 될 것 같긴한데, 이걸 뽑더라도 매번 항상 호출해야하고 실수로 호출하지 않으면 문제가 생김
- 호출하는 그 행위 자체도 중복임

### (ver 3) 개선안?
- 컨트롤러 호출 전에 공통기능을 처리할 계층이 필요함
- 프론트 컨트롤러 패턴(Front Controller Pattern) : 수문장에 해당하는, 공통 기능을 처리하는 컨트롤러 계층을 만들어보자!

---

## Ver 4 - 프론트 컨트롤러 도입
```java
public interface ControllerV1 {

    void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

}
```
```java
@WebServlet(name = "FrontControllerServletV1", urlPatterns = "/front-controller/v1/*")
public class FrontControllerServletV1 extends HttpServlet {

    private Map<String, ControllerV1> controllerMap = new HashMap<>();

    public FrontControllerServletV1() {
        controllerMap.put("/front-controller/v1/members/new-form", new MemberFormControllerV1());
        controllerMap.put("/front-controller/v1/members/save", new MemberSaveControllerV1());
        controllerMap.put("/front-controller/v1/members", new MemberListControllerV1());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("FrontControllerServletV1.service");
        String requestURI = request.getRequestURI();
        ControllerV1 controller = controllerMap.get(requestURI);
        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        controller.process(request, response);
    }
}
```
- 모든 요청에 대하여, 프론트 컨트롤러를 거치도록 함.
- 실질적으로 호출되는 서블릿은 프론트 컨트롤러 단 하나뿐임.
- 프론트 컨트롤러에는 내부적으로 `Map<String, ControllerV1>`을 가지고 있음.
  - key로, 요청 uri, value로는 `ControllerV1`인터페이스의 구현체를 가지고 있다.
- 요청이 오면, 맵에서 key에 대응하는 컨트롤러를 꺼내고 컨트롤러에 공통적으로 구현된 process 메서드를 실행한다.
  - (controller를 못 찾으면 상태코드에 404 NotFound를 set하고 반환)

---

## Ver 5 - View를 담당하는 MyView 추가
### MyView
```java
public class MyView {

    private String viewPath; // view 경로

    public MyView(String viewPath) {
        this.viewPath = viewPath;
    }

    // 렌더링
    public void render(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(viewPath);
        requestDispatcher.forward(request, response);
    }

}
```
- MyView는 내부적으로 viewPath(jsp 경로)를 가짐
- render 메서드 호출 시, requestDispatcher.forward를 통해, 제어의 주도권을 jsp쪽으로 넘김
  - 즉, 실질적으로 이 메서드를 호출하면 렌더링이 됨.


### ControllerV2, FrontControllerServletV2
```java
public interface ControllerV2 {

    MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

}
```
```java
@WebServlet(name = "FrontControllerServletV2", urlPatterns = "/front-controller/v2/*")
public class FrontControllerServletV2 extends HttpServlet {

    private Map<String, ControllerV2> controllerMap = new HashMap<>();

    public FrontControllerServletV2() {
        controllerMap.put("/front-controller/v2/members/new-form", new MemberFormControllerV2());
        controllerMap.put("/front-controller/v2/members/save", new MemberSaveControllerV2());
        controllerMap.put("/front-controller/v2/members", new MemberListControllerV2());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      
        String requestURI = request.getRequestURI();
        ControllerV2 controller = controllerMap.get(requestURI);
        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        MyView view = controller.process(request, response);
        view.render(request, response);
    }
}
```
- V1과 대부분 로직은 같은데, Controller에서 MyView를 반환하게 하고 MyView를 통해 렌더링하게 함.
- 이제 각 Controller에서는 View를 수행하지 않고 FrontController 계층에서 view를 공통처리하게 됨

---

## Ver 6 - Model 및 ViewResolver 추가

### FrontController의 책임 증가, But 나머지 Controller의 편의성 증가
```java
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
```
```java
public interface ControllerV3 {

    ModelView process(Map<String, String> paramMap);
}
```
- HttpServletRequest의 모든 쿼리 파라미터를 뽑아서 paramMap으로 만듬
- 각 Controller의 process 메서드는 매개변수로 전달된 paramMap을 기반으로 비즈니스 로직을 수행후 결과를 ModelView에 담아 반환
  - ModelView에 담긴 것
    - view의 논리적 이름
    - Model : view에서 출력할 대상
  - viewResolver를 통해 view의 논리적 이름을 물리적 경로명으로 변환한뒤 MyView로 변환
  - MyView에 render 메서드를 통해, model을 넘겨줘서 렌더링

### MyView의 render 메서드 오버로딩
```java
public class MyView {

    private String viewPath; // view 경로

    public MyView(String viewPath) {
        this.viewPath = viewPath;
    }

    // 생략 //

    public void render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        modelToRequestAttribute(model, request); // 모델을 HttpServletRequest에 담음
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(viewPath); 
        requestDispatcher.forward(request, response); // 제어의 주도권을 viewPath의 jsp에게 전달
    }

    private void modelToRequestAttribute(Map<String, Object> model, HttpServletRequest request) {
        model.forEach((key, value) -> request.setAttribute(key, value));
    }
}
```
- `modelToRequestAttribute` : model의 요소들을 request에 담음.
- viewPath에 저장된 물리적 경로에 위치한 jsp에 제어권을 넘김.
- jsp는 request에 담긴 값들을 기반으로 렌더링

---

## Ver 7 - Controller에서 viewName만 반환하도록 함
```java
public interface ControllerV4 {

    /**
     * @param paramMap
     * @param model
     * @return viewName
     */

    String process(Map<String,String> paramMap, Map<String,Object> model);

}
```
```java
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
```
- 기존 코드 : 컨트롤러에서 Model을 생성하고, modelView에 viewName을 함께 반환해야하는 수고를 들여야했음.
- 변경 코드
  - 프론트 컨트롤러에서 Model을 생성하고, parameter로 paramMap과 Model을 함께 넘김.
  - 컨트롤러에서는 인자로 주입된 `model`에 데이터를 담고, `viewName`만 반환하면 됨.

---