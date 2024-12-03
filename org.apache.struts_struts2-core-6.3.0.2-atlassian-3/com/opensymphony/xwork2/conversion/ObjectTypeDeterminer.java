/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.conversion;

public interface ObjectTypeDeterminer {
    public Class getKeyClass(Class var1, String var2);

    public Class getElementClass(Class var1, String var2, Object var3);

    public String getKeyProperty(Class var1, String var2);

    public boolean shouldCreateIfNew(Class var1, String var2, Object var3, String var4, boolean var5);
}

