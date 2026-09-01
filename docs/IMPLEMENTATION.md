# 구현 상세

## 이벤트 처리

응시 API는 이벤트를 발행하고 워커가 이벤트 ID 중복 여부를 확인한 뒤 실시간 상태와 영속 데이터를 저장합니다. 영속 저장에 실패한 이벤트는 재시도 큐로 보내 요청 처리와 복구 경로를 분리했습니다.

[EventProcessor 코드](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/service/EventProcessor.java)

## JWT와 활성 세션 검증

토큰 서명이 유효하더라도 로그아웃되거나 새 토큰으로 교체된 세션이면 요청을 허용하지 않습니다. 토큰 해석, 만료 확인, Redis 역할의 활성 세션 확인을 각각 분리했습니다.

[SessionTokenService 코드](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/auth/SessionTokenService.java)

## Gateway 인증

공개 경로는 토큰 없이 통과시키고 보호 경로는 Bearer Token을 검사합니다. 검증된 사용자·시험 식별정보와 역할을 이후 서비스가 사용할 수 있는 인증 결과로 구성했습니다.

[GatewayAuthorizationService 코드](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/auth/GatewayAuthorizationService.java)

## 공개 코드 작성 기준

- 회사 소스의 클래스명, 토큰 키, Redis 키와 라우팅 주소를 사용하지 않았습니다.
- 담당 기능의 검증 순서와 책임 분리만 새 코드로 구현했습니다.
- 만료 토큰, 비활성 세션, 공개 경로와 정상 인증을 단위 테스트로 확인합니다.

