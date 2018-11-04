# apiwidgets
This repo contains the Java Spring base `apiwidgets`

# How to use
Just need to attach one and only one annotation `@EnableApiWidgets` to enable all the features provided by `apiwidgets`.

```xml
<dependency>
    <groupId>com.arch</groupId>
    <artifactId>arch-apiwidgets</artifactId>
    <version>1.0.0-RELEASE</version>
</dependency>
```
The `@EnableApiWidgets` annotation SHOULD apply to global configuration class or main application.
```java
@EnableApiWidgets
@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
```

# Features
- [x] Unify API response protocol, like a `Standard`, that means will convert all the api results to a standard data structure `ApiResult<T>`:
```json
{
    "statusCode": 20000,
    "message": "OK",
    "result": {
    
    }
}
```
or
```json
{
    "statusCode": 20000,
    "message": "OK",
    "result": [
    
    ]
}
```
The `result` is the `@RestController` action method raw returned business domain. 

- [x] Global Handling Exceptions, all exceptions be thrown by RESTful API will be converted to `ApiResult` as described earlier. By using this, **apiwidgets** using/providing following standard `status code`:

| statusCode |  status code desc                          |
|:-----------|:-------------------------------------------|
| 20000      |  Success/OK                                |
| 20400      |  Not Content                               |
| 20500      |  api reentry                               |
| 30200      |  Ajax request redirect                     |
| 40000      |  Argument/parameter verify failure         |
| 40100      |  Unauthorized/Permission checking failure  |
| 40400      |  Not found by using filter condition       |
| 50000      |  System Exception                          |
| 50101      |  Access Token verify failure               |
| 50102      |  client_id/appKey invalid                  |
| 50103      |  time/date formatter error                 |

- [x] Making all request's parameters binding case insensitive. As all we know, the `@RequestParam(value="systemId")` underlying will call the
```java
request.getParameter(systemId)
```
that's case sensitive, it's Java standard. However, sometimes interaction with other languages, such as C#, they users always using `SystemId` rather then `systemId`, another reason is we want our API usage become more simplify, so we require `systemid`, `systemId`, `systemID`, `SystemId` represents the same thing.

- [x] Provides a better validation/verifying/checking framework. This ensure we can using POJO as DTO and it's checker. It isn't **mandatory** all developers to use POJO. However, in some complicated validation scenarios, e.g. some properties SHOULD meet some conditions when one or more properties isn't provided. `@EnsureChecked` usage simple as following:

```java
@EnsureChecked(checkBy = RoleCreateChecker.class)
public class RoleCreateDto {
    private int systemId;
    private String name;
    private String desc;

    // the standard setter and getter
}

public class RoleCreateChecker extends AbstractChecker<RoleCreateDto> {
    public boolean isValid(dto roleCreateDto, ConstraintValidatorContext context) {
        if (StringUtils.isNullOrEmpty(dto.getName())) {
            return invalid(context, "name", "角色名不能为空");
        }

        if (StringUtils.isNullOrEmpty(String.valueOf(dto.getDesc()))) {
            return invalid(context, "desc", "角色描述不能为空");
        }

        return true;
    }
}
```

- [x] Simplify Json Serialization or Deserialization Feature, we want that case insensitive and not failure on unknown properties so that we do not need to attach `@JsonProperty`, the reason as described earlier. For example, if you just need to write java client for `TOF` API that written using C#, you will enjoy this feature.
