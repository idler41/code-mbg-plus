#set($mapperPackage = $context.get("project.package.mapper"))
package $!{mapperPackage};

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import $!{domainFullName};
import org.springframework.stereotype.Repository;

/**
 * @author $context.get("project.author")
 * @date $!{generateTime}
 */
@Repository
public interface $!{domainName}Mapper extends BaseMapper<$!{domainName}> {

}