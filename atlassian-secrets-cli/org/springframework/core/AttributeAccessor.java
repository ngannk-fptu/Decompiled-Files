/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core;

import org.springframework.lang.Nullable;

public interface AttributeAccessor {
    public void setAttribute(String var1, @Nullable Object var2);

    @Nullable
    public Object getAttribute(String var1);

    @Nullable
    public Object removeAttribute(String var1);

    public boolean hasAttribute(String var1);

    public String[] attributeNames();
}

