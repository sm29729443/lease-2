# 1. 項目初始化

## 1. 資料庫初始化

- 1. 創建資料庫

        `CREATE DATABASE lease CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;`
- 2. 導入資料庫腳本

        將`lease.sql`導入`lease`資料庫。

## 2. 項目結構初始化

此項目的 Spring-Boot 項目結構如下:

```txt
lease
├── common（公共 module ——工具類、公用配置等）
│   ├── pom.xml
│   └── src
├── model（數據 module —— 與資料庫對應的 Entity）
│   ├── pom.xml
│   └── src
├── web（Web module）
│   ├── pom.xml
│   ├── web-admin（後台管理系統 Web module —— Controller、Service、Mapper 等）
│   │   ├── pom.xml
│   │   └── src
│   └── web-app（移動端 Web module —— Controller、Service、Mapper 等）
│       ├── pom.xml
│       └── src
└── pom.xml
```

可以看到把 common、model、web 都拆開來成一個 module，並且透過在 web module 以 maven dependency 的方式去導入 common、model，目前理解這樣做的好處有以下幾點。

- 每個 module 職責分明。
- 重用性，像 common、model 等 module 被抽離成一個 module 時，即可被多個 web module 給重複利用。
- 便於部屬及維護，當抽離成各個不同的 module 時，各個 module 都可以單獨構建及發布，並且若只需更新 web module，那就只需要重新佈署 web module，而不必再重新構建及部屬 common、model。

web module maven dependency:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.atguigu</groupId>
        <artifactId>lease-two</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>

    <artifactId>web</artifactId>
    <packaging>pom</packaging>
    <modules>
        <module>web-admin</module>
        <module>web-app</module>
    </modules>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.atguigu</groupId>
            <artifactId>model</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.atguigu</groupId>
            <artifactId>common</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>

</project>
```

## 3. 後臺管理系統項目初始化配置

### 1. Spring-Boot 配置

#### lease-two module pom.xml

在 root(lease-two) 配置以下 `pom.xml` 檔，root 主要是用來做依賴版本控制的，並不真的導入依賴。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.atguigu</groupId>
    <artifactId>lease-two</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging>
    <modules>
        <module>common</module>
        <module>model</module>
        <module>web</module>
    </modules>

    <!-- 繼承 Spring Boot 父項目 -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.0.5</version>
    </parent>

    <!-- 以 variable 的方式決定依賴版本 -->
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <mybatis-plus.version>3.5.3.1</mybatis-plus.version>
        <swagger.version>2.9.2</swagger.version>
        <jwt.version>0.11.2</jwt.version>
        <easycaptcha.version>1.6.2</easycaptcha.version>
        <minio.version>8.2.0</minio.version>
        <knife4j.version>4.1.0</knife4j.version>
        <aliyun.sms.version>2.0.23</aliyun.sms.version>
    </properties>

    <!--統一管理子模組要導入的依賴版本-->
    <dependencyManagement>
        <dependencies>
            <!--mybatis-plus-->
            <!--官方文档：https://baomidou.com/pages/bab2db/ -->
            <dependency>
                <groupId>com.baomidou</groupId>
                <artifactId>mybatis-plus-boot-starter</artifactId>
                <version>${mybatis-plus.version}</version>
            </dependency>

            <!--knife4j-->
            <!--官方文档：https://doc.xiaominfo.com/docs/quick-start -->
            <dependency>
                <groupId>com.github.xiaoymin</groupId>
                <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
                <version>${knife4j.version}</version>
            </dependency>

            <!--JWT-->
            <!--官方文檔：https://github.com/jwtk/jjwt#install-jdk-maven -->
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-api</artifactId>
                <version>${jwt.version}</version>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-impl</artifactId>
                <scope>runtime</scope>
                <version>${jwt.version}</version>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-jackson</artifactId>
                <scope>runtime</scope>
                <version>${jwt.version}</version>
            </dependency>

            <!--圖形驗證碼-->
            <!--官方文檔：https://gitee.com/ele-admin/EasyCaptcha -->
            <dependency>
                <groupId>com.github.whvcse</groupId>
                <artifactId>easy-captcha</artifactId>
                <version>${easycaptcha.version}</version>
            </dependency>

            <!--minio，在此項目主要用於儲存圖片-->
            <!--官方文檔：https://min.io/docs/minio/linux/developers/minio-drivers.html?ref=docs#java-sdk -->
            <dependency>
                <groupId>io.minio</groupId>
                <artifactId>minio</artifactId>
                <version>${minio.version}</version>
            </dependency>

            <!-- 這個我不需要，之後應該會用 line bot 等等替代 -->
            <!--阿里云短信客户端，用于发送短信验证码-->
            <!--官方文档：https://help.aliyun.com/document_detail/215759.html?spm=a2c4g.215759.0.0.49f32807f4Yc0y -->
<!--            <dependency>-->
<!--                <groupId>com.aliyun</groupId>-->
<!--                <artifactId>dysmsapi20170525</artifactId>-->
<!--                <version>${aliyun.sms.version}</version>-->
<!--            </dependency>-->
        </dependencies>
    </dependencyManagement>

</project>
```

#### web module pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.atguigu</groupId>
        <artifactId>lease-two</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>

    <artifactId>web</artifactId>
    <packaging>pom</packaging>
    <modules>
        <module>web-admin</module>
        <module>web-app</module>
    </modules>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.atguigu</groupId>
            <artifactId>model</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.atguigu</groupId>
            <artifactId>common</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
        </dependency>
    </dependencies>

    <!-- Spring Boot 提供的打包插件，用於打包可執行的 jar 包 -->
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

##### `spring-boot-maven-plugin` 與 `maven-compiler-plugin` 差別

`maven-compiler-plugin`所打包出來的 jar 只包含者項目本身的 .class文件 及 resource 下的資源，並不包含第三方 jar，而`spring-boot-maven-plugin`則會把第三方 jar 也一起打包成一個 jar 檔。

#### 創建`application.yml`

在 web-admin 下創建`application.yml`

```yml
server:
  port: 8080
```

#### 創建 Spring-Boot 啟動類

```java
package com.atguigu.lease;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ClassName: AdminWebApplication
 * Package: com.atguigu.lease
 */
@SpringBootApplication
public class AdminWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminWebApplication.class, args);
    }
}

```

### 2. MyBatis-Plus 配置

#### 1. 配置 common module pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.atguigu</groupId>
        <artifactId>lease-two</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>

    <artifactId>common</artifactId>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!--mybatis-plus-->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
        </dependency>

        <!--mysql driver-->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
        </dependency>
    </dependencies>
</project>
```

#### 2. 配置 model module pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.atguigu</groupId>
        <artifactId>lease-two</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>

    <artifactId>model</artifactId>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!--mybatis-plus-->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
        </dependency>
    </dependencies>

</project>
```

#### 3. 配置 web-admin module `application.yml`

```yml
server:
  port: 8080

spring:
  datasource:
    type: com.zaxxer.hikari.HikariDataSource
    url: jdbc:mysql://192.168.246.100:3306/lease?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=GMT%2b8
    username: root
    password: Atguigu.123
    hikari:
      # 會根據所配置的查詢語句去檢查連接是否正常，譬如這裡連接時會去執行 SELECT 1，若查詢成功，
      # 代表這次連接沒有問題
      connection-test-query: SELECT 1
      connection-timeout: 60000 #資料庫連接超時時間，默認 30s，這裡配置的單位是 ms
      idle-timeout: 500000 #連接池裡的空閒連接存活時間，默認 60m，配置單位 ms，若超過此時間，則連接關閉
      max-lifetime: 540000 #連接池裡的連接的最大存活時間，若超出設置時間，則此連接會被銷毀，並創立一個新連接，值0表示無限生命週期
      maximum-pool-size: 12 #連接池的最大連接數量，默認 10
      minimum-idle: 10 #最小空閒連接數，也就是連接池中隨時都備者10個連接供使用
      pool-name: SPHHikariPool # 連接池名稱

#打印 sql 語句的配置
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

#### 4. 創建 MyBatis-Plus 的配置類

目前功能只有一個，就是配置掃描 mapper 的 path

這裡`@MapperScan("com.atguigu.lease.web.*.mapper")`會看到 IDE 對者`web.*.mapper`報錯，訊息為 Cannot resolve package web，這是因為 common module 確實沒有 web 這個 package，這個 Class 本就是 web-admin 導入 common module 後要使用的，雖然這個報錯不會影響到功能，但若礙眼也可以在 common pom.xml 導入 web-admin，會看到這個報錯就不見了，這是因為導入 web-admin 後就擁有 `com.atguigu.lease.web`這個 package 了。

```java
package com.atguigu.lease.common.mybatisplus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: MybatisPlusConfiguration
 * Package: com.atguigu.lease.common.mybatisplus
 */
@Configuration
@MapperScan("com.atguigu.lease.web.*.mapper")
public class MybatisPlusConfiguration {
}

```

### 3. Knife4j 配置

#### 1. web module pom.xml 配置

新增以下 dependency

```xml
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
</dependency>
```

#### 2. model module pom.xml 配置

新增以下 dependency

```xml
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
</dependency>
```

#### 3. Knife4j 配置類

```java
package com.atguigu.lease.web.admin.custom.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI().info(
                new Info()
                        .title("後台管理系統API")
                        .version("1.0")
                        .description("後台管理系統API"));
    }
    
    @Bean
    public GroupedOpenApi systemAPI() {

        return GroupedOpenApi.builder().group("系统信息管理").
                pathsToMatch(
                        "/admin/system/**"
                ).
                build();
    }

    @Bean
    public GroupedOpenApi loginAPI() {

        return GroupedOpenApi.builder().group("後台登陸管理").
                pathsToMatch(
                        "/admin/login/**",
                        "/admin/info"
                ).
                build();
    }
    
    @Bean
    public GroupedOpenApi apartmentAPI() {

        return GroupedOpenApi.builder().group("公寓信息管理").
                pathsToMatch(
                        "/admin/apartment/**",
                        "/admin/room/**",
                        "/admin/label/**",
                        "/admin/facility/**",
                        "/admin/fee/**",
                        "/admin/attr/**",
                        "/admin/payment/**",
                        "/admin/region/**",
                        "/admin/term/**",
                        "/admin/file/**"
                ).build();
    }
    @Bean
    public GroupedOpenApi leaseAPI() {
        return GroupedOpenApi.builder().group("租赁信息管理").
                pathsToMatch(
                        "/admin/appointment/**",
                        "/admin/agreement/**"
                ).build();
    }
    @Bean
    public GroupedOpenApi userAPI() {
        return GroupedOpenApi.builder().group("平台用户管理").
                pathsToMatch(
                        "/admin/user/**"
                ).build();
    }
}
```

### 4. 生成或導入基礎程式碼

因為教學並非程式碼完全從 0 開始，對於以下文件有先寫好了一些基礎範本供導入，當然也可以自己透過 Mybatis X 等插件去生成。

這裡選擇的方式是直接匯入教學提供的檔案。

|要導入的文件     |module   |package or path|description                                             |
|----------------|---------|----------------------------------------|-------------------------------|
|Entity          |model    |com.atguigu.lease.model.entity          |與資料庫 table 對應的 java class|
|Enums           |model    |com.atguigu.lease.model.enums           |只要是狀態類型的字段都用 Enums   |
|mapper interface|web-admin|com.atguigu.lease.web.admin.mapper      |無                             |
|mapper xml      |web-admin|src/resources/mapper                    |無                             |
|service         |web-admin|com.atguigu.lease.web.admin.service     |無                             |
|serviceImpl     |web-admin|com.atguigu.lease.web.admin.service.impl|無                             |

#### 補充

- Entity 中的所有公共字段(id、create_time、update、is_deleted)抽取到一個 BaseEntity 中方便管理，然後讓所有 Entity 繼承 BaseEntity。
- 所有 Entity 均實現`Serializable`，因為此項目有使用到 redis，需要把物件寫入到 memory。
- 所有的 mapper 均是使用`@MapperScan`進行掃描。
- Table 中的狀態、類型字段，像是 status、type 均是以 INT 儲存的，而在 JAVA entity 中則是用 enums 表示。

若是在 JAVA 中也用 INT 去表示狀態，那程式碼就會是這樣:

```java
if(order.getStatus() == 1){
        // 業務邏輯
}
```

這樣的表示法可讀性差，維護時會很不方便，因此應該以 enums 去取代這種寫法

1. 編寫一個 enums class

```java
public enum Status {

CANCEL(0, "已取消"),
WAIT_PAY(1, "待支付"),
// 僅是示範不寫太多

private final Integer value;
private final String desc;

public Integer value() {
  return value;
}
public String desc() {
  return desc;
}
}
```

2. 將 status 定義成 `Status` 類型

```java
@Data
public class Order{
 private Integer id;
 private Integer userId;
 private Status status;
 ...
}
```

如此程式碼就會變成以下，可讀性較好

```java
order.setStatus(Status.WAIT_PAY);
```

### 5. 導入 Controller 相關程式碼

與上節差不多，教學提供了定義好 Method 但還沒編寫業務邏輯的 Controller。

需要導入的有:

|要導入的文件|module|package or path|description|
|-----------|------|---------------|-----------|
|controller|web-admin|com.atguigu.lease.web.admin.controller|無|
|vo|web-admin|com.atguigu.lease.web.admin.vo|用於封裝要從前端接受或 return 給前端的數據|
|result|common|com.atguigu.lease.common.result|統一定義 return 給前端的數據格式|
