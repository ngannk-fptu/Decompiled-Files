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
import java.util.HashSet;

public class BeanExtractingGeneratorExtension
implements GeneratorExtension {
    int ctor_modifiers = 1;
    int method_modifiers = 2;

    public void setConstructorModifiers(int n) {
        this.ctor_modifiers = n;
    }

    public int getConstructorModifiers() {
        return this.ctor_modifiers;
    }

    public void setExtractMethodModifiers(int n) {
        this.method_modifiers = n;
    }

    public int getExtractMethodModifiers() {
        return this.method_modifiers;
    }

    @Override
    public Collection extraGeneralImports() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Collection extraSpecificImports() {
        HashSet<String> hashSet = new HashSet<String>();
        hashSet.add("java.beans.BeanInfo");
        hashSet.add("java.beans.PropertyDescriptor");
        hashSet.add("java.beans.Introspector");
        hashSet.add("java.beans.IntrospectionException");
        hashSet.add("java.lang.reflect.InvocationTargetException");
        return hashSet;
    }

    @Override
    public Collection extraInterfaceNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    public void generate(ClassInfo classInfo, Class clazz, Property[] propertyArray, Class[] classArray, IndentedWriter indentedWriter) throws IOException {
        indentedWriter.println("private static Class[] NOARGS = new Class[0];");
        indentedWriter.println();
        indentedWriter.print(CodegenUtils.getModifierString(this.method_modifiers));
        indentedWriter.print(" void extractPropertiesFromBean( Object bean ) throws InvocationTargetException, IllegalAccessException, IntrospectionException");
        indentedWriter.println("{");
        indentedWriter.upIndent();
        indentedWriter.println("BeanInfo bi = Introspector.getBeanInfo( bean.getClass() );");
        indentedWriter.println("PropertyDescriptor[] pds = bi.getPropertyDescriptors();");
        indentedWriter.println("for (int i = 0, len = pds.length; i < len; ++i)");
        indentedWriter.println("{");
        indentedWriter.upIndent();
        int n = propertyArray.length;
        for (int i = 0; i < n; ++i) {
            indentedWriter.println("if (\"" + propertyArray[i].getName() + "\".equals( pds[i].getName() ) )");
            indentedWriter.upIndent();
            indentedWriter.println("this." + propertyArray[i].getName() + " = " + this.extractorExpr(propertyArray[i], classArray[i]) + ';');
            indentedWriter.downIndent();
        }
        indentedWriter.println("}");
        indentedWriter.downIndent();
        indentedWriter.println("}");
        indentedWriter.println();
        indentedWriter.print(CodegenUtils.getModifierString(this.ctor_modifiers));
        indentedWriter.println(' ' + classInfo.getClassName() + "( Object bean ) throws InvocationTargetException, IllegalAccessException, IntrospectionException");
        indentedWriter.println("{");
        indentedWriter.upIndent();
        indentedWriter.println("extractPropertiesFromBean( bean );");
        indentedWriter.downIndent();
        indentedWriter.println("}");
    }

    private String extractorExpr(Property property, Class clazz) {
        if (clazz.isPrimitive()) {
            String string = BeangenUtils.capitalize(property.getSimpleTypeName());
            String string2 = property.getSimpleTypeName() + "Value()";
            if (clazz == Character.TYPE) {
                string = "Character";
            } else if (clazz == Integer.TYPE) {
                string = "Integer";
            }
            return "((" + string + ") pds[i].getReadMethod().invoke( bean, NOARGS ))." + string2;
        }
        return "(" + property.getSimpleTypeName() + ") pds[i].getReadMethod().invoke( bean, NOARGS )";
    }
}

