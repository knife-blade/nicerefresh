# nice-refresh
## 1 介绍
nicerefresh：自动刷新Spring项目中@Value的值。

**为什么写这个组件？**

为什么写这个组件？Nacos和Apollo不是有自动更新功能吗？

答：按理他们能够自动更新，但他们的自动更新都有缺陷：
- Nacos：不支持@Value的自动更新。
- Apollo：不支持Property的自动更新。

如有不同观点，欢迎**自测之后**跟作者进行讨论。

**为什么不用@RefreshScope？**

@RefreshScope是有bug的，有很多失效的场景，比如：
1. 假如A组件注入B组件，B组件上使用了@RefreshScope并使用@Value获取配置，那么A组件上必须也加上@RefreshScope，否则无法实现动态刷新。
2. @RefreshScope 不能用在 @Scheduled、Listener、Timmer等类上，会有问题。

本组件没有上边这些失效的问题。

## 2 快速开始
### 2.1 引入依赖

```
<dependency>
    <groupId>com.suchtool</groupId>
    <artifactId>nicerefresh-spring-boot-starter</artifactId>
    <version>{newest-version}</version>
</dependency>

```

### 2.2 启用
默认不启用。启用方法是：在application.yml里添加配置：
```
suchtool:
  nicerefresh:
    enabled: true
```

## 3 详细配置

本组件支持使用SpringBoot配置文件进行配置，比如：application.yml。
| 配置                              | 描述       | 默认值            |
|-----------------------------------|------------|-------------------|
| suchtool.nicerefresh.enabled      | 是否启用       | false         |
| suchtool.nicerefresh.publish-event | 是否发布EnvironmentChangeEvent | false  |
| suchtool.nicerefresh.update-annotation  | 是否更新@Value的值 | true  |
| suchtool.nicerefresh.package-name | 需要自动刷新的包前缀。支持多个（逗号隔开，或者用-分行） | SpringBoot启动类所在的包 |
| suchtool.nicerefresh.debug  | 是否开启调试（开启后，会打印相关日志） | false  |

## 4 典型场景

### 4.1 Nacos

Nacos支持Property的更新，但不支持@Value的更新。（它会自动发布这个事件：EnvironmentChangeEvent）。
所以，针对Nacos的方案是：支持@Value的更新。

```
suchtool:
  nicerefresh:
    enabled: true
    update-annotation: true
    publish-event: false
```

### 4.2 Apollo

Apollo支持@Value的更新，不支持Property的更新。（它不自动发布这个事件：EnvironmentChangeEvent）。
所以，针对Apollo的方案是：监听Apollo独有的更新事件，发布出EnvironmentChangeEvent，Spring内部会自动更新Property。

```
suchtool:
  nicerefresh:
    enabled: true
    update-annotation: false
    publish-event: true
```

## 5 原理

更新@Value值的方案：启动时扫描bean，如果有@Value，就记下来。在配置发生变化时，通过反射去修改@Value标注的字段。
发布事件的方案：主要用于Apollo，监听Apollo专有的配置更新事件，发布为通用的是否发布EnvironmentChangeEvent

