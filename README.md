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
jdbc.driver=com.mysql.cj.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/mall_activity?useUnicode=true&characterEncoding=UTF-8
# jdbc.url=jdbc:mysql://localhost:3306/shard_mall_activity_0?useUnicode=true&characterEncoding=UTF-8
jdbc.username=root
jdbc.password=123456

## 生成指定表
scan.table.include=
## 不生成指定表
scan.table.exclude=
## 去除表前缀
scan.table.prefix.remove=t_
## 去除表后缀, \\_\\d*$ (t_user_16 => t_user)
scan.table.suffix.remove.regex=\\_\\d*$

## 生成文件，文件存在时处理策略: ignore-忽略 override-覆盖
target.file.exist.strategy=ignore
## 生成项目home路径
target.project.home=E:/workspace/mall-marketing
## 生成项目名称
target.project.name=mall-marketing-persistence
## 生成项目路径
target.project.path=${target.project.home}/${target.project.name}
## 生成项目java文件路径
target.project.path.java=${target.project.path}/src/main/java
## 生成项目资源文件路径
target.project.path.resources=${target.project.path}/src/main/resources
## 生成项目测试文件路径
target.project.path.test=${target.project.path}/src/test/java

## 项目基础包名
target.project.package.base=com.lfx.mall.marketing.persistence
## 生成domain包名
target.project.package.model=${target.project.package.base}.entity
## 生成mapper包名
target.project.package.mapper=${target.project.package.base}.dao

## xml生成路径
target.mapperXml.path=${target.project.path.resources}/mapper
## 生成xml文件可包含元素
## BaseResultMap,Example_Where_Clause,Update_By_Example_Where_Clause,Base_Column_List,selectByExample,selectByPrimaryKey,deleteByPrimaryKey,deleteByExample,insert,insertSelective,countByExample,updateByExampleSelective,updateByExample,updateByPrimaryKeySelective,updateByPrimaryKey
target.mapperXml.element.include=BaseResultMap,Base_Column_List

## 作者信息备注
comment.author=<a href="mailto:idler41@163.con">idler41</a>

## 启用自定义插件
plugin.example.enable=true
plugin.lombok.enable=true
plugin.mapper.enable=true
plugin.mapper-ignore.enable=true
plugin.template.enable=true

## 模板路径
plugin.template.home=D:/mbg/mall-groupshopping/velocity

# 模板插件约定配置: plugin.template.模板文件名(不包含后缀).模板自定义属性(模板引擎可访问)

## manager.java模板
plugin.template.manager.enable=true
plugin.template.manager.package=${target.project.package.base}.manager
## mapper.java模板
plugin.template.mapper.enable=true
plugin.template.mapper.package=${target.project.package.mapper}
## model.java模板
plugin.template.model.enable=true
plugin.template.model.package=${target.project.package.model}
## controller.java模板
plugin.template.controller.enable=true
plugin.template.controller.package=${target.project.package.base}.controller
## po.java模板
plugin.template.po.enable=true
plugin.template.po.package=${target.project.package.base}.controller.po
plugin.template.po.field.exclude=createTime,updateTime
## vo.java模板
plugin.template.vo.enable=true
plugin.template.vo.package=${target.project.package.base}.controller.vo
plugin.template.vo.field.exclude=updateTime
## convert.java模板
plugin.template.converter.enable=true
plugin.template.converter.package=${target.project.package.base}.converter
## managerTest.java模板
plugin.template.managerTest.enable=true
plugin.template.managerTest.package=${plugin.template.manager.package}
```

## 执行命令

拷贝并修改配置文件init.properties

```shell
## 打jar包
> mvn clean assembly:assembly -Dmaven.test.skip

## windows 执行
> java -Dinit.path=D:\init.properties -jar mbg-plus-jar-with-dependencies.jar

## linux 执行
> java -Dinit.path=/Users/apple/mywork/mbg-file/init.properties -jar mbg-plus-jar-with-dependencies.jar
```

## 模板详解

### 添加新模板步骤

1. 在配置属性plugin.template.home的路径下添加模板文件
2. 添加模板参数配置(假如该模板文件名称为xx.java)
	1. 启用该插件: plugin.template.xx.enable=true
	2. 模板生成到指定package： plugin.template.xx.package=

### 特殊模板: model.java

1. 在template.home的路径下添加名称为model.java的模板文件

2. 配置参数: ```properties plugin.template.model.enable=true ```

该模板启用后，mbg不会为每个表生成domain文件，而是改为模板插件生成domain文件。 如果关闭，则mbg会为每个表生成domain文件。 

模板插件方式生成domain文件相比mbg优点: 自由度更高,可以在不更改代码的情况下，随时更改模板

## TODO

1. 添加junit模板文件，并在指定路径生成，提高项目的测试覆盖率



	 