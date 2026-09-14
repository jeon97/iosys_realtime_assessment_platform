# 관리 서비스

## 서비스 역할

시험 운영에 필요한 계획·시험지·응시자 데이터를 반입하고 Redis와 DB 상태를 관리합니다.

## 담당 기능

- 계획, 패키지, 사용자 파일의 동시 업로드 처리
- ZIP 파일 내부 JSON 탐색과 추출
- 시험계획 JSON 파싱과 시험·시험지·단계 데이터 저장
- TEST·PRETEST 실행 유형별 사용자 분류
- 시험지 추가와 접근키·단축키 인코딩
- 시험 데이터 동기화 API
- Redis 시험 데이터 업로드·삭제
- 학생 답안 파일 다운로드
- 답안 파일 생성 상태 확인
- 외부 운영 연계 서비스 통합
- PostgreSQL 상태 수집 기능 보완

## 구현 방식

계획·시험지·응시자 파일과 ZIP 내부 JSON을 읽어 시험 운영 데이터로 저장하였습니다. 시험계획 ID와 실행 유형을 기준으로 데이터를 연결하였습니다.

Redis 업로드와 삭제는 별도 API로 제공해 운영자가 시험 데이터 준비 상태를 제어할 수 있도록 하였습니다. 답안 다운로드는 생성 상태를 확인한 뒤 파일명과 스트림을 반환하도록 구성하였습니다.

## 코드 예제

ZIP 경로 이탈 차단과 필수 JSON 검사는 예제에 추가한 규칙입니다.

- [PlanArchiveImporter](../../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/manager/PlanArchiveImporter.java): ZIP 항목·필수 JSON을 검사하고 디코딩 후 일괄 저장합니다.
- [PlanArchiveImporterTest](../../samples/event-worker/src/test/java/com/portfolio/assessment/eventworker/manager/PlanArchiveImporterTest.java): 정상 반입, 경로 이탈, 필수 파일 누락을 검증합니다.
