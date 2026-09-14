# 구현 사례

업무별 처리 과정과 코드 예제입니다. [예제 구성](SAMPLE-NOTES.md)

| 기능 | 코드 |
|---|---|
| 시험 설정 JSON의 이중 직렬화 처리 | [SettingsListReader](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/SettingsListReader.java) |
| 중첩 답안 요청에서 현재·이전 문항 분리 | [AnswerEnvelopeReader](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/AnswerEnvelopeReader.java) |
| 캐시 반영 시도 후 영속 저장 순서 구성 | [CacheThenDatabase](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/CacheThenDatabase.java) |
| 최신 활동 로그의 개수 제한과 만료 처리 | [RecentLogWriter](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/RecentLogWriter.java) |

## 1. 시험 설정 JSON의 이중 직렬화 처리

응시자 화면에 전달하는 시험 설정의 자료형을 정리하였습니다. 언어·허용 기기·시험 모드가 JSON 배열 또는 이중 직렬화된 문자열로 저장된 경우를 처리하였습니다. 두 형식을 목록으로 변환하고 해석할 수 없는 설정에는 빈 목록을 반환하도록 수정하였습니다.

### 코드 예제

일반 배열과 이중 직렬화 배열을 문자열 목록으로 변환합니다. 문자열 이외의 원소가 있으면 빈 목록을 반환합니다.

- 입출력: `["ko","en"]`과 이를 문자열로 감싼 값 → 동일한 2개 항목. 깨진 JSON·객체·숫자 혼합 배열 → 빈 목록.
- 테스트: 일반·이중 직렬화 입력의 동일 결과, 잘못된 입력에서 부분 결과가 남지 않는지 검증합니다.
- [코드](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/SettingsListReader.java) · [테스트 코드](../samples/event-worker/src/test/java/com/portfolio/assessment/eventworker/cases/SettingsListReaderTest.java)

## 2. 중첩 답안 요청에서 현재·이전 문항 분리

답안 저장 요청의 구조 변경에 맞춰 Service의 데이터 접근 경로를 수정하였습니다. 최상위에서 읽던 답안과 진행 상태를 내부 data 객체에서 읽도록 변경하였습니다. 현재 문항과 함께 전달되는 이전 문항 정보도 구분하여 처리하였습니다.

### 코드 예제

현재 답안·진행 상태·선택적 이전 답안을 분리합니다. data 객체 누락은 오류로 처리하며 결과 Map은 읽기 전용입니다.

- 입출력: 현재 2번·이전 1번 문항이 포함된 data → 두 답안 구분. 이전 문항 생략 → 빈 Optional. data 누락 → 입력 오류.
- 테스트: 중첩 구조 해석, 이전 답안 선택 처리, 필수 객체 누락과 결과 Map 변경 차단을 검증합니다.
- [코드](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/AnswerEnvelopeReader.java) · [테스트 코드](../samples/event-worker/src/test/java/com/portfolio/assessment/eventworker/cases/AnswerEnvelopeReaderTest.java)

## 3. 캐시 반영 시도 후 영속 저장 순서 구성

답안 워커의 Redis 저장과 DB 저장 순서를 조정하였습니다. 답안과 시험 요약의 캐시 반영 작업을 묶고, 반영 시도가 끝난 뒤 DB 저장을 실행하도록 Reactor 체인을 변경하였습니다.

### 코드 예제

캐시 작업이 모두 끝난 뒤 DB 함수를 호출합니다. 캐시 오류 이후에도 DB 저장을 진행하고 DB 오류는 호출자에게 전달합니다.

- 입출력: 캐시 A 대기·캐시 B 실패 → DB 미실행. A 완료 → DB 실행. DB 실패 → 오류 전달.
- 테스트: 지연된 캐시가 끝나기 전 DB 미호출, 캐시 실패 후 DB 진행, DB 실패 전파를 검증합니다.
- [코드](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/CacheThenDatabase.java) · [테스트 코드](../samples/event-worker/src/test/java/com/portfolio/assessment/eventworker/cases/CacheThenDatabaseTest.java)

## 4. 최신 활동 로그의 개수 제한과 만료 처리

응시자 활동 로그를 Redis 목록의 앞쪽에 추가하고 최신 50개만 유지하도록 처리하였습니다. 선택적으로 키 만료시간을 적용하였으며 최근 조회용 목록과 DB 보존 데이터를 나누어 관리하였습니다.

### 코드 예제

목록 앞쪽 추가 → 보존 개수 제한 → 선택적 만료 순서로 호출합니다. 보존 개수는 인자로 받습니다.

- 입출력: 보존 개수 2에 a·b·c 순서 추가 → c·b 유지. 추가 작업 실패 → false 반환, 후속 목록 정리 미실행.
- 테스트: 최신 순서, 오래된 항목 제거, 만료 인자 전달과 실패 시 후속 작업 중단을 검증합니다.
- [코드](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/RecentLogWriter.java) · [테스트 코드](../samples/event-worker/src/test/java/com/portfolio/assessment/eventworker/cases/RecentLogWriterTest.java)
