/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.content.ui.ContentUiSupport
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.confluence.core.ContentEntityObject
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.sharepage;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.sharepage.ContentTypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="contentTypeResolver")
public class ContentTypeResolverImpl
implements ContentTypeResolver {
    private final ContentUiSupport<ConfluenceEntityObject> contentUiSupport;

    @Autowired
    public ContentTypeResolverImpl(ContentUiSupport<ConfluenceEntityObject> contentUiSupport) {
        this.contentUiSupport = contentUiSupport;
    }

    @Override
    public String getContentType(ContentEntityObject contentToShare) {
        if (contentToShare instanceof CustomContentEntityObject) {
            return this.contentUiSupport.getContentTypeI18NKey((ConfluenceEntityObject)contentToShare);
        }
        return contentToShare.getType();
    }
}

