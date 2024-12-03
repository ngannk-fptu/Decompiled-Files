/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.seraph.util;

import java.util.Collection;

public interface IPathMapper {
    public String get(String var1);

    public Collection<String> getAll(String var1);

    public void put(String var1, String var2);
}

