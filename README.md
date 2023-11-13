# Template-BE

시계토끼 서비스의 Spring Boot 서버를 위한 Repo 입니다.<br/>
v1.1.0

---

## Feature

**Development in Container**

- 컨테이너 상의 VSCode를 사용하는 개발 환경 구축을 위한 `Dev Container` 적용<br/>

**Github Actions**

- 브랜치에 PR 시 캐싱을 적용한 빌드 테스트 실행
- 태그를 `push` 하면 Container Registry(Docker Hub)로 Docker 이미지 build 후 push
