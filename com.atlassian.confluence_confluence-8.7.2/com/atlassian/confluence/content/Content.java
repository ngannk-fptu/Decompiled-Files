/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content;

import com.atlassian.confluence.core.ContentEntityObject;

public interface Content {
    public static final Long UNSET = 0L;

    public ContentEntityObject getEntity();
}

