/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.jira.cache;

import java.io.Serializable;

public interface SimpleStringCache
extends Serializable {
    public void put(Object var1, String var2);

    public String get(Object var1);

    public void remove(Object var1);
}

