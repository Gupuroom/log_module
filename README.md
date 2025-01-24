# log_module README

## 1. Submodule 설정
- 이 모듈은 **단독으로 동작하지 않습니다**. 반드시 메인 프로젝트에 **submodule**로 추가하여 사용해야 합니다.
- 해당 로그 모듈은 Spring Boot 환경에서의 로그 관리 및 설정을 간소화하는 용도로 사용됩니다. 외부 프로젝트에서 쉽게 재사용 가능하도록 설계되었습니다.

### Submodule 추가 방법
```bash
git submodule add https://github.com/Gupuroom/log_module.git ./log_module
```

## 2. 모듈 구조

`log_module`은 최소한의 의존성으로 구성되어 있으며, 다른 프로젝트에서도 유연하게 사용될 수 있도록 설계되었습니다.

### 기본 패키지 구조
```bash
com.log_module
│
├── config
│   └── WebConfig
│
├── exception
│   ├── CommonException
│   ├── CommonExceptionCode
│   ├── CommonExceptionHandler
│   └── CommonExceptionResponse
│
├── logging
│   ├── filter
│   │   ├── ApiLogFilter
│   │   └── ErrorLogFilter
│   ├── interceptor
│   │   └── LogInterceptor
│   ├── type
│   │   └── MDCKey
│   └── wrapper
│       └── CustomHttpRequestWrapper
│       └── CustomHttpResponseWrapper
│
└── test
    ├── controller
    │   └── TestController
    └── type
        └── TestExceptionCode

resources
│
├── application.yml
└── logback-spring.xml

```


### 의도한 바

이 모듈의 주된 목적은 **로그의 일관성 유지**, **재사용성 높이기**, **유지보수 용이성**을 기반으로 한 로그 시스템을 구축하는 것입니다.

- **유연성**: 다양한 환경 (Local, Dev, Real)에서 환경에 맞는 로그 기록 방식을 지원합니다.
- **유지보수성**: 코드의 변경 없이 로그 관리만으로 환경에 맞게 로그를 기록할 수 있습니다.

## 3. 로그 기록 정책
1. **실서버**
    - 기본적으로 **Response Log**만 기록하고, **에러 발생 시**에만 Request Log를 추가로 기록하여 로그 파일 크기를 최소화합니다.

2. **환경별 설정**
    - **Local**: 로그를 Console에 출력
    - **Dev/Real**: APILOG, ERRORLOG, CONSOLELOG를 별도의 파일로 저장

## 4. 주요 기능

### 4.1 API, Error 분리 기록
- **문제점**: Console로 찍을 시 특정 로그를 찾기 어려운 점을 개선하기 위해 ERROR 로그만 별도로 기록하도록 설계하였습니다.
- **해결방안**: Error 로그는 파일로 저장되며, Error 로그만을 기록하여 에러 발생 시 빠르게 로그를 분석할 수 있습니다.

logback-spring.xml
```java
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
<file>${LOG_DIR}/error.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
<fileNamePattern>${LOG_DIR}/error.%d{yyyy-MM-dd}.log.zip</fileNamePattern>
<maxHistory>${MAX_HISTORY}</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd'T'HH:mm:ss.SSSXXX} %-5level --- [ %thread] %logger{36} : %msg%n</pattern>
        </encoder>
        <filter class="com.log_module.logging.filter.ErrorLogFilter"/>
    </appender>
```
ErrorLogFilter.java
```java
@Component
public class ErrorLogFilter extends Filter<LoggingEvent> {

    @Override
    public FilterReply decide(LoggingEvent event) {
        Level level = event.getLevel();
        if (level == Level.ERROR) {
            return FilterReply.ACCEPT;
        }

        return FilterReply.DENY;
    }
}
```
### 4.2 API 별 검색 기능 (traceId 추가)
- **문제점**: 기존 timestamp 로그 검색은 번거로웠습니다.
- **해결방안**: `traceId`를 추가하여 API 별로 쉽게 검색할 수 있도록 했습니다.

CommonExceptionHandler.java
```java
@ExceptionHandler(CommonException.class)
public ResponseEntity<CommonExceptionResponse> handleCustomException(CommonException exception) {
    CommonExceptionCode errorCode = exception.getErrorCode();
    CommonExceptionResponse errorResponse = CommonExceptionResponse.of(errorCode);

    errorLogMDCRequestDetails();
    log.error("Common exception occurred: code: {}, message: {}", errorCode.getCode(), errorCode.getMessage(), exception);
    return ResponseEntity.badRequest().body(errorResponse);
}
```
CommonExceptionResponse.java
```java
@Getter
@Builder
public class CommonExceptionResponse {
    private String code;
    private String message;
    private String traceId;
    private LocalDateTime timestamp;

    public static CommonExceptionResponse of(CommonExceptionCode errorCode) {
        String traceId = MDC.get(MDCKey.TRACE_ID.getKey());
        return CommonExceptionResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .traceId(traceId)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
```

### 4.3 로그 파일 일별 분리, 압축, 보관일 지정
- **문제점**: 기존 WAR + Tomcat 환경에서는 로그 설정이 제한적이고, 일별 분리와 압축이 복잡했습니다.
- **해결방안**: JAR 환경에서 **로그 파일 자동 분리**, **압축**, **보관일 설정**을 지원합니다. 기본 보관일은 **30일**이며, 설정 파일(`application.yml`)을 통해 쉽게 변경할 수 있습니다.

#### 설정 예시
```yaml
config:
  log-dir: logs        # 로그 저장 경로
  max-history: 30      # 로그 보관일 (기본값: 30일)
```