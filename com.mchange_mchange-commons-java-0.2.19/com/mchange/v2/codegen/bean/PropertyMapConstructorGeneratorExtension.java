/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.codegen.bean;

import com.mchange.v2.codegen.CodegenUtils;
import com.mchange.v2.codegen.IndentedWriter;
import com.mchange.v2.codegen.bean.ClassInfo;
import com.mchange.v2.codegen.bean.GeneratorExtension;
import com.mchange.v2.codegen.bean.Property;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class PropertyMapConstructorGeneratorExtension
implements GeneratorExtension {
    int ctor_modifiers = 1;

    @Override
    public Collection extraGeneralImports() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Collection extraSpecificImports() {
        HashSet<String> hashSet = new HashSet<String>();
        hashSet.add("java.util.Map");
        return hashSet;
    }

    @Override
    public Collection extraInterfaceNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    public void generate(ClassInfo classInfo, Class clazz, Property[] propertyArray, Class[] classArray, IndentedWriter indentedWriter) throws IOException {
        indentedWriter.print(CodegenUtils.getModifierString(this.ctor_modifiers));
        indentedWriter.print(' ' + classInfo.getClassName() + "( Map map )");
        indentedWriter.println("{");
        indentedWriter.upIndent();
        indentedWriter.println("Object raw;");
        int n = propertyArray.length;
        for (int i = 0; i < n; ++i) {
            Property property = propertyArray[i];
            String string = property.getName();
            Class clazz2 = classArray[i];
            indentedWriter.println("raw = map.get( \"" + string + "\" );");
            indentedWriter.println("if (raw != null)");
            indentedWriter.println("{");
            indentedWriter.upIndent();
            indentedWriter.print("this." + string + " = ");
            if (clazz2 == Boolean.TYPE) {
                indentedWriter.println("((Boolean) raw ).booleanValue();");
            } else if (clazz2 == Byte.TYPE) {
                indentedWriter.println("((Byte) raw ).byteValue();");
            } else if (clazz2 == Character.TYPE) {
                indentedWriter.println("((Character) raw ).charValue();");
            } else if (clazz2 == Short.TYPE) {
                indentedWriter.println("((Short) raw ).shortValue();");
            } else if (clazz2 == Integer.TYPE) {
                indentedWriter.println("((Integer) raw ).intValue();");
            } else if (clazz2 == Long.TYPE) {
                indentedWriter.println("((Long) raw ).longValue();");
            } else if (clazz2 == Float.TYPE) {
                indentedWriter.println("((Float) raw ).floatValue();");
            } else if (clazz2 == Double.TYPE) {
                indentedWriter.println("((Double) raw ).doubleValue();");
            }
            indentedWriter.println("raw = null;");
            indentedWriter.downIndent();
            indentedWriter.println("}");
        }
        indentedWriter.downIndent();
        indentedWriter.println("}");
    }
}

