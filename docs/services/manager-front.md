# 관리 UI

## 서비스 역할

운영자가 시험계획 목록과 상세 상태를 확인하고 시험 데이터를 Redis에 준비하거나 제거하는 React 기반 화면입니다.

## 내가 개발한 기능

- 시험 목록의 페이지네이션과 검색 조건 상태관리
- 시험 상세정보 Store 구성
- Redis 업로드·삭제 API 연결
- 목록과 상세 화면의 로딩·오류 상태 처리
- API 프록시와 배포 기본 경로 조정
- TypeScript 코드 정리와 불필요한 경고 제거

## 구현 방식

목록 조건과 페이지 상태를 Store에 모아 화면 이동 후에도 동일 조건을 유지하도록 구성했습니다. Redis 작업은 상세 Store의 명령으로 분리하고 성공 후 상세 상태를 다시 조회하도록 했습니다.

## 공개 예제

- [examStore.mjs](../../samples/manager-ui/examStore.mjs): 검색·페이지 상태와 Redis 명령 후 재조회 흐름을 프레임워크 독립 코드로 재작성했습니다.
- [examStore.test.mjs](../../samples/manager-ui/examStore.test.mjs): 조건 변경, 페이지 초기화, 작업 후 재조회를 검증합니다.

