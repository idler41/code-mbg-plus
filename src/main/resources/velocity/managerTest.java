#set($bizPackage = $config.get("plugin.template.manager.package"))
#set($basePackage = $config.get("target.project.package.base"))
package $!{bizPackage};

import $!{basePackage}.base.AbstractSpringTest;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author $config.get("comment.author")
 * @date $config.get("generateTime")
 */
public class $!{domainName}ManagerTest extends AbstractSpringTest {

    @Autowired
    private $!{domainName}Manager manager;

    @BeforeClass
    public static void setup() {
        initSysProperty();
    }
}