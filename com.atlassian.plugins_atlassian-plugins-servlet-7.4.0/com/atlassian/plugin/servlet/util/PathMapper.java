/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.servlet.util;

import java.util.Collection;

public interface PathMapper {
    public String get(String var1);

    public Collection<String> getAll(String var1);

    public void put(String var1, String var2);
}

