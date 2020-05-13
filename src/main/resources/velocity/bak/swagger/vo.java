#set($voPackage = $context.get("plugin.template.vo.package"))
package $!{voPackage};

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
#foreach($fieldImport in $originClassParam.fieldImportList)
import $!{fieldImport};
#end

#foreach($javaDocLine in $originClassParam.javaDocLines)
$!{javaDocLine}
#end
@Data
@ApiModel(value = "$!{originClassParam.tableRemark}")
public class $!{domainName}Vo implements Serializable {
#foreach($originClassField in $originClassParam.originClassFieldList)

    /**
     *  $!{originClassField.fieldRemark}
     */
    @ApiModelProperty(value = "$!{originClassField.fieldRemark}")
    private $!{originClassField.fieldType} $!{originClassField.fieldName};
#end
}