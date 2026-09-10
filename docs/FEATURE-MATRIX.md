# 담당 기능과 공개 코드

표의 코드에는 공개용 추가 검증·상태 규칙이 포함됩니다. 원본 적용 여부는 [근거와 구분](SOURCE-SCOPE.md)에 명시하였습니다.

| MSA | 담당 업무 주제 | 공개 예제의 구성 | 공개 예제 |
|---|---|---|---|
| common | 시험계획·세션·사용자·그룹·답안·로그 공통 계약 | 불변 시험 문맥과 반응형 Repository 경계 | [AssessmentContext](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/common/AssessmentContext.java) |
| examinee | 답안·상태·접속 로그·채팅·카메라 데이터 처리 | 시험 문맥 검증 후 이벤트 발행, 큐 장애 시 DB 대체 저장 | [AnswerSubmissionService](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/examinee/AnswerSubmissionService.java) |
| examinee | 카메라·채팅·이상행위 실시간 이벤트 | 최신 상태와 이력 저장 분리, 이벤트 ID 중복 방지, 큐 장애 대체 저장 | [MonitoringEventService](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/examinee/MonitoringEventService.java) |
| manager | 계획·패키지·사용자 파일 반입, 동기화, Redis 관리 | ZIP 경로와 필수 JSON 검증 후 일괄 저장 | [PlanArchiveImporter](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/manager/PlanArchiveImporter.java) |
| manager-front | 시험 검색·페이지네이션, Redis 업로드·삭제 | 목록 조건 Store와 명령 실행 후 상세 재조회 | [ExamStore](../samples/manager-ui/examStore.mjs) |
| security | Access/Refresh Token, 시험 세션, Redis 토큰 상태 | 토큰 해석·만료와 활성 세션 검증 분리 | [SessionTokenService](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/auth/SessionTokenService.java) |
| worker | 답안·상태·로그·채팅 이벤트 소비 | 이벤트 ID 중복 검사, 상태 갱신, 영속화, 실패 큐 저장 | [EventProcessor](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/service/EventProcessor.java) |
| gateway | JWT 필터, 서비스 라우팅, SSE·CORS | 공개 경로 판정 후 보호 경로의 토큰·세션 확인 | [GatewayAuthorizationService](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/auth/GatewayAuthorizationService.java) |
| websocket | 공통 라이브러리 버전과 환경 설정 정리 | 기능 개발 기여는 근거 부족으로 제외 | [서비스 문서](services/websocket.md) |
| monitor | 감독관 모니터링 구성요소 | 본인 커밋 없음 | [서비스 문서](services/monitor.md) |
| proctor | 감독관 업무 구성요소 | 본인 커밋 없음 | [서비스 문서](services/proctor.md) |

회사 소스의 클래스명과 설정은 사용하지 않았으며, 공개 예제는 담당 기능의 처리 구조만 새로 작성한 코드입니다.


## 원본 처리 흐름을 확인한 추가 사례

- [시험 설정 JSON의 이중 직렬화 처리](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/SettingsListReader.java)
- [중첩 답안 요청에서 현재·이전 문항 분리](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/AnswerEnvelopeReader.java)
- [캐시 반영 시도 후 영속 저장 순서 구성](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/CacheThenDatabase.java)
- [최신 활동 로그의 개수 제한과 만료 처리](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/RecentLogWriter.java)

입력·결과와 검증 범위는 [구현 사례 상세](CASE-STUDIES.md)에 정리하였습니다.
