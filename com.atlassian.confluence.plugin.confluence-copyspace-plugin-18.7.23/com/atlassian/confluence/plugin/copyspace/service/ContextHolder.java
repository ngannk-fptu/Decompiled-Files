/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.copyspace.service;

import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;

public interface ContextHolder {
    public void put(String var1, CopySpaceContext var2);

    public CopySpaceContext getContext(String var1);
}

