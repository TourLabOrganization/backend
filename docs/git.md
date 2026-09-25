# Git

## 브랜치

주 브랜치는 2개다.

| 브랜치 | 역할 | 붙어 있는 워크플로 |
|---|---|---|
| `develop` | 개발과 배포의 기준. 여기 머지되면 배포된다 | `cd.yml` (푸시 시 GHCR → EC2) |
| `main` | 릴리스 기준. 릴리스할 때만 `develop`에서 PR을 올린다 | `release.yml` (머지된 PR 제목의 버전으로 태깅) |

- 작업은 `<타입>/<기능요약>` 브랜치에서 하고, PR로만 머지한다.
  일반 작업의 베이스 브랜치는 `develop`이다
- 타입은 커밋 타입과 같은 목록에서 고르고, 기능요약은 kebab-case로 쓴다.
  예: `feat/health-check-api`, `docs/ai-convention`, `ci/github-actions`
- **`develop`과 `main`에 직접 푸시하거나 force-push하지 않는다.** 사람이 승인해도 하지 않는다
- 릴리스 PR의 제목은 `release: ... vX.Y.Z` 형식으로 쓴다.
  `release.yml`이 제목에서 `vX.Y.Z`를 찾지 못하면 실패한다

## 커밋

- 메시지는 `<타입>: <한국어 요약>`으로 쓴다. 예: `feat: 헬스체크 API 추가`
- 타입은 `feat` `fix` `refactor` `docs` `test` `chore` `ci` 중에서 고른다
- `refactor`는 기능 변경이 없는 정리에만 쓴다. 구조가 바뀌면 `feat`이다
- 커밋 전에 `./gradlew spotlessApply`를 직접 실행한다 (`CLAUDE.md` '커밋 전')

원격에 올리기 전 확인 절차는 `CLAUDE.md` 3번 규칙 하나만 따른다. 여기에 다시 적지 않는다.

## pre-commit hook

- `./gradlew installGitHooks`를 1회 실행하면 커밋할 때 `spotlessCheck`가 돈다
- 검사만 하고 자동 수정은 하지 않는다. 실패하면 `./gradlew spotlessApply` 후 다시 커밋한다
