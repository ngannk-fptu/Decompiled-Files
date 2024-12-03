/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.expression;

public interface Resolver {
    public int getIndex(String var1);

    public String getKey(String var1);

    public String getProperty(String var1);

    public boolean hasNested(String var1);

    public boolean isIndexed(String var1);

    public boolean isMapped(String var1);

    public String next(String var1);

    public String remove(String var1);
}

