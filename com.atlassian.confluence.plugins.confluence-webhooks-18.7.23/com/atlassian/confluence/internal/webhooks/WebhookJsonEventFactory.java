/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.confluence.event.events.content.ContentEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostEvent
 *  com.atlassian.confluence.event.events.content.comment.CommentEvent
 *  com.atlassian.confluence.event.events.content.page.PageChildrenReorderEvent
 *  com.atlassian.confluence.event.events.content.page.PageEvent
 *  com.atlassian.confluence.event.events.content.page.PageMoveEvent
 *  com.atlassian.confluence.event.events.content.page.PageUpdateEvent
 *  com.atlassian.confluence.event.events.group.GroupEvent
 *  com.atlassian.confluence.event.events.label.LabelEvent
 *  com.atlassian.confluence.event.events.security.ContentPermissionEvent
 *  com.atlassian.confluence.event.events.space.SpaceEvent
 *  com.atlassian.confluence.event.events.user.UserEvent
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.Labelable
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.ContentConvertible
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.plugins.createcontent.api.events.BlueprintPageCreateEvent
 *  com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint
 *  com.atlassian.confluence.security.ContentPermission
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.Spaced
 *  com.atlassian.confluence.themes.events.ThemeChangedEvent
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.user.User
 *  com.atlassian.webhooks.WebhookEvent
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.internal.webhooks;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostEvent;
import com.atlassian.confluence.event.events.content.comment.CommentEvent;
import com.atlassian.confluence.event.events.content.page.PageChildrenReorderEvent;
import com.atlassian.confluence.event.events.content.page.PageEvent;
import com.atlassian.confluence.event.events.content.page.PageMoveEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.event.events.group.GroupEvent;
import com.atlassian.confluence.event.events.label.LabelEvent;
import com.atlassian.confluence.event.events.security.ContentPermissionEvent;
import com.atlassian.confluence.event.events.space.SpaceEvent;
import com.atlassian.confluence.event.events.user.UserEvent;
import com.atlassian.confluence.internal.webhooks.ApplicationWebhookEvent;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.ContentConvertible;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.createcontent.api.events.BlueprintPageCreateEvent;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.themes.events.ThemeChangedEvent;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.user.User;
import com.atlassian.webhooks.WebhookEvent;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="webhookJsonEventFactory")
public class WebhookJsonEventFactory {
    private static final boolean WEBHOOKS_PAYLOAD_ID_ONLY_DISABLED = Boolean.getBoolean("confluence.webhooks.id.only.disabled");
    private final ApplicationProperties applicationProperties;
    private final UserManager userManager;

    @Autowired
    public WebhookJsonEventFactory(@ComponentImport ApplicationProperties applicationProperties, @ComponentImport UserManager userManager) {
        this.applicationProperties = applicationProperties;
        this.userManager = userManager;
    }

    public Map<String, Object> build(WebhookEvent webhookEvent, AttachmentEvent event) {
        return WebhookJsonEventFactory.builder(webhookEvent, event.getTimestamp()).put((Object)"attachedTo", this.contentEntityObjectToMap(event.getAttachedTo())).put((Object)"attachments", event.getAttachments().stream().map(this::attachmentToMap).collect(ImmutableList.toImmutableList())).build();
    }

    public Map<String, Object> build(WebhookEvent webhookEvent, BlogPostEvent event) {
        return WebhookJsonEventFactory.builder(webhookEvent, event.getTimestamp()).put((Object)"blog", this.contentEntityObjectToMap((ContentEntityObject)event.getBlogPost())).build();
    }

    public Map<String, Object> build(BlueprintPageCreateEvent event) {
        return WebhookJsonEventFactory.builder(ApplicationWebhookEvent.BLUEPRINT_PAGE_CREATED, event.getTimestamp()).put((Object)"blueprintContext", (Object)event.getContext()).put((Object)"blueprint", this.contentBlueprintToMap(event.getBlueprint())).put((Object)"creator", (Object)WebhookJsonEventFactory.getUserKey(event.getCreator())).put((Object)"page", this.contentEntityObjectToMap((ContentEntityObject)event.getPage())).build();
    }

    public Map<String, Object> build(WebhookEvent webhookEvent, CommentEvent event) {
        return WebhookJsonEventFactory.builder(webhookEvent, event.getTimestamp()).put((Object)"comment", this.commentToMap(event.getComment())).build();
    }

    public Map<String, Object> build(WebhookEvent webhookEvent, ContentPermissionEvent event) {
        ImmutableMap.Builder builder = WebhookJsonEventFactory.builder(webhookEvent, event.getTimestamp()).put((Object)"content", this.contentEntityObjectToMap(event.getContent()));
        ContentPermission contentPermission = event.getContentPermission();
        if (contentPermission != null) {
            builder.put((Object)"type", (Object)contentPermission.getType());
        }
        return builder.build();
    }

    public Map<String, Object> build(WebhookEvent webhookEvent, ContentEvent event) {
        return WebhookJsonEventFactory.builder(webhookEvent, event.getTimestamp()).put((Object)"content", this.contentEntityObjectToMap(event.getContent())).build();
    }

    public Map<String, Object> build(WebhookEvent webhookEvent, ConfluenceEvent event) {
        return WebhookJsonEventFactory.builder(webhookEvent, event.getTimestamp()).build();
    }

    public Map<String, Object> build(WebhookEvent webhookEvent, GroupEvent event) {
        return WebhookJsonEventFactory.builder(webhookEvent, event.getTimestamp()).put((Object)"group", (Object)event.getGroup().getName()).build();
    }

    public Map<String, Object> build(WebhookEvent webhookEvent, LabelEvent event) {
        ImmutableMap.Builder<String, Object> builder = WebhookJsonEventFactory.builder(webhookEvent, event.getTimestamp());
        builder.put((Object)"label", this.labelToMap(event.getLabel(), true));
        Labelable labelable = event.getLabelled();
        if (labelable != null) {
            builder.put((Object)"labeled", this.labelableToMap(labelable));
        }
        return builder.build();
    }

    public Map<String, Object> build(PageChildrenReorderEvent event) {
        return WebhookJsonEventFactory.builder(ApplicationWebhookEvent.PAGE_CHILDREN_REORDERED, event.getTimestamp()).put((Object)"page", this.contentEntityObjectToMap((ContentEntityObject)event.getPage())).put((Object)"oldSortedChildren", event.getOldSortedChildPages().stream().map(this::contentEntityObjectToMap).collect(ImmutableList.toImmutableList())).put((Object)"newSortedChildren", event.getNewSortedChildPages().stream().map(this::contentEntityObjectToMap).collect(ImmutableList.toImmutableList())).build();
    }

    public Map<String, Object> build(PageMoveEvent event) {
        Page oldParentPage = event.getOldParentPage();
        Page newParentPage = event.getNewParentPage();
        ImmutableMap.Builder builder = WebhookJsonEventFactory.builder(ApplicationWebhookEvent.PAGE_MOVED, event.getTimestamp()).put((Object)"page", this.contentEntityObjectToMap((ContentEntityObject)event.getPage()));
        if (oldParentPage != null) {
            builder.put((Object)"oldParent", this.contentEntityObjectToMap((ContentEntityObject)oldParentPage));
        }
        if (newParentPage != null) {
            builder.put((Object)"newParent", this.contentEntityObjectToMap((ContentEntityObject)newParentPage));
        }
        return builder.build();
    }

    public Map<String, Object> build(PageUpdateEvent event) {
        return WebhookJsonEventFactory.builder(ApplicationWebhookEvent.PAGE_UPDATED, event.getTimestamp()).put((Object)"page", this.contentEntityObjectToMap((ContentEntityObject)event.getPage())).put((Object)"updateTrigger", (Object)event.getUpdateTrigger().lowerCase()).build();
    }

    public Map<String, Object> build(WebhookEvent webhookEvent, PageEvent event) {
        return WebhookJsonEventFactory.builder(webhookEvent, event.getTimestamp()).put((Object)"page", this.contentEntityObjectToMap((ContentEntityObject)event.getPage())).build();
    }

    public Map<String, Object> build(WebhookEvent webhookEvent, SpaceEvent event) {
        return WebhookJsonEventFactory.builder(webhookEvent, event.getTimestamp()).put((Object)"space", this.spaceToMap(event.getSpace())).build();
    }

    public Map<String, Object> build(WebhookEvent webhookEvent, ThemeChangedEvent event) {
        return WebhookJsonEventFactory.builder(webhookEvent, event.getTimestamp()).put((Object)"global", (Object)StringUtils.isBlank((CharSequence)event.getSpaceKey())).put((Object)"spaceKey", (Object)StringUtils.trimToEmpty((String)event.getSpaceKey())).put((Object)"oldThemeKey", (Object)StringUtils.trimToEmpty((String)event.getOldThemeKey())).put((Object)"newThemeKey", (Object)StringUtils.trimToEmpty((String)event.getNewThemeKey())).build();
    }

    public Map<String, Object> build(WebhookEvent webhookEvent, UserEvent event) {
        UserProfile userProfile;
        ImmutableMap.Builder<String, Object> builder = WebhookJsonEventFactory.builder(webhookEvent, event.getTimestamp());
        User user = event.getUser();
        if (user != null && (userProfile = this.userManager.getUserProfile(user.getName())) != null) {
            builder.put((Object)"userProfile", this.userProfileToMap(userProfile));
        }
        return builder.build();
    }

    private static ImmutableMap.Builder<String, Object> builder(WebhookEvent event, long timestamp) {
        return ImmutableMap.builder().put((Object)"timestamp", (Object)timestamp).put((Object)"event", (Object)event.getId()).put((Object)"userKey", (Object)WebhookJsonEventFactory.getUserKey(AuthenticatedUserThreadLocal.get()));
    }

    private Map<String, Object> attachmentToMap(Attachment attachment) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put((Object)"id", (Object)attachment.getId());
        if (WEBHOOKS_PAYLOAD_ID_ONLY_DISABLED) {
            builder.put((Object)"fileName", (Object)attachment.getFileName());
            builder.put((Object)"version", (Object)attachment.getVersion());
            builder.put((Object)"comment", (Object)StringUtils.defaultIfBlank((CharSequence)attachment.getVersionComment(), (CharSequence)""));
            builder.put((Object)"fileSize", (Object)attachment.getFileSize());
            builder.put((Object)"creatorKey", (Object)WebhookJsonEventFactory.getUserKey(attachment.getCreator()));
            builder.put((Object)"creationDate", (Object)attachment.getCreationDate().getTime());
            builder.put((Object)"lastModifierKey", (Object)WebhookJsonEventFactory.getUserKey(attachment.getLastModifier()));
            builder.put((Object)"modificationDate", (Object)attachment.getLastModificationDate().getTime());
            builder.put((Object)"self", (Object)this.getFullUrl(attachment.getDownloadPath()));
        }
        return builder.build();
    }

    private Map<String, Object> commentToMap(Comment comment) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.putAll(this.contentEntityObjectToMap((ContentEntityObject)comment));
        if (WEBHOOKS_PAYLOAD_ID_ONLY_DISABLED && comment.getParent() != null) {
            builder.put((Object)"inReplyTo", this.commentToMap(comment.getParent()));
        }
        return builder.build();
    }

    private Map<String, Object> contentBlueprintToMap(ContentBlueprint blueprint) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put((Object)"id", (Object)blueprint.getId());
        builder.put((Object)"indexKey", (Object)blueprint.getIndexKey());
        builder.put((Object)"spaceKey", (Object)StringUtils.defaultIfBlank((CharSequence)blueprint.getSpaceKey(), (CharSequence)""));
        builder.put((Object)"i18nNameKey", (Object)blueprint.getI18nNameKey());
        builder.put((Object)"indexTitleI18nKey", (Object)StringUtils.defaultIfBlank((CharSequence)blueprint.getIndexTitleI18nKey(), (CharSequence)""));
        builder.put((Object)"moduleCompleteKey", (Object)blueprint.getModuleCompleteKey());
        builder.put((Object)"createResult", (Object)StringUtils.defaultIfBlank((CharSequence)blueprint.getCreateResult(), (CharSequence)""));
        builder.put((Object)"howToUseTemplate", (Object)StringUtils.defaultIfBlank((CharSequence)blueprint.getHowToUseTemplate(), (CharSequence)""));
        return builder.build();
    }

    private Map<String, Object> contentEntityObjectToMap(ContentEntityObject ceo) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put((Object)"id", (Object)ceo.getId());
        if (WEBHOOKS_PAYLOAD_ID_ONLY_DISABLED) {
            Space space;
            if (!StringUtils.isBlank((CharSequence)ceo.getTitle())) {
                builder.put((Object)"title", (Object)ceo.getTitle());
            }
            if (ceo instanceof ContentConvertible) {
                builder.put((Object)"contentType", (Object)((ContentConvertible)ceo).getContentTypeObject().getType());
            }
            builder.put((Object)"creatorKey", (Object)WebhookJsonEventFactory.getUserKey(ceo.getCreator()));
            builder.put((Object)"lastModifierKey", (Object)WebhookJsonEventFactory.getUserKey(ceo.getLastModifier()));
            builder.put((Object)"creationDate", ceo.getCreationDate() != null ? Long.valueOf(ceo.getCreationDate().getTime()) : "");
            builder.put((Object)"modificationDate", ceo.getLastModificationDate() != null ? Long.valueOf(ceo.getLastModificationDate().getTime()) : "");
            builder.put((Object)"version", (Object)ceo.getVersion());
            builder.put((Object)"self", (Object)this.getFullUrl(ceo.getUrlPath()));
            if (ceo instanceof Spaced && (space = ((Spaced)ceo).getSpace()) != null) {
                builder.put((Object)"spaceKey", (Object)space.getKey());
            }
        }
        return builder.build();
    }

    private String getFullUrl(String relativeUrl) {
        return this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + relativeUrl;
    }

    private static String getUserKey(@Nullable ConfluenceUser user) {
        return user == null ? "" : user.getKey().getStringValue();
    }

    private Map<String, Object> labelableToMap(Labelable labelable) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put((Object)"labels", labelable.getLabels().stream().map(label -> this.labelToMap((Label)label, true)).collect(ImmutableList.toImmutableList()));
        if (labelable instanceof Attachment) {
            builder.putAll(this.attachmentToMap((Attachment)labelable));
        } else if (labelable instanceof ContentEntityObject) {
            builder.putAll(this.contentEntityObjectToMap((ContentEntityObject)labelable));
        }
        return builder.build();
    }

    private Map<String, Object> labelToMap(Label label, boolean nameOnly) {
        if (nameOnly) {
            return ImmutableMap.of((Object)"name", (Object)label.getName());
        }
        ConfluenceUser ownerUser = label.getOwnerUser();
        return ImmutableMap.of((Object)"name", (Object)label.getName(), (Object)"ownerKey", (Object)WebhookJsonEventFactory.getUserKey(ownerUser), (Object)"title", (Object)label.getDisplayTitle(), (Object)"self", (Object)this.getFullUrl(label.getUrlPath()));
    }

    private Map<String, Object> spaceToMap(Space space) {
        Page homePage;
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put((Object)"id", (Object)space.getId());
        if (WEBHOOKS_PAYLOAD_ID_ONLY_DISABLED) {
            builder.put((Object)"key", (Object)space.getKey());
            builder.put((Object)"title", (Object)space.getDisplayTitle());
            if (space.getDescription() != null) {
                builder.put((Object)"description", (Object)space.getDescription().getBodyAsString());
            }
            builder.put((Object)"isPersonalSpace", (Object)space.isPersonal());
            builder.put((Object)"self", (Object)this.getFullUrl(space.getUrlPath()));
        }
        if ((homePage = space.getHomePage()) != null) {
            builder.put((Object)"homePage", this.contentEntityObjectToMap((ContentEntityObject)homePage));
        }
        builder.put((Object)"creatorKey", (Object)WebhookJsonEventFactory.getUserKey(space.getCreator()));
        builder.put((Object)"creationDate", (Object)space.getCreationDate().getTime());
        builder.put((Object)"lastModifierKey", (Object)WebhookJsonEventFactory.getUserKey(space.getLastModifier()));
        builder.put((Object)"modificationDate", (Object)space.getLastModificationDate().getTime());
        return builder.build();
    }

    private Map<String, Object> userProfileToMap(UserProfile userProfile) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put((Object)"userKey", (Object)userProfile.getUserKey().getStringValue());
        if (WEBHOOKS_PAYLOAD_ID_ONLY_DISABLED) {
            builder.put((Object)"email", (Object)userProfile.getEmail());
            builder.put((Object)"fullName", (Object)userProfile.getFullName());
        }
        return builder.build();
    }
}

