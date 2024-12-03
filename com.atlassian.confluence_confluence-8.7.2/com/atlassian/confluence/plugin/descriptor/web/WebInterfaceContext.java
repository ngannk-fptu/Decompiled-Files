/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.plugin.descriptor.web;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.DisplayableLabel;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.plugin.descriptor.web.ContextMap;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import java.util.Map;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface WebInterfaceContext
extends ContextMap<String, Object> {
    public static final String CONTEXT_KEY_USER = "user";
    public static final String CONTEXT_KEY_TARGET_USER = "targetUser";
    public static final String CONTEXT_KEY_PAGE = "page";
    public static final String CONTEXT_KEY_SPACE = "space";
    public static final String CONTEXT_KEY_SPACE_ID = "spaceid";
    public static final String CONTEXT_KEY_SPACE_KEY = "spacekey";
    public static final String CONTEXT_KEY_COMMENT = "comment";
    public static final String CONTEXT_KEY_LABEL = "label";
    public static final String CONTEXT_KEY_ATTACHMENT = "attachment";
    public static final String CONTEXT_KEY_PERSONAL_INFORMATION = "userinfo";
    public static final String CONTEXT_KEY_PARENT_PAGE = "parentPage";
    @Deprecated
    public static final String CONTEXT_KEY_DRAFT = "draft";
    @Deprecated
    public static final String CONTEXT_KEY_CONTENT_DRAFT = "contentDraft";
    @Deprecated
    public static final String CONTEXT_KEY_EDIT_PAGE_RESTRICTED = "editPageRestricted";

    public @Nullable ConfluenceUser getCurrentUser();

    public @Nullable ConfluenceUser getTargetedUser();

    public @Nullable AbstractPage getPage();

    @Deprecated
    public @Nullable Draft getDraft();

    @Deprecated
    public @Nullable ContentEntityObject getContentDraft();

    public @Nullable Space getSpace();

    public Optional<Long> getSpaceId();

    public @Nullable String getSpaceKey();

    public @Nullable Comment getComment();

    public @Nullable DisplayableLabel getLabel();

    public @Nullable Attachment getAttachment();

    public @Nullable PersonalInformation getPersonalInformation();

    public @Nullable Object getParameter(String var1);

    public boolean hasParameter(String var1);

    @Override
    public Map<String, Object> toMap();

    @Deprecated
    public boolean isEditPageRestricted();

    public @Nullable AbstractPage getParentPage();
}

