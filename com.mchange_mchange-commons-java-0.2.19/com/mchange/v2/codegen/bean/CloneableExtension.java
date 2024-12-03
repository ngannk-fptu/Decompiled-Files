/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.codegen.bean;

import com.mchange.v2.codegen.IndentedWriter;
import com.mchange.v2.codegen.bean.ClassInfo;
import com.mchange.v2.codegen.bean.GeneratorExtension;
import com.mchange.v2.codegen.bean.Property;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class CloneableExtension
implements GeneratorExtension {
    boolean export_public;
    boolean exception_swallowing;
    String mLoggerName = null;

    public boolean isExportPublic() {
        return this.export_public;
    }

    public void setExportPublic(boolean bl) {
        this.export_public = bl;
    }

    public boolean isExceptionSwallowing() {
        return this.exception_swallowing;
    }

    public void setExceptionSwallowing(boolean bl) {
        this.exception_swallowing = bl;
    }

    public String getMLoggerName() {
        return this.mLoggerName;
    }

    public void setMLoggerName(String string) {
        this.mLoggerName = string;
    }

    public CloneableExtension(boolean bl, boolean bl2) {
        this.export_public = bl;
        this.exception_swallowing = bl2;
    }

    public CloneableExtension() {
        this(true, false);
    }

    @Override
    public Collection extraGeneralImports() {
        return this.mLoggerName == null ? Collections.EMPTY_SET : Arrays.asList("com.mchange.v2.log");
    }

    @Override
    public Collection extraSpecificImports() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Collection extraInterfaceNames() {
        HashSet<String> hashSet = new HashSet<String>();
        hashSet.add("Cloneable");
        return hashSet;
    }

    @Override
    public void generate(ClassInfo classInfo, Class clazz, Property[] propertyArray, Class[] classArray, IndentedWriter indentedWriter) throws IOException {
        if (this.export_public) {
            indentedWriter.print("public Object clone()");
            if (!this.exception_swallowing) {
                indentedWriter.println(" throws CloneNotSupportedException");
            } else {
                indentedWriter.println();
            }
            indentedWriter.println("{");
            indentedWriter.upIndent();
            if (this.exception_swallowing) {
                indentedWriter.println("try");
                indentedWriter.println("{");
                indentedWriter.upIndent();
            }
            indentedWriter.println("return super.clone();");
            if (this.exception_swallowing) {
                indentedWriter.downIndent();
                indentedWriter.println("}");
                indentedWriter.println("catch (CloneNotSupportedException e)");
                indentedWriter.println("{");
                indentedWriter.upIndent();
                if (this.mLoggerName == null) {
                    indentedWriter.println("e.printStackTrace();");
                } else {
                    indentedWriter.println("if ( " + this.mLoggerName + ".isLoggable( MLevel.FINE ) )");
                    indentedWriter.upIndent();
                    indentedWriter.println(this.mLoggerName + ".log( MLevel.FINE, \"Inconsistent clone() definitions between subclass and superclass! \", e );");
                    indentedWriter.downIndent();
                }
                indentedWriter.println("throw new RuntimeException(\"Inconsistent clone() definitions between subclass and superclass! \" + e);");
                indentedWriter.downIndent();
                indentedWriter.println("}");
            }
            indentedWriter.downIndent();
            indentedWriter.println("}");
        }
    }
}

