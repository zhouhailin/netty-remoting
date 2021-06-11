# Netty Remoting

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/bc80abd17a444f0ba0d94ec807e07843)](https://app.codacy.com/manual/zhouhailin/netty-remoting?utm_source=github.com&utm_medium=referral&utm_content=zhouhailin/netty-remoting&utm_campaign=Badge_Grade_Settings)
[![Jdk Version](https://img.shields.io/badge/JDK-1.8-green.svg)](https://img.shields.io/badge/JDK-1.8-green.svg)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/link.thingscloud/netty-remoting/badge.svg)](https://maven-badges.herokuapp.com/maven-central/link.thingscloud/netty-remoting/)

## netty-remoting

```xml
<dependency>
    <groupId>link.thingscloud</groupId>
    <artifactId>netty-remoting-impl</artifactId>
    <version>${netty-remoting.version}</version>
</dependency>
```

## netty-remoting-spring-boot-starter

```xml
<dependency>
    <groupId>link.thingscloud</groupId>
    <artifactId>netty-remoting-spring-boot-starter</artifactId>
    <version>${netty-remoting.version}</version>
</dependency>
```

```properties
# RemotingServerProperties or RemotingClientProperties
netty.remoting.server.serverListenPort=9888

```

```java
// enable remoting client
@EnableRemotingClientAutoConfiguration
// enable remoting server
@EnableRemotingServerAutoConfiguration
@SpringBootApplication
public class Bootstrap {

    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class, args);
    }

    @Autowired
    RemotingClient remotingClient;
    @Autowired
    RemotingServer remotingServer;
    @Autowired
    RemotingCommandFactory factory;

    @PostConstruct
    public void start() {
        remotingClient.invokeOneWay("127.0.0.1:9888", factory.createRequest());
    }
}
```

### RemotingClient

## License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) Copyright (C) Apache Software Foundation
