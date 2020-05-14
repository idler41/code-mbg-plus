# 简介

基于mybatis generator + velocity的代码生成器。通过自定义模板，来定制自己需要的代码。

## 自定义插件

- MapperOverIgnorePlugin
	- xml文件存在时，不合并xml文件
- MapperPlugin
	- 删除xml文件指定方法，如: 项目集成了myabtis-plus，则xml文件不需要生成crud方法，该插件可将xml文件的所有crud方法都删除
- ExamplePlugin
	- 不生成example类 
- LombokPlugin
	- 实体类添加lombok注解
- VelocityPlugin
	- 根据模板文件，为每个bean生成对应的文件，如: 对应的mvc
	- resources/velocity目录下有模板示例


## 自定义功能

### 扫描schema下的所有表

默认扫描schema下的所有表，生成对应的mvc文件。也可通过其他配置参数来决定扫描表的策略。

```properties
## 生成指定表
tables.include=t_user,t_role
## 不生成指定表
tables.exclude=t_user
```
同时配置tables.include和tables.exclude时只有tables.include会生效。

### 表名特殊处理

```properties
## 去除表前缀
table.remove.prefix=t_
## 去除表后缀, \\_\\d*$ (t_user_16 => t_user)
table.remove.suffix.regex=\\_\\d*$
```

### 配置mapper.xml生成指定元素

```
## BaseResultMap,Example_Where_Clause,Update_By_Example_Where_Clause,Base_Column_List,selectByExample,selectByPrimaryKey,deleteByPrimaryKey,deleteByExample,insert,insertSelective,countByExample,updateByExampleSelective,updateByExample,updateByPrimaryKeySelective,updateByPrimaryKey
mapper.id.include=BaseResultMap,Base_Column_List
```


### 生成字段备注

所有的domain类或模板都可生成备注信息，如: 

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

```properties
project.author=<a href="mailto:demo@163.con">demo</a>
```

```java
/**
 * @author <a href="mailto:demo@163.com">demo</a>
 * @date 2020-04-13 23:28:56
 */
 public class XXX

```

### XML文件格式

xml文件缩进由2个空格改为4个空格

### 自动创建目录

目标路径的父目录不存在时，会自动创建目录

## 启动参数文件模板

```properties
############################ 必填参数 ############################
jdbc.driver=com.mysql.cj.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/oms?useUnicode=true&characterEncoding=UTF-8
jdbc.username=root
jdbc.password=root

## 模板路径
### linux
template.home=/Users/apple/GitHub/code-mbg-plus/src/main/resources/velocity
### windows
#template.home=G:\\mbg-file\\velocity
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

project.path=${project.home}/${project.name}
project.path.java=${project.path}/src/main/java
project.path.resources=${project.path}/src/main/resources
project.path.test=${project.path}/src/main/test
############################ 必填参数 ############################

############################ 选填参数 ############################
project.author=<a href="mailto:demo@163.con">demo</a>
file.ignore=true
## 生成的mapper方法
## BaseResultMap,Example_Where_Clause,Update_By_Example_Where_Clause,Base_Column_List,selectByExample,selectByPrimaryKey,deleteByPrimaryKey,deleteByExample,insert,insertSelective,countByExample,updateByExampleSelective,updateByExample,updateByPrimaryKeySelective,updateByPrimaryKey
mapper.id.include=BaseResultMap,Base_Column_List

## 生成指定表
tables.include=
## 不生成指定表
tables.exclude=
## 去除表前缀
table.remove.prefix=t_
## 去除表后缀, \\_\\d*$ (t_user_16 => t_user)
table.remove.suffix.regex=\\_\\d*$

## 启用指定插件
plugin.example.enable=true
plugin.lombok.enable=true
plugin.mapper.enable=true
plugin.mapper-ignore.enable=true
plugin.template.enable=true

### velocity启动模板
plugin.template.biz.enable=true
plugin.template.biz.package=com.demo.biz
plugin.template.mapper.enable=true
plugin.template.mapper.package=${project.package.mapper}
plugin.template.model.enable=true
plugin.template.model.package=${project.package.model}
plugin.template.controller.enable=true
plugin.template.controller.package=com.demo.controller
plugin.template.po.enable=true
plugin.template.po.package=com.demo.controller.po
plugin.template.po.field.exclude=createTime,updateTime
plugin.template.vo.enable=true
plugin.template.vo.package=com.demo.controller.vo
plugin.template.vo.field.exclude=

plugin.template.field.exclude.filter=po,vo
############################ 选填参数 ############################
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

## 模板详解

### 添加新模板步骤

1. 在配置属性template.home的路径下添加模板文件
2. 添加模板参数配置(假如该模板文件名称为xx.java)
	1. 启用该插件: plugin.template.xx.enable=true
	2. 模板生成到指定package： plugin.template.xx.package=

### 特殊模板: model.java

在template.home的路径下添加名称为model.java的模板文件

配置参数启用:

```properties
plugin.template.model.enable=true
```

该模板启用后，mbg不会为每个表生成domain文件，而是改为模板插件生成domain文件。 如果关闭，则mbg会为每个表生成domain文件。 

插件方式生成domain文件相比mbg优点: 自由度更高,可以在不更改代码的情况下，随时更改模板

## TODO

1. 添加junit模板文件，并在指定路径生成，提高项目的测试覆盖率



	 