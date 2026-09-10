# 구현 사례 상세

담당 기능의 입력, 처리 순서와 예외 경계를 코드·테스트에 연결하였습니다. 원본 구현과 공개 예제의 차이는 각 사례에 명시하였습니다. 기여 확인 범위는 [근거와 공개 예제 구분](SOURCE-SCOPE.md)을 기준으로 합니다.

| 사례 | 구현 주제 | 코드 |
|---|---|---|
| 1 | 시험 설정 JSON의 이중 직렬화 처리 | [SettingsListReader](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/SettingsListReader.java) |
| 2 | 중첩 답안 요청에서 현재·이전 문항 분리 | [AnswerEnvelopeReader](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/AnswerEnvelopeReader.java) |
| 3 | 캐시 반영 시도 후 영속 저장 순서 구성 | [CacheThenDatabase](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/CacheThenDatabase.java) |
| 4 | 최신 활동 로그의 개수 제한과 만료 처리 | [RecentLogWriter](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/RecentLogWriter.java) |

## 1. 시험 설정 JSON의 이중 직렬화 처리

응시자 화면으로 전달하는 시험 설정의 자료형을 정리하였습니다. 언어, 허용 기기, 시험 모드 등의 설정이 JSON 배열 또는 JSON 문자열로 한 번 더 감싼 배열로 저장되어 있어, 두 표현을 목록으로 변환하도록 파싱 흐름을 수정하였습니다. 원본은 첫 번째 파싱이 실패하면 내부 문자열을 다시 읽고, 해석할 수 없는 설정에는 빈 목록을 반환합니다.

**공개 코드**

일반 배열과 한 번 감싼 배열을 같은 문자열 목록으로 변환하도록 재작성하였습니다. 호출 화면은 저장 형식에 따른 분기를 반복하지 않고 목록을 사용할 수 있습니다.

- 입력·결과 예: `["ko","en"]`과 이를 문자열로 감싼 값 → 동일한 2개 항목. 깨진 JSON·객체·숫자 혼합 배열 → 빈 목록.
- 검증: 일반·이중 직렬화 입력의 동일 결과, 잘못된 입력에서 부분 결과가 남지 않는지 검증하였습니다.
- [구현 코드](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/SettingsListReader.java) · [테스트](../samples/event-worker/src/test/java/com/portfolio/assessment/eventworker/cases/SettingsListReaderTest.java)

**원본과 구분한 부분**

공개 예제에는 문자열 원소만 허용하는 규칙을 추가하였습니다. 원본 Jackson의 값 변환 동작 전체를 복제하지 않았으며, 잘못된 설정을 빈 목록으로 처리하는 업무 선택은 유지하였습니다.

확인 근거: R1 · 응시자 서비스 Git 변경과 해당 처리 구간의 작성자 확인.

## 2. 중첩 답안 요청에서 현재·이전 문항 분리

응시자 답안 저장 요청의 데이터 구조 변경을 반영하였습니다. 요청의 최상위 값에서 답안과 진행 상태를 읽던 흐름을 내부 data 묶음에서 읽도록 수정하였습니다. 원본 답안 처리에는 현재 문항과 진행 상태 외에 이전 문항 정보가 전달되는 분기도 존재합니다.

**공개 코드**

현재 답안, 진행 상태, 선택적인 이전 답안을 각각 분리하는 요청 해석 예제를 작성하였습니다. 업무 필드명은 일반화하고, 이후 저장 서비스가 사용할 묶음을 반환하도록 구성하였습니다.

- 입력·결과 예: 현재 2번·이전 1번 문항이 포함된 data → 두 답안 구분. 이전 문항 생략 → 빈 Optional. data 누락 → 입력 오류.
- 검증: 중첩 구조 해석, 이전 답안 선택 처리, 필수 객체 누락과 결과 Map 변경 차단을 검증하였습니다.
- [구현 코드](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/AnswerEnvelopeReader.java) · [테스트](../samples/event-worker/src/test/java/com/portfolio/assessment/eventworker/cases/AnswerEnvelopeReaderTest.java)

**원본과 구분한 부분**

필수 객체 누락 시 명시적인 예외를 반환하고 읽기 전용 Map을 만드는 처리는 공개 예제의 보완입니다. 실제 요청 인증이나 답안 소유권 검증을 이 예제의 기능으로 주장하지 않습니다.

확인 근거: R2 · 응시자 답안 서비스의 data 필드 변경 커밋 확인.

## 3. 캐시 반영 시도 후 영속 저장 순서 구성

답안 워커에서 Redis 저장과 DB 저장의 실행 순서를 조정하였습니다. 답안과 시험 요약의 캐시 반영을 묶어 수행한 뒤 DB 저장 흐름으로 이어지도록 Reactor 체인을 변경하였습니다. 캐시 오류를 처리하는 분기와 DB 저장 순서를 분리해 구성하였습니다.

**공개 코드**

여러 캐시 작업의 완료를 기다린 뒤 DB 작업을 시작하도록 재작성하였습니다. 캐시 작업 하나가 실패해도 나머지 시도가 끝난 후 DB 경로를 실행합니다. DB 호출은 지연 생성하여 캐시 완료 전에 시작되지 않도록 하였습니다.

- 입력·결과 예: 캐시 A 대기·캐시 B 실패 → DB 미실행. A 완료 → DB 실행. DB 실패 → 오류 전달.
- 검증: 지연된 캐시가 끝나기 전 DB 미호출, 캐시 실패 후 DB 진행, DB 실패 전파를 검증하였습니다.
- [구현 코드](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/CacheThenDatabase.java) · [테스트](../samples/event-worker/src/test/java/com/portfolio/assessment/eventworker/cases/CacheThenDatabaseTest.java)

**원본과 구분한 부분**

두 저장소를 하나의 트랜잭션으로 묶거나 이벤트 유실을 방지한다고 보장하지 않습니다. 원본에는 오류를 소비하는 경로도 존재합니다. 공개 예제는 DB 오류를 호출자에게 전달하도록 보완하였습니다.

확인 근거: R3 · 워커 저장 순서 변경 Git 커밋 확인.

## 4. 최신 활동 로그의 개수 제한과 만료 처리

응시자의 최근 활동을 빠르게 조회할 수 있도록 Redis 목록에 새 로그를 앞쪽으로 추가하고, 최신 항목만 유지하는 처리를 구현·수정하였습니다. 원본은 최신 50개를 유지하며 선택적으로 키 만료 시간을 적용합니다. 장기 보존 데이터와 최근 조회용 목록을 구분한 기능입니다.

**공개 코드**

목록 앞쪽 추가 → 보존 개수 제한 → 선택적 만료 순서로 구성하였습니다. 보존 개수는 예제에서 설정값으로 받아 적은 수의 입력으로 제거 동작을 확인할 수 있도록 하였습니다.

- 입력·결과 예: 보존 개수 2에 a·b·c 순서 추가 → c·b 유지. 추가 작업 실패 → false 반환, 후속 목록 정리 미실행.
- 검증: 최신 순서, 오래된 항목 제거, 만료 인자 전달과 실패 시 후속 작업 중단을 검증하였습니다.
- [구현 코드](../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/cases/RecentLogWriter.java) · [테스트](../samples/event-worker/src/test/java/com/portfolio/assessment/eventworker/cases/RecentLogWriterTest.java)

**원본과 구분한 부분**

원본의 공통 처리에는 다른 개발자의 수정도 포함되어 있어 본인이 변경한 목록 처리 구간을 기준으로 설명하였습니다. 예제의 여러 Redis 명령은 원자적이지 않으며, Redis 서버 통합 테스트는 포함하지 않습니다.

확인 근거: R4 · Redis 로그 추가·최신 목록 정리 Git 변경 구간 확인.

모든 입력값과 사례 식별자는 설명용으로 구성하였습니다. 단위 테스트 결과는 공개 예제에 한정합니다.
