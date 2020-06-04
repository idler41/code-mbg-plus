#set($mapperPackage = $context.get("project.package.mapper"))
#set($bizPackage = $context.get("plugin.template.manager.package"))
package $!{bizPackage};

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import $!{domainFullName};
import $!{mapperPackage}.$!{domainName}Mapper;
import org.springframework.stereotype.Service;

/**
 * @author $context.get("project.author")
 * @date $!{generateTime}
 */
@Service
public class $!{domainName}Manager extends ServiceImpl<$!{domainName}Mapper, $!{domainName}> {

}