# 엔티티 규약

필드 구성의 기준은 ERD다. ERD가 작업 컨텍스트에 없으면 필드를 추측해서 만들지 말고,
멈춰서 사용자에게 요청한다.

## 스키마

테이블은 Hibernate가 엔티티에서 만든다. 마이그레이션 도구는 두지 않았다.
`ddl-auto`는 로컬이 `update`, 테스트가 `create-drop`이다.

- 제약에 이름을 주려면 `@Table(uniqueConstraints = ...)`에 적는다.
  적지 않으면 Hibernate가 `UK...` 형태로 붙인다
- 이름 규칙은 `uk_<테이블>_<컬럼>` / `fk_<테이블>_<참조테이블>`로 한다 (`naming.md`)
- `update`는 컬럼을 추가할 뿐 **지우거나 좁히거나 타입을 바꾸지 않는다.**
  그런 변경은 DB에서 직접 처리한다
- **운영 DB에는 `update`를 쓰지 않는다.** 마이그레이션 도구를 붙이고 `validate`로 바꾼다

## 클래스 선언

| 반드시 붙인다 | 붙이지 않는다 |
|---|---|
| `@Entity` | **클래스 레벨 `@Builder`** |
| `@Getter` | `@Setter` `@Data` `@ToString` `@EqualsAndHashCode` `@NonNull` |
| `@NoArgsConstructor(access = AccessLevel.PROTECTED)` | |
| `@Table(name = "<테이블명>")` — snake_case 복수 | |

오른쪽 열의 lombok 애노테이션은 `lombok.config`가 컴파일 에러로 막는다.

클래스 레벨 `@Builder`는 막혀 있지 않지만 쓰지 않는다.

## 필드

### 기본값이 틀려서 반드시 명시해야 하는 것

| 대상 | 반드시 쓴다 | JPA 기본값 | 빼먹으면 |
|---|---|---|---|
| `@ManyToOne` 연관 | `fetch = FetchType.LAZY` | `EAGER` | 조회할 때마다 연관 엔티티까지 끌고 온다 |
| enum 컬럼 | `@Enumerated(EnumType.STRING)` | `ORDINAL` | enum이 숫자로 저장된다. 상수 순서를 바꾸면 기존 데이터의 의미가 바뀐다 |

**둘 다 컴파일되고 테스트도 통과한다. DB에만 틀린 값이 쌓인다.**

### 나머지 필드 규칙

- PK는 `Long id`, `@Id` + `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- NOT NULL 컬럼에는 `@Column(nullable = false)`

## 연관관계

- **1:N 관계는 N쪽에만 `@ManyToOne`을 둔다.** 1쪽에는 N을 가리키는 필드를 만들지 않는다
- 따라서 **`@OneToMany`를 쓰지 않는다.** 양방향 연관도 열지 않는다
- 연관 필드에는 `@JoinColumn(name = "<참조테이블>_id")`. 필수 연관이면 `optional = false`도 함께 쓴다
- `fetch = FetchType.LAZY`를 반드시 쓴다 (위 기본값 표)
- 1쪽에서 N을 찾아야 하면 리포지토리 메서드로 한다. `findByArticleId` 같은 이름을 쓴다

## 조립과 상태 변경

- **필수값은 생성자 파라미터로 받는다.**
  하나 빠지면 컴파일 에러가 나는 것이 필수값을 강제하는 유일한 수단이다
- **생성자에 `@Builder`를 붙이지 않는다.**
  붙이는 순간 그 필수 파라미터가 옵셔널한 빌더 세터로 바뀌어서, 값을 안 넣어도 `.build()`가
  그냥 컴파일된다. 유일한 예외는 필수 파라미터가 8개를 넘는 경우다
- 파생 기본값(생성 시점의 초기 상태 등)은 **생성자 안에서** 정한다. 호출자가 넘기게 하지 않는다
- 상태 변경은 setter가 아니라 **의도가 드러나는 메서드**로 한다.
  `publish()`, `close()`, `updateTitle(...)` 같은 이름을 쓴다.
  메서드 하나가 함께 바뀌어야 하는 필드를 전부 책임진다.
  예를 들어 상태를 바꾸면 그 시각도 같은 메서드에서 갱신한다

## BaseTimeEntity

- `global/entity/BaseTimeEntity`가 `created_at` / `updated_at`을 채운다.
  `@MappedSuperclass` + `@EntityListeners(AuditingEntityListener.class)`가 붙어 있다
- `@EnableJpaAuditing`은 `global/config/JpaAuditingConfig` **한 곳에만** 둔다
- 상속 대상은 `created_at`과 `updated_at`을 **둘 다** 가진 테이블뿐이다.
  `created_at`만 있는 테이블은 상속하지 않는다
- **테이블에 시각 컬럼이 필요하면 그 테이블을 만드는 PR에서 함께 넣는다**
