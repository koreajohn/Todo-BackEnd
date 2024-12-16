📅 Todo List & Calendar App
💫 소개
Todo List와 Calendar에서 일정을 등록하고 양방향으로 확인할 수 있는 통합 일정 관리 애플리케이션입니다.
🚀 실행 방법
참고 Swagger UI http://localhost:8080/swagger-ui/index.html#/

원하는 폴더에서 Git Bash 실행
프로젝트 클론
bashCopygit clone -b master https://github.com/koreajohn/Todo-BackEnd.git

IDE에서 프로젝트 오픈

VS Code나 IntelliJ 등 선호하는 IDE 사용


application.properties 파일 생성

resources 디렉토리 생성
resources 하위에 첨부드린 application.properties 붙혀넣기


개발 서버 실행

AssignmentApplication Run



🛠️ 주요 컴포넌트 및 기술 스택
Spring Security

기능

인증/인가 처리
JWT 기반 보안
CORS 관리
보안 필터 체인 구성


사용 이유

클라이언트-서버 간 안전한 통신을 위한 표준화된 보안 프레임워크
Stateless 서버 구현을 위한 JWT 토큰 기반 인증
프론트엔드(React)와의 통신을 위한 CORS 설정
보안 취약점 최소화



Spring Data JPA

기능

CRUD 자동화
커스텀 쿼리
엔티티 매핑
트랜잭션 관리


사용 이유

반복적인 데이터베이스 작업 감소로 생산성 향상
메서드 이름으로 쿼리 생성
데이터베이스 벤더 독립성
타입 안전한 쿼리 작성



Jakarta Persistence (JPA)

기능

ORM 구현
엔티티 관리
트랜잭션 처리
DB 연동


사용 이유

객체지향적 데이터베이스 처리
생산성과 유지보수성 향상
자동 스키마 생성
다양한 DB 플랫폼 지원



Lombok

기능

로깅 기능
생성자 자동 생성
Getter/Setter 자동 생성
빌더 패턴 지원


사용 이유

보일러플레이트 코드 제거
코드 가독성 향상
개발 생산성 증가
일관된 코드 작성



Spring Web (Spring MVC)

기능

REST API 구현
HTTP 처리
라우팅
웹 계층 구성


사용 이유

RESTful 웹 서비스 구현
프론트엔드와의 효율적 통신
HTTP 요청/응답 처리
확장 가능한 구조



Swagger (SpringDoc OpenAPI)

기능

API 문서화
테스트 인터페이스
API 스펙 정의


사용 이유

자동 문서 생성
API 테스트 환경 제공
프론트엔드 개발자와의 협업
실시간 API 변경사항 반영



Jackson

기능

JSON 처리
객체 변환
데이터 포맷팅


사용 이유

효율적인 객체-JSON 변환
REST API 데이터 처리
다양한 데이터 타입 지원
커스텀 직렬화 규칙 적용



🎯 프로젝트 목표

개발 생산성 향상
코드 품질과 유지보수성 확보
보안성 강화
확장 가능한 아키텍처 구현
효율적인 협업 환경 구축


© 2024 Todo List & Calendar App - Developed with ❤️ by koreajohn
