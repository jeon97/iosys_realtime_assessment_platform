# API Gateway

## 서비스 역할

외부 요청의 단일 진입점으로서 인증을 검사하고 응시자·관리자·인증·실시간 서비스로 라우팅합니다.

## 내가 개발한 기능

- Spring Cloud Gateway 기본 구성
- UI와 API 경로별 서비스 라우팅
- 응시자 Context Path 추가
- JWT 필터와 토큰 Provider
- Redis 활성 토큰 확인
- 인증 제외·보호 경로 정리
- 관리자 경로와 SSE 요청 CORS 허용
- SSE에 필요한 헤더 노출
- 서명키와 서비스 주소의 환경 설정 분리

## 구현 방식

공개 경로는 필터를 통과시키고 보호 경로는 Bearer Token을 파싱한 뒤 토큰과 Redis 세션을 확인합니다. 인증 서비스와 같은 토큰 규칙을 사용하고, 정상 인증된 요청만 대상 서비스로 전달합니다.

## 공개 예제

- [GatewayAuthorizationService](../../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/auth/GatewayAuthorizationService.java): 공개 경로, Bearer Token, 사용자·시험 문맥 전달을 구현했습니다.

