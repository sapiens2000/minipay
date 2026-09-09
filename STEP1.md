# Step1 설계 논의 정리

## 회원가입 동시성

- 문제: 동일 username으로 10개 스레드가 동시에 `register()` 호출 시 중복 row 생성됨.
- 원인: `User.username`에 unique 제약이 없어서, 애플리케이션 레벨에서 막을 방법이 없었음.
- 결정: `username`에 DB unique 제약 추가.
- 남은 이슈: unique 제약 위반 시 `DataIntegrityViolationException` → 커스텀 예외로 변환하는 위치(서비스 try-catch / AOP / 핸들러에서 cause 파악) 미정.

## 격리 수준 (Isolation Level)

- MySQL 기본 격리 수준(REPEATABLE READ) 그대로 사용.
- REPEATABLE READ는 Dirty Read, Non-repeatable Read를 막지만, 표준 정의상 Phantom Read는 못 막음
  (단, InnoDB는 next-key lock으로 실무에서는 대부분 방지됨).
- 핵심 결론: 잔액 차감처럼 "읽고 나서 그 값 기준으로 쓰는(read-then-write)" 패턴의 Lost Update 문제는
  격리 수준이 아니라 락 전략으로 해결해야 함
  (InnoDB의 일반 `SELECT`는 락 없는 스냅샷 읽기라서 격리 수준을 올려도 근본적으로 해결 안 됨).
- 미정: 격리 수준을 `@Transactional(isolation = ...)`으로 명시할지, DB 기본값에 암묵적으로 의존할지.

## 인출 트랜잭션 락 전략 (메인 계좌 → 적금 계좌)

- 결정: 비관적 락(`SELECT ... FOR UPDATE`) 사용.
- 근거: 낙관적 락은 충돌 시 예외/재시도가 필요한데, 짧고 빈번한 인출 트랜잭션 특성상 비관적 락이 더 적합하다고 판단.
- 락 순서: 메인 계좌(`ACCOUNT`)와 적금 계좌(`SAVINGS_ACCOUNT`)는 서로 다른 테이블이며 PK 시퀀스도 독립적.
  현재 스코프에서는 인출이 메인 → 적금 단방향만 존재하므로, 두 테이블 간 락 순서로 인한 데드락 여지는 없다고 판단.
  (같은 타입 계좌 간, 예: 메인-메인 이체가 생기면 그때는 id 순 정렬로 락 순서를 통일해야 함.)
- 재검토 예정: Step5(적금 기능)에서 적금 해지/만기 시 적금 → 메인으로 자금이 이동하는 흐름이 생기면,
  메인↔적금 간에도 양방향 락 순서 문제가 발생할 수 있으므로 그때 다시 검토.

## 일일 한도 (dailyLimit / used) 로직

- 리셋 방식: 매일 00시에 배치로 `used` 일괄 초기화.
  - 배치 구체 구현(스케줄러, 자정 경계에 진행 중인 인출 트랜잭션과의 충돌 처리 등)은 배치 도입 시점에 구체화 예정.
- 원자성: 한도 체크 + `used` 갱신은 잔액 차감과 **같은 트랜잭션, 같은 락**으로 처리.
- 락 순서: 한 트랜잭션 안에서 `User`(한도)와 `Account`(메인 계좌 잔액)에 각각 `FOR UPDATE`를 거는데,
  인출 트랜잭션은 항상 "해당 유저의 User → 해당 유저의 Account → 적금 Account" 한 방향으로만 락을 걸므로
  현재 스코프에서는 데드락 여지 없음 (다른 방향으로 락을 거는 코드 경로가 생기면 재검토).

## 적금 계좌 생성 시점

- 회원가입 시 메인 계좌와 함께 생성하지 않고, 별도의 생성 로직(엔드포인트/서비스 메서드)을 추가해 유저가 원할 때 개설하는 방식으로 예정.

## 격리 수준 명시 여부

- 코드에 명시하지 않고 DB 기본값(MySQL REPEATABLE READ)을 그대로 사용.
- 이후 스텝에서 격리 수준을 바꾸게 되면, 바뀌는 시점의 해당 Step 문서(예: STEP2.md 등)에 변경 사유와 함께 기록.

## 테스트 커버리지 게이트 (CI)

- `build.gradle.kts`에 JaCoCo 플러그인 추가, line coverage 80% 미만이면 `jacocoTestCoverageVerification`이 실패하도록 설정 (`check` task에 연결).
- `.github/workflows/ci.yml` 추가: PR 시 MySQL 서비스 컨테이너를 띄우고 `./gradlew check` 실행, 미달 시 PR 체크 실패.
- 이 부분은 과제의 설계 논의 대상이 아니라 의존성/CI 설정이라 직접 구성함.

## 향후 스텝에서 결정할 항목

- 예외 변환 위치 (서비스 try-catch / AOP / 핸들러 cause 분석): 지금은 예외 종류가 적어 근거가 흐릿함.
  이후 스텝에서 예외가 더 추가되면 그때 패턴을 확정.
- 자정 배치와 진행 중인 인출 트랜잭션 간 충돌 처리: 배치 도입 시점(해당 Step)에서 구체화.
