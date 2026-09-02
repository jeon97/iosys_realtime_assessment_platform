# 응시자 실시간 상태 처리

응시자 API는 답안뿐 아니라 접속, 카메라, 채팅과 이상행위 이벤트도 처리했습니다. 이 데이터는 화면에 즉시 보여야 하는 현재 상태와 사후 확인을 위한 이력이 모두 필요합니다.

## 분리한 책임

- Redis: 시험별 응시자의 최신 접속·카메라 상태
- 메시지 큐: 채팅과 이상행위 이벤트 전달
- PostgreSQL: 감독 이력과 감사 기록 영속화
- 이벤트 ID: 재전송된 메시지의 중복 처리 차단

카메라 이벤트는 촬영 영상을 저장하는 기능이 아니라 상태 변경을 전달하는 흐름으로 정리했습니다. 채팅 내용과 개인정보도 공개 예제에 넣지 않았습니다.

[MonitoringEventService](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/examinee/MonitoringEventService.java)는 시험 문맥 확인, 최신 상태 갱신, 이벤트 중복 방지와 큐 장애 시 이력 보존을 독립적으로 재작성한 예제입니다.
