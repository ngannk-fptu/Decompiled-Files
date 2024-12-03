/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 */
package com.atlassian.confluence.content.custom;

import com.atlassian.confluence.content.ContentEntityAdapter;
import com.atlassian.confluence.content.ContentType;
import com.atlassian.confluence.content.apisupport.ApiSupportProvider;
import com.atlassian.confluence.content.custom.BaseCustomContentType;
import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.security.PermissionDelegate;

public class CustomContentTypeWrapper
extends BaseCustomContentType {
    private final ContentType delegateContentType;

    public CustomContentTypeWrapper(ContentType delegateContentType, com.atlassian.confluence.api.model.content.ContentType apiContentType, ApiSupportProvider supportProvider) {
        super(apiContentType, supportProvider);
        this.delegateContentType = delegateContentType;
    }

    @Override
    public ContentEntityAdapter getContentAdapter() {
        return this.delegateContentType.getContentAdapter();
    }

    @Override
    public PermissionDelegate getPermissionDelegate() {
        return this.delegateContentType.getPermissionDelegate();
    }

    @Override
    public ContentUiSupport getContentUiSupport() {
        return this.delegateContentType.getContentUiSupport();
    }
}

