/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelParser
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.mobile.helper;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentHelper {
    private ContentHelper() {
    }

    public static boolean isSaved(@Nullable Collection<String> labels) {
        if (AuthenticatedUserThreadLocal.isAnonymousUser()) {
            return false;
        }
        String label = AuthenticatedUserThreadLocal.getUsername() + ":favourite";
        return labels != null && labels.stream().anyMatch(value -> value.contains(label));
    }

    public static boolean isSaved(@Nullable List<Label> labels) {
        String label = LabelParser.PERSONAL_LABEL_PREFIX + "favourite";
        return labels != null && labels.stream().anyMatch(value -> value.getDisplayTitle().contains(label));
    }

    @Deprecated
    public static boolean isAbstractPage(@Nonnull ContentEntityObject ceo) {
        return ContentType.PAGE.getType().equals(ceo.getType()) || ContentType.BLOG_POST.getType().equals(ceo.getType());
    }

    public static boolean isPageOrBlog(@Nonnull ContentEntityObject content) {
        return EnumSet.of(ContentTypeEnum.PAGE, ContentTypeEnum.BLOG).contains(content.getTypeEnum());
    }

    public static boolean isInlineComment(@Nonnull Comment comment) {
        return comment.isInlineComment() || comment.getContainer() instanceof Attachment;
    }

    public static String getCommentLocation(@Nonnull Comment comment) {
        return ContentHelper.isInlineComment(comment) ? "inline" : "footer";
    }
}

