/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.ContentEntityObject;

public interface Modification<T extends ContentEntityObject> {
    public void modify(T var1);
}

