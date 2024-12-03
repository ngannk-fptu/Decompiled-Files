/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.pages.actions;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.pages.actions.CommentAware;
import com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class CommentAwareHelper {
    private static final Logger log = LoggerFactory.getLogger(CommentAwareHelper.class);
    private final CommentManager commentManager;
    private final PermissionManager permissionManager;
    private final ConfluenceWebResourceManager webResourceManager;

    public CommentAwareHelper(CommentManager commentManager, PermissionManager permissionManager, ConfluenceWebResourceManager webResourceManager) {
        this.commentManager = commentManager;
        this.permissionManager = permissionManager;
        this.webResourceManager = webResourceManager;
    }

    public void configureCommentAware(CommentAware commentAware, ParameterSource parameterSource, ConfluenceUser user) {
        this.getComment(parameterSource, "commentId", "focusedCommentId").ifPresent(comment -> {
            if (this.permissionManager.hasPermission((User)user, Permission.VIEW, comment)) {
                commentAware.setComment((Comment)comment);
                this.webResourceManager.putMetadata("comment-id", comment.getIdAsString());
            }
        });
    }

    private @NonNull Optional<Comment> getComment(ParameterSource parameterSource, String ... possibleCommentIdParameterNames) {
        for (String parameterName : possibleCommentIdParameterNames) {
            if (!parameterSource.hasParameter(parameterName)) continue;
            return CommentAwareHelper.parseId(parameterSource.getParameter(parameterName)).map(this.commentManager::getComment);
        }
        return Optional.empty();
    }

    private static Optional<Long> parseId(@Nullable String str) {
        if (StringUtils.isNumeric((CharSequence)str)) {
            return Optional.of(Long.parseLong(str));
        }
        log.debug("Non-numeric ID in request: '{}'", (Object)str);
        return Optional.empty();
    }

    public static interface ParameterSource {
        public @Nullable String getParameter(String var1);

        default public boolean hasParameter(String name) {
            return StringUtils.isNotEmpty((CharSequence)this.getParameter(name));
        }
    }
}

