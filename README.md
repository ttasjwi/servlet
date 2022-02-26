
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

---

