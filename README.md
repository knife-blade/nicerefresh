# nicerefresh
## 介绍
nicerefresh：自动刷新Spring项目中@Value的值。

## 快速开始
### 1.引入依赖

```
<dependency>
    <groupId>com.suchtool</groupId>
    <artifactId>nicerefresh-spring-boot-starter</artifactId>
    <version>{newest-version}</version>
</dependency>

```

### 2.启用
默认不启用。
启用方法是：在application.yml里添加配置：
```
suchtool:
  nicerefresh:
    enabled: true
```

## 原理

启动时扫描bean，如果有@Value，就记下来。在配置发生变化时，通过反射去修改@Value标注的字段。

## 为什么不用@RefreshScope？
@RefreshScope是有bug的，有很多失效的场景，比如：
1. 假如A组件注入B组件，B组件上使用了@RefreshScope并使用@Value获取配置，那么A组件上必须也加上@RefreshScope，否则无法实现动态刷新。
2. @RefreshScope 不能用在 @Scheduled、Listener、Timmer等类上，会有问题。

本组件没有上边这些失效的问题。

## 详细配置

本组件支持使用SpringBoot配置文件进行配置，比如：application.yml。

| 配置                              | 描述       | 默认值            |
|-----------------------------------|------------|-------------------|
| suchtool.nicerefresh.enabled      | 是否启用       | false         |
| suchtool.nicerefresh.package-name | 需要自动刷新的包前缀。支持多个（逗号隔开，或者用-分行） | SpringBoot启动类所在的包 |


