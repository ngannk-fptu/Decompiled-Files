/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content;

import com.atlassian.confluence.content.ContentEntityAdapter;
import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.security.PermissionDelegate;

public interface ContentType {
    public ContentEntityAdapter getContentAdapter();

    public PermissionDelegate getPermissionDelegate();

    public ContentUiSupport getContentUiSupport();
}

