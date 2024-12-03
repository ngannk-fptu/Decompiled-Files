/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.codegen.bean;

import com.mchange.v2.codegen.IndentedWriter;
import com.mchange.v2.codegen.bean.ClassInfo;
import com.mchange.v2.codegen.bean.GeneratorExtension;
import com.mchange.v2.codegen.bean.Property;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class PropsToStringGeneratorExtension
implements GeneratorExtension {
    private Collection excludePropNames = null;

    public void setExcludePropertyNames(Collection collection) {
        this.excludePropNames = collection;
    }

    public Collection getExcludePropertyNames() {
        return this.excludePropNames;
    }

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
        indentedWriter.println("public String toString()");
        indentedWriter.println("{");
        indentedWriter.upIndent();
        indentedWriter.println("StringBuffer sb = new StringBuffer();");
        indentedWriter.println("sb.append( super.toString() );");
        indentedWriter.println("sb.append(\" [ \");");
        int n = propertyArray.length;
        for (int i = 0; i < n; ++i) {
            Property property = propertyArray[i];
            if (this.excludePropNames != null && this.excludePropNames.contains(property.getName())) continue;
            indentedWriter.println("sb.append( \"" + property.getName() + " -> \" + " + property.getName() + " );");
            if (i == n - 1) continue;
            indentedWriter.println("sb.append( \", \");");
        }
        indentedWriter.println();
        indentedWriter.println("String extraToStringInfo = this.extraToStringInfo();");
        indentedWriter.println("if (extraToStringInfo != null)");
        indentedWriter.upIndent();
        indentedWriter.println("sb.append( extraToStringInfo );");
        indentedWriter.downIndent();
        indentedWriter.println("sb.append(\" ]\");");
        indentedWriter.println("return sb.toString();");
        indentedWriter.downIndent();
        indentedWriter.println("}");
        indentedWriter.println();
        indentedWriter.println("protected String extraToStringInfo()");
        indentedWriter.println("{ return null; }");
    }
}

