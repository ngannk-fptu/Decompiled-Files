/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.model.pagination;

import com.atlassian.confluence.api.model.pagination.CursorType;

public interface Cursor {
    public boolean isReverse();

    public boolean isEmpty();

    public CursorType getCursorType();
}

