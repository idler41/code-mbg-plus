#set($mapperPackage = $config.get("target.project.package.mapper"))
package $!{mapperPackage};

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import $!{domainFullName};
import org.springframework.stereotype.Repository;

/**
 * @author $config.get("comment.author")
 * @date $config.get("generateTime")
 */
@Repository
public interface $!{domainName}Mapper extends BaseMapper<$!{domainName}> {

}