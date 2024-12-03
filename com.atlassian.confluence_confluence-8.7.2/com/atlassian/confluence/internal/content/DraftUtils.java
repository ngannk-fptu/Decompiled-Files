/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.api.model.content.ContentStatus
 */
package com.atlassian.confluence.internal.content;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.search.service.ContentTypeEnum;

@Internal
public class DraftUtils {
    private DraftUtils() {
    }

    public static boolean isDraft(ContentEntityObject content) {
        return (content instanceof Draft || content instanceof AbstractPage) && content.isDraft();
    }

    public static boolean isPersonalDraft(ContentEntityObject content) {
        return content instanceof Draft;
    }

    public static boolean isSharedDraft(ContentEntityObject content) {
        return content instanceof AbstractPage && content.getContentStatusObject() == ContentStatus.DRAFT;
    }

    public static boolean isPageOrBlogPost(ContentEntityObject content) {
        return content instanceof AbstractPage && !content.isDraft() && (content.getTypeEnum() == ContentTypeEnum.PAGE || DraftUtils.isBlogPost(content));
    }

    public static boolean isBlogPost(ContentEntityObject content) {
        return content instanceof AbstractPage && !content.isDraft() && content.getTypeEnum() == ContentTypeEnum.BLOG;
    }
}

