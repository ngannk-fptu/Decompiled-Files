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
import com.mchange.v2.codegen.bean.SimplePropertyMask;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class StateBeanImportExportGeneratorExtension
implements GeneratorExtension {
    int ctor_modifiers = 1;

    @Override
    public Collection extraGeneralImports() {
        return Arrays.asList("com.mchange.v2.bean");
    }

    @Override
    public Collection extraSpecificImports() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Collection extraInterfaceNames() {
        return Arrays.asList("StateBeanExporter");
    }

    @Override
    public void generate(ClassInfo classInfo, Class clazz, Property[] propertyArray, Class[] classArray, IndentedWriter indentedWriter) throws IOException {
        String string;
        int n;
        String string2 = classInfo.getClassName();
        int n2 = propertyArray.length;
        Property[] propertyArray2 = new Property[n2];
        for (n = 0; n < n2; ++n) {
            propertyArray2[n] = new SimplePropertyMask(propertyArray[n]);
        }
        indentedWriter.println("protected class MyStateBean implements StateBean");
        indentedWriter.println("{");
        indentedWriter.upIndent();
        for (n = 0; n < n2; ++n) {
            propertyArray2[n] = new SimplePropertyMask(propertyArray[n]);
            BeangenUtils.writePropertyMember(propertyArray2[n], indentedWriter);
            indentedWriter.println();
            BeangenUtils.writePropertyGetter(propertyArray2[n], indentedWriter);
            indentedWriter.println();
            BeangenUtils.writePropertySetter(propertyArray2[n], indentedWriter);
        }
        indentedWriter.println();
        indentedWriter.downIndent();
        indentedWriter.println("}");
        indentedWriter.println();
        indentedWriter.println("public StateBean exportStateBean()");
        indentedWriter.println("{");
        indentedWriter.upIndent();
        indentedWriter.println("MyStateBean out = createEmptyStateBean();");
        for (n = 0; n < n2; ++n) {
            string = BeangenUtils.capitalize(propertyArray[n].getName());
            indentedWriter.println("out.set" + string + "( this." + (classArray[n] == Boolean.TYPE ? "is" : "get") + string + "() );");
        }
        indentedWriter.println("return out;");
        indentedWriter.downIndent();
        indentedWriter.println("}");
        indentedWriter.println();
        indentedWriter.println("public void importStateBean( StateBean bean )");
        indentedWriter.println("{");
        indentedWriter.upIndent();
        indentedWriter.println("MyStateBean msb = (MyStateBean) bean;");
        for (n = 0; n < n2; ++n) {
            string = BeangenUtils.capitalize(propertyArray[n].getName());
            indentedWriter.println("this.set" + string + "( msb." + (classArray[n] == Boolean.TYPE ? "is" : "get") + string + "() );");
        }
        indentedWriter.downIndent();
        indentedWriter.println("}");
        indentedWriter.println();
        indentedWriter.print(CodegenUtils.getModifierString(this.ctor_modifiers));
        indentedWriter.println(" " + string2 + "( StateBean bean )");
        indentedWriter.println("{ importStateBean( bean ); }");
        indentedWriter.println("protected MyStateBean createEmptyStateBean() throws StateBeanException");
        indentedWriter.println("{ return new MyStateBean(); }");
    }
}

