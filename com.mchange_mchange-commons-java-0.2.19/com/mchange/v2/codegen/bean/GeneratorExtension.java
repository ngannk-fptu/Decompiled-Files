/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.codegen.bean;

import com.mchange.v2.codegen.IndentedWriter;
import com.mchange.v2.codegen.bean.ClassInfo;
import com.mchange.v2.codegen.bean.Property;
import java.io.IOException;
import java.util.Collection;

public interface GeneratorExtension {
    public Collection extraGeneralImports();

    public Collection extraSpecificImports();

    public Collection extraInterfaceNames();

    public void generate(ClassInfo var1, Class var2, Property[] var3, Class[] var4, IndentedWriter var5) throws IOException;
}

