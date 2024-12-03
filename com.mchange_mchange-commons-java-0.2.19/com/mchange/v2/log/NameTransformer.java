/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.log;

public interface NameTransformer {
    public String transformName(String var1);

    public String transformName(Class var1);

    public String transformName();
}

