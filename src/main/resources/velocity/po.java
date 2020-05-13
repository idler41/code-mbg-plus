#set($poPackage = $context.get("project.package.po"))
package $!{poPackage};

import lombok.Data;
import java.io.Serializable;
#foreach($fieldImport in $originClassParam.fieldImportList)
import $!{fieldImport};
#end

#foreach($javaDocLine in $originClassParam.javaDocLines)
$!{javaDocLine}
#end
@Data
public class $!{domainName}Po implements Serializable {
#foreach($originClassField in $originClassParam.originClassFieldList)

    /**
     *  $!{originClassField.fieldRemark}
     */
    private $!{originClassField.fieldType} $!{originClassField.fieldName};
#end
}