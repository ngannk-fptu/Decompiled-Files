/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.codegen.bean;

import com.mchange.v2.codegen.CodegenUtils;
import com.mchange.v2.codegen.IndentedWriter;
import com.mchange.v2.codegen.bean.BeangenUtils;
import com.mchange.v2.codegen.bean.ClassInfo;
import com.mchange.v2.codegen.bean.GeneratorExtension;
import com.mchange.v2.codegen.bean.Property;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class CopyConstructorGeneratorExtension
implements GeneratorExtension {
    int ctor_modifiers = 1;

    @Override
    public Collection extraGeneralImports() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Collection extraSpecificImports() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Collection extraInterfaceNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    public void generate(ClassInfo classInfo, Class clazz, Property[] propertyArray, Class[] classArray, IndentedWriter indentedWriter) throws IOException {
        indentedWriter.print(CodegenUtils.getModifierString(this.ctor_modifiers));
        indentedWriter.print(" " + classInfo.getClassName() + "( ");
        indentedWriter.print(classInfo.getClassName() + " copyMe");
        indentedWriter.println(" )");
        indentedWriter.println("{");
        indentedWriter.upIndent();
        int n = propertyArray.length;
        for (int i = 0; i < n; ++i) {
            String string = classArray[i] == Boolean.TYPE ? "is" + BeangenUtils.capitalize(propertyArray[i].getName()) + "()" : "get" + BeangenUtils.capitalize(propertyArray[i].getName()) + "()";
            indentedWriter.println(propertyArray[i].getSimpleTypeName() + ' ' + propertyArray[i].getName() + " = copyMe." + string + ';');
            indentedWriter.print("this." + propertyArray[i].getName() + " = ");
            String string2 = propertyArray[i].getDefensiveCopyExpression();
            if (string2 == null) {
                string2 = propertyArray[i].getName();
            }
            indentedWriter.println(string2 + ';');
        }
        indentedWriter.downIndent();
        indentedWriter.println("}");
    }
}

