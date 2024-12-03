/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookEvent
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.internal.webhooks;

import com.atlassian.webhooks.WebhookEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

public enum ApplicationWebhookEvent implements WebhookEvent
{
    ATTACHMENT_CREATED("attachment_created"),
    ATTACHMENT_REMOVED("attachment_removed"),
    ATTACHMENT_RESTORED("attachment_restored"),
    ATTACHMENT_TRASHED("attachment_trashed"),
    ATTACHMENT_UPDATED("attachment_updated"),
    BLOG_CREATED("blog_created"),
    BLOG_REMOVED("blog_removed"),
    BLOG_RESTORED("blog_restored"),
    BLOG_TRASHED("blog_trashed"),
    BLOG_UPDATED("blog_updated"),
    BLUEPRINT_PAGE_CREATED("blueprint_page_created"),
    COMMENT_CREATED("comment_created"),
    COMMENT_REMOVED("comment_removed"),
    COMMENT_UPDATED("comment_updated"),
    CONTENT_CREATED("content_created"),
    CONTENT_RESTORED("content_restored"),
    CONTENT_TRASHED("content_trashed"),
    CONTENT_UPDATED("content_updated"),
    CONTENT_PERMISSIONS_UPDATED("content_permissions_updated"),
    GROUP_CREATED("group_created"),
    GROUP_REMOVED("group_removed"),
    LABEL_ADDED("label_added"),
    LABEL_CREATED("label_created"),
    LABEL_DELETED("label_deleted"),
    LABEL_REMOVED("label_removed"),
    PAGE_CHILDREN_REORDERED("page_children_reordered"),
    PAGE_CREATED("page_created"),
    PAGE_MOVED("page_moved"),
    PAGE_REMOVED("page_removed"),
    PAGE_RESTORED("page_restored"),
    PAGE_TRASHED("page_trashed"),
    PAGE_UPDATED("page_updated"),
    SPACE_CREATED("space_created"),
    SPACE_LOGO_UPDATED("space_logo_updated"),
    SPACE_PERMISSIONS_UPDATED("space_permissions_updated"),
    SPACE_REMOVED("space_removed"),
    SPACE_UPDATED("space_updated"),
    THEME_ENABLED("theme_enabled"),
    USER_CREATED("user_created"),
    USER_DEACTIVATED("user_deactivated"),
    USER_FOLLOWED("user_followed"),
    USER_REACTIVATED("user_reactivated"),
    USER_REMOVED("user_removed");

    private static final String I18N_PREFIX = "confluence.webhooks.event.";
    private final String id;
    private final String i18nKey;

    private ApplicationWebhookEvent(String id) {
        this.id = id;
        this.i18nKey = I18N_PREFIX + this.name().toLowerCase();
    }

    public String getId() {
        return this.id;
    }

    public String getI18nKey() {
        return this.i18nKey;
    }

    public static @Nullable ApplicationWebhookEvent forId(@Nullable String id) {
        for (ApplicationWebhookEvent event : ApplicationWebhookEvent.values()) {
            if (!event.getId().equalsIgnoreCase(id)) continue;
            return event;
        }
        return null;
    }
}

