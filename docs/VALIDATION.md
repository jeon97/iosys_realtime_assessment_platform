# 예제 테스트

2026-09-12 실행 결과: Java 21개 통과, 실패·오류·건너뜀 0개. Node 테스트 2개도 통과하였습니다.

## 실행

```sh
mvn -f samples/event-worker/pom.xml test
node --test samples/manager-ui/examStore.test.mjs
```

별도로 작성한 코드 예제의 단위 테스트 결과입니다. DB·캐시·외부 시스템은 메모리 저장소나 주입 함수로 대체합니다.

[구현 사례](CASE-STUDIES.md) · [예제 구성](SAMPLE-NOTES.md)
