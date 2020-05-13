#set($domainPackage = $context.get("project.package.model"))
package $!{domainPackage};

import lombok.Data;
import java.io.Serializable;
#foreach($fieldImport in $originClassParam.fieldImportList)
import $!{fieldImport};
#end

#foreach($javaDocLine in $originClassParam.javaDocLines)
$!{javaDocLine}
#end
@Data
public class $!{domainName} implements Serializable {
#foreach($originClassField in $originClassParam.originClassFieldList)

    /**
     *  $!{originClassField.fieldRemark}
     */
    private $!{originClassField.fieldType} $!{originClassField.fieldName};
#end
}