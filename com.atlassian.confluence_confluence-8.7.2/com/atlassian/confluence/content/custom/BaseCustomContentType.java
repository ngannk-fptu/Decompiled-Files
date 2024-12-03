/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 */
package com.atlassian.confluence.content.custom;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.apisupport.ApiSupportProvider;
import com.atlassian.confluence.content.apisupport.ContentTypeApiSupport;
import com.atlassian.confluence.content.custom.CustomContentType;
import com.atlassian.confluence.content.custom.NullContentTypeApiSupport;

public abstract class BaseCustomContentType
implements CustomContentType {
    private final ApiSupportProvider apiSupportProvider;
    private final ContentType contentType;

    public BaseCustomContentType(ContentType contentType, ApiSupportProvider apiSupportProvider) {
        this.apiSupportProvider = apiSupportProvider;
        this.contentType = contentType;
    }

    @Override
    public ContentTypeApiSupport<CustomContentEntityObject> getApiSupport() {
        return new NullContentTypeApiSupport(this.contentType, this.apiSupportProvider);
    }
}

