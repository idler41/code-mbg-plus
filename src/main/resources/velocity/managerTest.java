#set($bizPackage = $context.get("plugin.template.manager.package"))
#set($basePackage = $context.get("project.package.base"))
package $!{bizPackage};

import $!{basePackage}.base.AbstractSpringTest;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author $context.get("project.author")
 * @date $!{generateTime}
 */
public class $!{domainName}ManagerTest extends AbstractSpringTest {

    @Autowired
    private $!{domainName}Manager manager;

    @BeforeClass
    public static void setup() {
        initSysProperty();
    }
}