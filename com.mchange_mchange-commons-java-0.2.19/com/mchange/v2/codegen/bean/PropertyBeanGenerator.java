/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.codegen.bean;

import com.mchange.v2.codegen.bean.ClassInfo;
import com.mchange.v2.codegen.bean.Property;
import java.io.IOException;
import java.io.Writer;

public interface PropertyBeanGenerator {
    public void generate(ClassInfo var1, Property[] var2, Writer var3) throws IOException;
}

