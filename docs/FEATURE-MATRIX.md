# 기능별 구현 근거

| 담당 영역 | 개발한 기능 | 구현 방식 | 공개 예제 |
|---|---|---|---|
| 응시자 API | 답안·상태·접속 로그·채팅·카메라 데이터 처리 | 토큰의 시험 식별정보를 기준으로 요청 검증 후 저장 또는 이벤트 발행 | [EventProcessor](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/service/EventProcessor.java) |
| 인증 | Access/Refresh Token 발급·검증, Redis 토큰 상태 확인 | 토큰 해석과 활성 세션 검증을 분리 | [SessionTokenService](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/auth/SessionTokenService.java) |
| Gateway | JWT 필터, 서비스 라우팅, SSE·CORS 처리 | 인증 제외 경로 판정 후 보호 경로의 토큰·세션 확인 | [GatewayAuthorizationService](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/auth/GatewayAuthorizationService.java) |
| 이벤트 워커 | 답안·상태·로그·채팅 이벤트 소비 | 이벤트 ID 중복 검사, 상태 갱신, 영속화, 실패 큐 저장 | [EventProcessor](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/service/EventProcessor.java) |
| 관리 기능 | 시험 데이터 파일·JSON 처리, 동기화, Redis 업로드·삭제 | 입력 자료 검증 후 저장소별 처리 책임 분리 | [기여 내역](CONTRIBUTIONS.md) |

회사 소스의 클래스명과 설정은 사용하지 않았으며, 공개 예제는 담당 기능의 처리 구조만 새로 작성한 코드입니다.

