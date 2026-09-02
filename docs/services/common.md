# 공통 도메인 모듈

## 서비스 역할

여러 MSA가 동일한 시험 데이터 구조와 반응형 Repository 계약을 사용하도록 제공하는 공통 라이브러리입니다.

## 내가 개발한 기능

- 시험계획, 시험 세션, 사용자, 그룹·그룹원 모델
- 답안, 사용자별 시험, 진행 상태, 단계 진행 모델
- 시험지 목록·시험·단계 모델
- 접속 로그, 채팅, 일반 로그와 부정행위 의심 데이터 모델
- `planId`, `runType`, `groupId`, 역할 등을 조합한 조회 메서드
- `Mono`와 `Flux` 반환 범위를 데이터 건수에 맞게 수정
- 서비스 변경에 맞춘 공통 라이브러리 버전 관리
- 데이터베이스 예약어를 안전한 필드명으로 변환하는 처리

## 구현 방식

공통 모듈은 업무 로직을 넣지 않고 데이터 계약과 Repository 경계만 제공합니다. 한 건 조회는 `Mono`, 여러 건 조회는 `Flux`로 구분하고, 서비스에서 필요한 조회 조건을 메서드 이름에 명시했습니다.

필드 변경 시에는 모델, Repository ID 타입, 사용하는 서비스의 공통 버전을 함께 변경했습니다. 공통 모듈 변경이 여러 서비스에 전파되기 때문에 직렬화 형식과 DB 컬럼 호환성을 같이 확인했습니다.

## 공개 예제

- [AssessmentContext](../../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/common/AssessmentContext.java): 서비스 사이에서 전달하는 시험 식별정보를 불변 객체로 재작성했습니다.
- [AssessmentEvent](../../samples/event-worker/src/main/java/com/portfolio/assessment/eventworker/domain/AssessmentEvent.java): 워커가 사용하는 공통 이벤트 계약 예제입니다.

