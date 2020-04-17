# 简介

基于mybatis generator + velocity的代码生成器。通过自定义模板，来定制自己需要的代码。

## 自定义插件

- LombokPlugin
	- 实体类添加lombok注解

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MqMessage implements Serializable {
```
- DomainTablePlugin
	- domain类添加mybatis-plus注解@TableName，默认关闭，可通过参数domain.table.enable=true开启

```java
@TableName("mq_message")
public class MqMessage implements Serializable {
```

- MapperOverIsMergeablePlugin
	- xml文件存在时，不合并xml文件
- MapperPlugin
	- 删除xml文件指定方法，如: 项目集成了myabtis-plus，则xml文件不需要生成crud方法，该插件可将xml文件的所有crud方法都删除

- ExamplePlugin
	- 不生成example类 
- VelocityPlugin
	- 根据模板文件，为每个bean生成对应的文件，如: 对应的mvc

## 其它功能

### 扫描数据库的所有表

默认扫描数据库的所有表，对数据库下的所有表生成对应的mvc文件。也可通过其他配置参数来决定扫描表的策略。

```properties
## 生成指定表
include.tables=t_user,t_role
## 不生成指定表
exclude.tables=t_user
```
同时配置include.tables和exclude.tables时只有include.tables会生效。

### 去除表名前缀

如表t_user生成实体类User.java

```properties
remove.table.prefix=t_
```

### XML文件格式

xml文件缩进由2个空格改为4个空格

### 添加字段备注

所有的domain类都自动生成备注数据，如: 

```java
    /**
     * 消息
     */
    private String message;

    /**
     * 消息状态: 0 => 未投递, 1 => 投递成功
     */
    private Integer messageStatus;

    /**
     * 用户ID
     */
    private Integer userId;
```

### 添加作者信息

所有class文件添加如下文件头, 可通过project.author指定@author value

```java
/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2020-04-13 23:28:56
 */
 public class XXX

```

### 自动创建目录

目标路径的父目录不存在时，会自动创建目录

## 启动参数文件模板

```properties
# 必填参数
jdbc.driver=com.mysql.cj.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/oms?useUnicode=true&characterEncoding=UTF-8
jdbc.username=root
jdbc.password=root
## 生成项目路径
project.home=/Users/apple/mywork/micro-service
project.name=demo
## 生成domain包名
project.package.model=com.demo.domain
## 生成mapper包名
project.package.mapper=com.demo.mapper
## 模板路径
template.home=/Users/apple/GitHub/code-mbg-plus/src/main/resources/velocity


# 选填参数
project.path=${project.home}/${project.name}
project.path.java=${project.path}/src/main/java
project.path.resources=${project.path}/src/main/resources
project.path.test=${project.path}/src/main/test

## 生成指定表
include.tables=
## 不生成指定表
exclude.tables=
domain.table.enable=false
remove.table.prefix=

file.ignore=false
mbg.plugin.po.enable=false
project.package.biz=
```

## 执行命令

```shell

拷贝init.properties

> mvn clean install

## windows
> java -Dinit.path=D:\init.properties -jar code-mbg-plus.jar

## linux
> java -Dinit.path=/Users/apple/mywork/mbg-file/init.properties -jar code-mbg-plus.jar
```

## TODO

1. 区分单库单表与分库分表场景
	- 分库分表场景下,去除表后缀 t_user1生成对应的类名User.java
	- 分库分表场景下@TableName注解为逻辑表名
2. 自动生成crud的junit测试文件，提高项目的测试覆盖率
3. 添加更多的模板
	- 带swagger和不带swagger注解的controller模板



	 