/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.diff;

import com.atlassian.confluence.core.ContentEntityObject;

public interface Differ {
    public String diff(ContentEntityObject var1, ContentEntityObject var2);
}

