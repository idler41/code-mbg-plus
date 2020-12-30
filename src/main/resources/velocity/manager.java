#set($mapperPackage = $config.get("target.project.package.mapper"))
#set($bizPackage = $config.get("plugin.template.manager.package"))
package $!{bizPackage};

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import $!{domainFullName};
import $!{mapperPackage}.$!{domainName}Mapper;
import org.springframework.stereotype.Service;

/**
 * @author $config.get("comment.author")
 * @date $config.get("generateTime")
 */
@Service
public class $!{domainName}Manager extends ServiceImpl<$!{domainName}Mapper, $!{domainName}> {

}