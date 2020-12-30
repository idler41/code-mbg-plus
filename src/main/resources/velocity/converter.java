#set($myPackage = $config.get("plugin.template.converter.package"))
package $!{myPackage};

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

#foreach($javaDocLine in $originClassParam.javaDocLines)
$!{javaDocLine}
#end
@Mapper
public interface $!{domainName}Converter {

    $!{domainName}Converter INSTANCE = Mappers.getMapper($!{domainName}Converter.class);
}