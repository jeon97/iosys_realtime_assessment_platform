# 이벤트 워커

## 서비스 역할

RabbitMQ에서 답안·상태·로그·채팅·부정행위 이벤트를 소비해 Redis와 PostgreSQL에 반영합니다.

## 내가 개발한 기능

- 답안 이벤트 소비와 DB 저장
- 답안의 Redis 실시간 조회 구조 저장
- 응시 상태 워커 전면 수정
- 접속·이벤트 로그 큐 소비
- 채팅 메시지 소비와 Redis 저장
- 부정행위 의심 이벤트 처리
- 그룹원 상태 데이터 구성
- 시험 사전 데이터를 Redis에 업로드하는 서비스
- Redis와 DB의 저장 순서 조정
- 이벤트 종류별 조건 분기와 공통 모델 버전 정리

## 구현 방식

이벤트 종류마다 Consumer와 저장 Service를 분리했습니다. 빠른 조회가 필요한 현재 상태는 Redis에, 보존이 필요한 답안·로그는 DB에 반영했습니다. 일부 저장이 실패했을 때 재처리할 수 있도록 이벤트 ID를 기준으로 중복 처리를 방지하는 구조가 필요합니다.

## 공개 예제

- [EventProcessor](../../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/service/EventProcessor.java): 중복 검사, 상태 반영, 영속 저장, 실패 큐 이동을 구현했습니다.
- [EventProcessorTest](../../samples/event-worker/src/test/java/com/portfolio/assessment/eventworker/service/EventProcessorTest.java): 정상 처리, 중복 이벤트, 저장 실패를 검증합니다.

