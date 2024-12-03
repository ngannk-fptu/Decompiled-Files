/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.content;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.VersionChildOwnerPolicy;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.util.FugueConversionUtil;
import com.atlassian.fugue.Option;
import java.util.Optional;

public interface ContentEntityAdapter {
    @Deprecated
    public Option<String> getUrlPath(CustomContentEntityObject var1);

    default public Optional<String> urlPath(CustomContentEntityObject pluginContentEntityObject) {
        return FugueConversionUtil.toOptional(this.getUrlPath(pluginContentEntityObject));
    }

    @Deprecated
    public Option<String> getDisplayTitle(CustomContentEntityObject var1);

    default public Optional<String> displayTitle(CustomContentEntityObject pluginContentEntityObject) {
        return FugueConversionUtil.toOptional(this.getDisplayTitle(pluginContentEntityObject));
    }

    @Deprecated
    public Option<String> getNameForComparison(CustomContentEntityObject var1);

    default public Optional<String> nameForComparison(CustomContentEntityObject pluginContentEntityObject) {
        return FugueConversionUtil.toOptional(this.getNameForComparison(pluginContentEntityObject));
    }

    @Deprecated
    public Option<String> getAttachmentsUrlPath(CustomContentEntityObject var1);

    default public Optional<String> attachmentsUrlPath(CustomContentEntityObject pluginContentEntityObject) {
        return FugueConversionUtil.toOptional(this.getAttachmentsUrlPath(pluginContentEntityObject));
    }

    @Deprecated
    public Option<String> getAttachmentUrlPath(CustomContentEntityObject var1, Attachment var2);

    default public Optional<String> attachmentUrlPath(CustomContentEntityObject pluginContentEntityObject, Attachment attachment) {
        return FugueConversionUtil.toOptional(this.getAttachmentUrlPath(pluginContentEntityObject, attachment));
    }

    public BodyType getDefaultBodyType(CustomContentEntityObject var1);

    @Deprecated
    public Option<String> getExcerpt(CustomContentEntityObject var1);

    default public Optional<String> excerpt(CustomContentEntityObject pluginContentEntityObject) {
        return FugueConversionUtil.toOptional(this.getExcerpt(pluginContentEntityObject));
    }

    public boolean isAllowedParent(CustomContentEntityObject var1, CustomContentEntityObject var2);

    public boolean isAllowedContainer(ContentEntityObject var1, ContentEntityObject var2);

    public boolean isIndexable(CustomContentEntityObject var1, boolean var2);

    public boolean shouldConvertToContent(CustomContentEntityObject var1);

    public VersionChildOwnerPolicy getVersionChildPolicy(ContentType var1);
}

