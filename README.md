# 📑 목차
- [1️⃣ 프로젝트 소개](##1️⃣-프로젝트-소개)
- [2️⃣ 특징](#2️⃣-특징)
- [3️⃣ 설정 방법](#3️⃣-설정-방법)
- [4️⃣ 패키지 구조](#4️⃣-패키지-구조)
- [5️⃣ 환경 설정](#5️⃣-환경-설정)

# 1️⃣ 프로젝트 소개

`log_module`은 **Spring Boot** 기반 프로젝트의 로그 관리를 간소화하고, **환경별 설정**, **일관된 로그 관리**, 그리고 **유지보수성 향상**을 목표로 설계된 모듈입니다.

이 모듈은 다음과 같은 문제를 해결하는 데 초점을 맞춥니다:

- 환경에 따른 로그 관리의 복잡성 해소
- API 호출 추적 및 디버깅 편의성 증대
- 초기 프로젝트 설정 효율성 강화

---

# 2️⃣ 특징

`log_module`은 다양한 환경과 요구 사항에 맞춰 유연하게 설계된 모듈로, 다음과 같은 주요 특징을 제공합니다:

### 1. **환경별 설정**
- `local`, `dev`, `real` 환경에 따라 로그 출력
  - **local**: `Console` 출력
  - **dev/real**: `Api` 로그, `Error` 로그, `Console` 로그를 각각 별도의 파일로 분리 기록

### 2. **Error 및 Api 로그 관리**
- Error 로그와 Api 로그를 분리하여 저장
  - Error 로그는 별도 파일에 기록하여 디버깅과 문제 추적이 용이
  - API 로그는 `traceId`를 포함해 호출 흐름을 명확히 추적 가능

### 3. **효율적인 로그 파일 관리**
- 로그 파일을 **일별로 분리**하고, 자동으로 압축 및 보관일 설정
- 기본적으로 **30일**간 보관하며, yml 파일에서 보관 기간 변경 가능

### 4. **최소한의 의존성**
- Spring Boot 프로젝트에서 별도의 복잡한 설정 없이 손쉽게 적용 가능
- 단순한 구조로 설계되어, 다른 프로젝트에서도 재사용 가능

### 5. **추적 가능한 API 호출**
- 각 API 호출 시 `traceId`를 기록하여 디버깅 및 에러 발생 시 관련 로그 검색 용이
- 에러 발생 시 `traceId`를 통해 연관된 로그 추적

### 6. **테스트 지원**
- 기본적으로 제공되는 테스트 컨트롤러와 예외 코드를 통해, 모듈 테스트 및 검증이 용이


이 모듈은 단순히 로그를 기록하는 기능뿐 아니라, 개발 및 운영 환경에서 로그 관리를 체계적으로 할 수 있도록 설계되었습니다.

---

# 3️⃣ 설정 방법

### 1. 서브모듈 설정
서브모듈은 다른 Git 저장소를 현재 프로젝트에 종속적으로 포함시키는 방법입니다.  
**`log_module`**을 서브모듈로 설정하려면 아래 과정을 따릅니다.

```bash
git submodule add https://github.com/Gupuroom/log_module.git log_module
git submodule init
git submodule update
```

>💡 참고:
다른 개발자가 이 프로젝트를 클론하면, 서브모듈 데이터가 포함되지 않을 수 있으니 아래 명령어를 실행해야 합니다.
```bash
git clone <repository_url> git submodule update --init --recursive
```

### 2. 멀티모듈 설정
멀티모듈 설정은 하나의 프로젝트가 아닌 여러 Module을 모아 구성한 프로젝트 구조를 말합니다.

여기서는 `log_module`을 Root 프로젝트에 모듈로서 포함하여, 의존성을 관리하고 log_module의 기능을 Root 프로젝트에서 활용할 수 있도록 설정하는 방법을 설명합니다.

#### 1. settings.gradle 설정 추가
log_module을 프로젝트에 포함:

```gardle
include ':log_module'
```

#### Root 프로젝트 build.gradle 설정 추가
```gradle
allprojects {
    repositories {
        mavenCentral()  // 모든 모에서 Maven Central을 사용할 수 있게 설정
    }
}

dependencies {
    ...
    implementation project(':log_module') // log_module을 의존성에 추가
    ...
}

subprojects {
    apply plugin: 'java'  // Java 플러그인을 모든 서브모듈에 적용
    apply plugin: 'org.springframework.boot'  // Spring Boot 플러그인을 서브모듈에 적용
    apply plugin: 'io.spring.dependency-management'  // 의존성 관리를 위한 플러그인 적용
}

```

#### @SpringBootApplication 설정
멀티모듈 프로젝트에서는 @SpringBootApplication에서 각 모듈의 패키지를 스캔해야 합니다.

루트 프로젝트의 @SpringBootApplication을 아래와 같이 scanBasePackages를 정합니다:
```java
@SpringBootApplication(scanBasePackages = {"com.example", "com.log_module"})
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
```

---

# 4️⃣ 패키지 구조

`log_module`은 다음과 같은 패키지 구조입니다:

```bash
com.log_module
│
├── config
│   └── WebConfig                # Spring Boot 설정 클래스
│
├── exception
│   ├── CommonException          # 공통 예외 처리 클래스
│   ├── CommonExceptionCode      # 예외 코드 정의
│   ├── CommonExceptionHandler   # 예외 처리 핸들러
│   └── CommonExceptionResponse  # 예외 응답 클래스
│
├── logging
│   ├── filter
│   │   ├── ApiLogFilter         # API 로그 필터
│   │   └── ErrorLogFilter       # ERROR 로그 필터
│   ├── interceptor
│   │   └── LogInterceptor      # 로그 인터셉터
│   ├── type
│   │   └── MDCKey              # MDC Key 정의
│   └── wrapper
│       └── CustomHttpRequestWrapper  # 커스텀 HTTP 요청 래퍼
│       └── CustomHttpResponseWrapper # 커스텀 HTTP 응답 래퍼
│
└── test
    ├── controller
    │   └── TestController       # 테스트용 컨트롤러
    └── type
        └── TestExceptionCode    # 테스트용 예외 코드
```

---

# 5️⃣ 환경 설정

### application.yml 설정
- 로그 파일 경로 및 보관일 설정.
```yaml
config:
  log-dir: logs   # 로그 파일 저장 경로
  max-history: 30  # 로그 보관일 (기본 30일)
```

### Spring Profiles 설정

Root 프로젝트에서 사용할 환경을 지정해야 합니다. 예를 들어 local, dev, real 환경을 설정하려면 아래와 같이 활성화할 수 있습니다.

**지정안 할 시 로그가 호출되지 않습니다.**
```yaml
spring:
  profiles:
    active: local or dev or real  # 사용할 환경 설정
```
