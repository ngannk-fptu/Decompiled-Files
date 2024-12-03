/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.migration;

public interface BatchTask<T> {
    public boolean apply(T var1, int var2, int var3) throws Exception;
}

