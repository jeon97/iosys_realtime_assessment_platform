# 응시자 실시간 상태 처리

응시자 API에서 접속·카메라·채팅·부정행위 의심 이벤트를 처리하였습니다. 화면 조회용 최신 상태는 Redis에 반영하고, 메시지와 로그는 큐·워커를 거쳐 저장하도록 구성하였습니다.

## 코드 예제

MonitoringEventService는 시험·사용자 식별정보를 확인한 뒤 최신 상태를 갱신하고 이력 이벤트를 발행합니다. 큐 발행 실패 시 대체 저장 경로를 호출합니다. 이벤트 ID 중복 검사는 예제의 추가 규칙입니다.

카메라 예제는 상태 이벤트를 처리하며 영상 저장은 포함하지 않습니다.

[코드](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/examinee/MonitoringEventService.java) · [예제 구성](SAMPLE-NOTES.md)
