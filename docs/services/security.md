# 인증 서비스

## 서비스 역할

사용자 로그인, JWT 발급·갱신·검증과 Redis 기반 활성 세션을 관리합니다.

## 내가 개발한 기능

- Access Token과 Refresh Token 발급
- 토큰 서명키의 외부 설정 분리
- JWT에 사용자·시험계획·실행유형·그룹·접근유형 정보 추가
- 쿠키와 응답을 이용한 토큰 전달
- Redis에서 사용자 ID 기준 활성 토큰 확인
- 시험 세션 생성과 로그인 정보 저장
- 접근키 기반 응시자 로그인
- 비동기 WebFlux 인증 흐름
- 인증 실패 응답과 CORS·허용 경로 설정
- 역할과 사용자명을 포함한 로그인 응답

## 구현 방식

토큰 자체의 서명·만료 검증과 Redis 활성 세션 검증을 분리했습니다. 로그인 성공 시 토큰뿐 아니라 시험계획, 실행유형, 그룹과 접근유형을 시험 세션에 저장해 이후 서비스가 동일한 실행 문맥을 사용할 수 있도록 했습니다.

## 공개 예제

- [SessionTokenService](../../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/auth/SessionTokenService.java): 토큰 만료와 활성 세션을 함께 검증합니다.
- [SessionTokenServiceTest](../../samples/event-worker/src/test/java/com/portfolio/assessment/eventworker/auth/SessionTokenServiceTest.java): 정상·만료·비활성 세션을 검증합니다.

