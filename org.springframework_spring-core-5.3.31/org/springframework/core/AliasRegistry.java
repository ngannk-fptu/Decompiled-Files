/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core;

public interface AliasRegistry {
    public void registerAlias(String var1, String var2);

    public void removeAlias(String var1);

    public boolean isAlias(String var1);

    public String[] getAliases(String var1);
}

