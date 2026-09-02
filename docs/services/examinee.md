# 응시자 서비스

## 서비스 역할

응시자가 시험에 접속한 이후 사용하는 조회·저장 API와 실시간 이벤트 발행을 담당합니다.

## 내가 개발한 기능

- 토큰 정보 기반 응시자·시험계획·시험지·문항 조회
- 시험계획 JSON의 문자열 필드를 목록 구조로 변환
- 현재 답안, 풀이 문항, 사용자별 시험 상태 저장
- 답안 제출 시 시험 완료 상태 처리
- 시험 진행 단계와 응시 상태 조회·갱신
- 접속 로그와 일반 로그의 큐 발행
- 부정행위 의심 데이터 저장 API
- 채팅 메시지 발행과 Redis 메시지 조회
- 모바일 카메라 상태, 이미지 업로드와 파일 검증
- 얼굴 인식 API 오류 처리
- RabbitMQ 사용 불가 시 DB 직접 저장 경로
- 메시지 헤더와 예약어 치환 처리

## 구현 방식

Controller는 입력과 토큰 식별정보를 Service에 전달하고, Service가 사용자·시험계획 일치 여부를 검증합니다. 답안·로그·상태는 RabbitMQ 이벤트로 발행해 요청 응답과 저장 작업을 분리했습니다.

메시지 큐를 사용할 수 없을 때 데이터가 사라지지 않도록 영속 저장소 대체 경로를 두었습니다. 답안 요청 구조가 변경될 때는 현재 답안뿐 아니라 풀이 문항과 사용자 시험 상태가 같은 데이터 블록에서 처리되도록 맞췄습니다.

## 공개 예제

- [AnswerSubmissionService](../../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/examinee/AnswerSubmissionService.java): 사용자·시험 식별정보 검증, 이벤트 발행, 발행 실패 시 저장 대체 경로를 구현했습니다.
- [AnswerSubmissionServiceTest](../../samples/event-worker/src/test/java/com/portfolio/assessment/eventworker/examinee/AnswerSubmissionServiceTest.java): 정상 발행, 잘못된 사용자, 큐 장애 대체 저장을 검증합니다.

