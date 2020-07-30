#set($myPackage = $context.get("plugin.template.converter.package"))
#set($voPackage = $context.get("plugin.template.vo.package"))
package $!{myPackage};

import $!{domainFullName};
import $!{voPackage}.$!{domainName}Vo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

#foreach($javaDocLine in $originClassParam.javaDocLines)
$!{javaDocLine}
#end
@Mapper
public interface $!{domainName}Converter {

        $!{domainName}Converter INSTANCE = Mappers.getMapper($!{domainName}Converter.class);

        $!{domainName}Vo doToVo($!{domainName} domain);

        List<$!{domainName}Vo> doListToVoList(List<$!{domainName}> list);
}