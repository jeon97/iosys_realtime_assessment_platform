# 관리 서비스

## 서비스 역할

시험 운영에 필요한 계획·시험지·응시자 데이터를 반입하고 Redis와 DB 상태를 관리합니다.

## 내가 개발한 기능

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

업로드 단계에서 파일 종류와 필수 파일 존재 여부를 먼저 확인하고, ZIP 항목의 경로를 검증한 뒤 JSON만 추출합니다. 파싱과 저장을 분리하고 시험계획 ID와 실행 유형을 모든 저장 데이터에 전달했습니다.

Redis 업로드와 삭제는 별도 API로 제공해 운영자가 시험 데이터 준비 상태를 제어할 수 있도록 했습니다. 답안 다운로드는 생성 상태를 확인한 뒤 파일명과 스트림을 반환하도록 구성했습니다.

## 공개 예제

- [PlanArchiveImporter](../../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/manager/PlanArchiveImporter.java): ZIP 항목 검증, 필수 JSON 확인, 디코딩 후 일괄 저장 흐름을 재작성했습니다.
- [PlanArchiveImporterTest](../../samples/event-worker/src/test/java/com/portfolio/assessment/eventworker/manager/PlanArchiveImporterTest.java): 정상 반입, 경로 이탈, 필수 파일 누락을 검증합니다.

