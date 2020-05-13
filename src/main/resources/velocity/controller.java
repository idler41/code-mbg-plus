#set($bizPackage = $context.get("plugin.template.biz.package"))
#set($controllerPackage = $context.get("plugin.template.controller.package"))
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