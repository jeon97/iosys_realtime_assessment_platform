# 실시간 온라인 평가 플랫폼

온라인 시험의 응시자 상태, 답안, 접속 기록과 메시지를 처리하는 MSA 기반 플랫폼입니다. 기존 Java 시스템의 Spring Boot 전환에 참여하여 공통 모델, 응시자 API, 인증·Gateway, 이벤트 워커와 관리자 기능을 개발하였습니다.

## 담당 업무와 구현

### 공통 도메인과 데이터 접근 계층

시험계획, 세션, 응시자, 답안, 진행 상태 등 여러 서비스가 함께 사용하는 모델과 Repository를 공통 모듈로 관리하였습니다. 필드와 조회 조건이 바뀔 때 사용하는 서비스까지 함께 확인하고 버전을 올려 배포하였습니다.

### 응시자 기능

토큰에 포함된 시험 정보를 기준으로 응시 데이터를 조회하고, 답안·진행 상태·접속 로그·채팅·카메라 데이터를 처리하는 API를 구현하였습니다. 메시지 발행에 실패하면 DB 저장 경로를 호출하도록 분기하였습니다.

### 인증과 Gateway

JWT 발급과 검증, Refresh Token, Redis 기반 토큰 상태 확인을 구현하였습니다. Gateway에서는 인증 필터와 서비스 라우팅을 구성하고, UI/API 경로 및 SSE 요청이 정상적으로 통과하도록 CORS 설정을 정리하였습니다.

### 이벤트 워커

RabbitMQ에서 답안, 상태, 로그, 채팅 이벤트를 받아 Redis와 PostgreSQL에 반영하는 워커를 구현하였습니다. 이벤트 종류별 저장 처리를 연결하고 Redis 반영 시도 후 DB 저장을 실행하도록 순서를 조정하였습니다.

### 관리 기능

외부 시험 데이터를 파일과 JSON 형태로 받아 저장하는 기능, 시험 데이터 동기화, Redis 업로드·삭제, 답안 파일 다운로드 기능을 구현하였습니다.

## 기술 구성

| 구분 | 사용 기술 | 적용 영역 |
|---|---|---|
| Backend | Java 17, Spring Boot, WebFlux | 여러 I/O 작업을 비동기 흐름으로 처리 |
| Gateway | Spring Cloud Gateway | 인증과 서비스 라우팅을 진입점에서 통합 |
| Messaging | RabbitMQ, Reactor RabbitMQ | 요청 처리와 저장 작업을 분리 |
| Cache | Redis | 접속 상태와 실시간 조회 데이터 관리 |
| Database | PostgreSQL, R2DBC | 답안과 로그 등 영속 데이터 저장 |
| Realtime | WebSocket, SSE | 응시자·감독관 메시지와 상태 전달 |
| Frontend | React, TypeScript, Vite | 운영 화면과 시험 상태 조회 |
| Build | Gradle, Docker | 서비스별 빌드와 실행 환경 구성 |

## 구현 사례

| 구현 사례 | 공개 코드 |
|---|---|
| 시험 설정 JSON의 이중 직렬화 처리 | [SettingsListReader](samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/SettingsListReader.java) |
| 중첩 답안 요청에서 현재·이전 문항 분리 | [AnswerEnvelopeReader](samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/AnswerEnvelopeReader.java) |
| 캐시 반영 시도 후 영속 저장 순서 구성 | [CacheThenDatabase](samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/CacheThenDatabase.java) |
| 최신 활동 로그의 개수 제한과 만료 처리 | [RecentLogWriter](samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/RecentLogWriter.java) |

[Spring Boot 전환](docs/MODERNIZATION.md) · [서비스별 담당 기능](docs/SERVICES.md) · [부하 시험 시나리오](docs/PERFORMANCE-TEST.md)

## 코드 예제

업무 처리 흐름을 별도로 작성한 예제입니다. 회사 운영 코드와 데이터는 포함하지 않습니다. 예제의 구성과 추가 규칙은 [예제 안내](docs/SAMPLE-NOTES.md)에 있습니다.

- [이벤트 워커](samples/event-worker): 중복 이벤트 검사, 상태 저장과 저장 실패 시 재시도 큐 처리
- [인증·Gateway](samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/auth): 토큰 만료, 활성 세션과 접근 경로 검사
- [응시자 답안 처리](samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/examinee): 이벤트 발행과 큐 장애 시 대체 저장
- [응시자 상태 처리](samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/examinee/MonitoringEventService.java): 카메라·채팅·이상행위 이벤트 처리
- [시험자료 반입](samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/manager): ZIP 항목·필수 JSON 검사 후 일괄 저장
- [관리 UI 상태관리](samples/manager-ui): 검색·페이지 상태와 Redis 작업 후 상세 재조회

[담당 업무](docs/CONTRIBUTIONS.md) · [구현 상세](docs/CASE-STUDIES.md) · [코드 목록](docs/FEATURE-MATRIX.md) · [예제 실행·테스트](docs/VALIDATION.md)
