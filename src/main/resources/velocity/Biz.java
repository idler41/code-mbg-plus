#set($mapperPackage = $context.get("project.package.mapper"))
#set($bizPackage = $context.get("project.package.biz"))
package $!{bizPackage};

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import $!{domainFullName};
import $!{mapperPackage}.$!{domainName}Mapper;
import org.springframework.stereotype.Service;

/**
 * @author $context.get("project.author")
 * @date $!{generateTime}
 */
@Service
public class $!{domainName}Biz extends ServiceImpl<$!{domainName}Mapper, $!{domainName}> {

}