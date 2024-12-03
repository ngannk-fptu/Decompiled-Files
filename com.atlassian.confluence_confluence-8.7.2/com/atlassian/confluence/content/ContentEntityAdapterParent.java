/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.content;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.content.ContentEntityAdapter;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.VersionChildOwnerPolicy;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.fugue.Option;

public class ContentEntityAdapterParent
implements ContentEntityAdapter {
    @Override
    @Deprecated
    public Option<String> getUrlPath(CustomContentEntityObject pluginContentEntityObject) {
        return Option.none();
    }

    @Override
    @Deprecated
    public Option<String> getDisplayTitle(CustomContentEntityObject pluginContentEntityObject) {
        return Option.none();
    }

    @Override
    @Deprecated
    public Option<String> getNameForComparison(CustomContentEntityObject pluginContentEntityObject) {
        return Option.none();
    }

    @Override
    @Deprecated
    public Option<String> getAttachmentsUrlPath(CustomContentEntityObject pluginContentEntityObject) {
        return Option.none();
    }

    @Override
    @Deprecated
    public Option<String> getAttachmentUrlPath(CustomContentEntityObject pluginContentEntityObject, Attachment attachment) {
        return Option.none();
    }

    @Override
    public BodyType getDefaultBodyType(CustomContentEntityObject pluginContentEntityObject) {
        return BodyType.RAW;
    }

    @Override
    @Deprecated
    public Option<String> getExcerpt(CustomContentEntityObject pluginContentEntityObject) {
        return Option.none();
    }

    @Override
    public boolean isAllowedParent(CustomContentEntityObject child, CustomContentEntityObject parent) {
        return false;
    }

    @Override
    public boolean isAllowedContainer(ContentEntityObject child, ContentEntityObject container) {
        return false;
    }

    @Override
    public boolean isIndexable(CustomContentEntityObject pluginContentEntityObject, boolean isDefaultIndexable) {
        return isDefaultIndexable;
    }

    @Override
    public boolean shouldConvertToContent(CustomContentEntityObject pluginContentEntityObject) {
        return true;
    }

    @Override
    public VersionChildOwnerPolicy getVersionChildPolicy(ContentType contentType) {
        return VersionChildOwnerPolicy.currentVersion;
    }
}

