# Spring-Security
1. **Basic knowledge of Spring Security**

-----------------

## Contents
1. [Using](#using)
2. [Project Dependency](#project-dependency)
3. [Licence](#license)

--------------------------------------------

## Using
1. **FrontEnd** - HTML5
2. **BackEnd** - Java(JDK 1.8), MySQL(v8.0.25), Spring Boot(2.3.12.RELEASE), JPA
3. **Library&API** - Spring Security, Lombok, Mustache
4. **IDE** - STS (Spring Tool Suite 3.9.12.RELEASE), MySQL Workbench 8.0 CE

--------------------------------------------

## Project Dependency
1. **Spring Boot DevTools** : Hot reloading, 소스파일을 수정했을 때 저장하면 프로젝트가 자동으로 리로딩이 됨.
2. **Lombok** : Getter, Setter, 생성자, Builder 패턴을 위해 사용
3. **Spinrg Data JPA** : ORM 쓰기 위해 사용
4. **MySQL Driver** : MySQL DB 사용
5. **Spring Security** : 우리 서버로 들어오는 모든 주소가 막혀서 인증이 필요한 서버가 된다.
6. **Mustache** : Template Engine / 스프링에서 공식적으로 사용 권장
    - mustache : 기본 폴더가 src/main/resoureces/
    - 뷰 리졸버 설정 : templates (prefix), .mustache (suffix)
    - 예를 들어, src/main/resources/templates/index.mustache
    - Mustache를 사용하겠다고 의존성 설정을 하면 자동으로 뷰 리졸버가 설정됨. 뷰 리졸버를 yml, properties 파일에 설정할 필요 없음
    - .html을 사용하기 위해서 config/WebMvcConfig.java 에서 Mustache 재설정
7. **Spring Web** : Web과 관련된 Annotation

--------------------------------------------

## License
- **Source Code** based on [codingspecialist'lecture](https://github.com/codingspecialist)