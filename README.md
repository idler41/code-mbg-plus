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

- MapperOverIgnorePlugin
	- xml文件存在时，不合并xml文件
- MapperPlugin
	- 删除xml文件指定方法，如: 项目集成了myabtis-plus，则xml文件不需要生成crud方法，该插件可将xml文件的所有crud方法都删除

- ExamplePlugin
	- 不生成example类 
- VelocityPlugin
	- 根据模板文件，为每个bean生成对应的文件，如: 对应的mvc

## 其它功能

### 扫描schema下的所有表

默认扫描schema下的所有表，生成对应的mvc文件。也可通过其他配置参数来决定扫描表的策略。

```java
## 生成指定表
include.tables=t_user,t_role
## 不生成指定表
exclude.tables=t_user
```
同时配置include.tables和exclude.tables时只有include.tables会生效。

### 去除表名前缀

```java
## t_user => User.java
remove.table.prefix=t_
```
### 去除表名后缀

```java
## t_user_16 => User.java
remove.table.prefix=t_
remove.table.suffix.regex=\\_\\d*$
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
### linux
project.home=/Users/apple/mywork/micro-service
### windows
#project.home=G:\\mbg-file
project.name=demo
## 生成domain包名
project.package.model=com.demo.domain
## 生成mapper包名
project.package.mapper=com.demo.mapper
## 模板路径
### linux
template.home=/Users/apple/GitHub/code-mbg-plus/src/main/resources/velocity
### windows
#template.home=G:\\mbg-file\\velocity

# 选填参数
project.path=${project.home}/${project.name}
project.path.java=${project.path}/src/main/java
project.path.resources=${project.path}/src/main/resources
project.path.test=${project.path}/src/main/test

## 启用指定插件
plugin.table-name.enable=true
plugin.example.enable=true
plugin.lombok.enable=true
plugin.mapper.enable=true
plugin.mapper-ignore.enable=true
plugin.velocity.enable=true

## 生成指定表
include.tables=
## 不生成指定表
exclude.tables=
remove.table.prefix=t_

## 去除表后缀, \\_\\d*$ (t_user_16 => t_user)
remove.table.suffix.regex=\\_\\d*$
## 分表场景，去除表后缀后可能存在重复表名, 设置为true则过滤掉重复的表
remove.table.repeat.name=true

## 生成的mapper方法
## BaseResultMap,Example_Where_Clause,Update_By_Example_Where_Clause,Base_Column_List,selectByExample,selectByPrimaryKey,deleteByPrimaryKey,deleteByExample,insert,insertSelective,countByExample,updateByExampleSelective,updateByExample,updateByPrimaryKeySelective,updateByPrimaryKey
mapper.id.include=BaseResultMap,Base_Column_List

file.ignore=false
mbg.plugin.po.enable=false
project.package.biz=com.demo.biz
project.package.controller=com.demo.controller
project.author=<a href="mailto:demo@163.con">demo</a>
```

## 执行命令

拷贝并修改配置文件init.properties

```shell
## 打jar包
> mvn clean install

## windows 执行
> java -Dinit.path=D:\init.properties -jar code-mbg-plus.jar

## linux 执行
> java -Dinit.path=/Users/apple/mywork/mbg-file/init.properties -jar code-mbg-plus.jar
```

## 手动添加模板示例

1. init.properties添加配置信息

```properties
project.package.controller=com.demo.controller
```

2. 在template.home路径添加模板文件：

Controller.java

```java
#set($bizPackage = $context.get("project.package.biz"))
#set($controllerPackage = $context.get("project.package.controller"))
#set ($domain = $!domainName.substring(0,1).toLowerCase()+$!domainName.substring(1))
package $!{controllerPackage};

import $!{bizPackage}.$!{domainName}Biz;
import $!{domainFullName};
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author $context.get("project.author")
 * @date $!{generateTime}
 */
@RestController("/$!{domain}")
@Slf4j
public class $!{domainName}Controller {

    @Autowired
    private $!{domainName}Biz $!{domain}Biz;
}
```

生成代码: XXController.java

```java
package com.demo.controller;

import com.demo.biz.ActPromotionActivityBiz;
import com.demo.domain.ActPromotionActivity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:demo@163.com">demo</a>
 * @date 2020-04-18 11:41:38
 */
@RestController("/actPromotionActivity")
@Slf4j
public class ActPromotionActivityController {

    @Autowired
    private ActPromotionActivityBiz actPromotionActivityBiz;
}
```


## TODO

1. 自动生成crud的junit测试文件，提高项目的测试覆盖率
2. 添加更多的模板
	- 带swagger和不带swagger注解的controller模板



	 