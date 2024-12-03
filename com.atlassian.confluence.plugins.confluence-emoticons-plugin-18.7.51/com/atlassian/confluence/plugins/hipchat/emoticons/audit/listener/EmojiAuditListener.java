/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.confluence.audit.AuditingContext
 *  com.atlassian.confluence.audit.StandardAuditResourceTypes
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  com.atlassian.user.UserManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.audit.listener;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.plugins.hipchat.emoticons.audit.event.CustomEmojiDeletedByAdminAuditEvent;
import com.atlassian.confluence.plugins.hipchat.emoticons.audit.event.CustomEmojiDeletedByUserAuditEvent;
import com.atlassian.confluence.plugins.hipchat.emoticons.audit.event.CustomEmojiUploadDisabledAuditEvent;
import com.atlassian.confluence.plugins.hipchat.emoticons.audit.event.CustomEmojiUploadEnabledAuditEvent;
import com.atlassian.confluence.plugins.hipchat.emoticons.audit.event.CustomEmojiUploadedAuditEvent;
import com.atlassian.confluence.plugins.hipchat.emoticons.audit.listener.AbstractAuditListener;
import com.atlassian.confluence.plugins.hipchat.emoticons.audit.utils.AuditCategories;
import com.atlassian.confluence.plugins.hipchat.emoticons.audit.utils.MessageKeyBuilder;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmojiAuditListener
extends AbstractAuditListener {
    static final String ENABLE_CUSTOM_EMOJI_UPLOAD_EVENT_SUMMARY = MessageKeyBuilder.buildSummaryTextKey("custom.emoji.upload.enabled");
    static final String DISABLE_CUSTOM_EMOJI_UPLOAD_EVENT_SUMMARY = MessageKeyBuilder.buildSummaryTextKey("custom.emoji.upload.disabled");
    static final String CUSTOM_EMOJI_UPLOADED_EVENT_SUMMARY = MessageKeyBuilder.buildSummaryTextKey("custom.emoji.uploaded");
    static final String CUSTOM_EMOJI_DELETED_BY_USER_EVENT_SUMMARY = MessageKeyBuilder.buildSummaryTextKey("custom.emoji.deleted.by.user");
    static final String CUSTOM_EMOJI_DELETED_BY_ADMIN_EVENT_SUMMARY = MessageKeyBuilder.buildSummaryTextKey("custom.emoji.deleted.by.admin");
    private static final Logger log = LoggerFactory.getLogger(EmojiAuditListener.class);

    @Autowired
    public EmojiAuditListener(@ComponentImport(value="auditService") AuditService auditService, @ComponentImport EventPublisher eventPublisher, @ComponentImport I18nResolver i18nResolver, @ComponentImport LocaleResolver localeResolver, @ComponentImport StandardAuditResourceTypes resourceTypes, @ComponentImport AuditingContext auditingContext, @ComponentImport UserManager userManager) {
        super(auditService, eventPublisher, i18nResolver, localeResolver, resourceTypes, auditingContext, userManager);
    }

    @EventListener
    public void onCustomEmojiEnabledEvent(CustomEmojiUploadEnabledAuditEvent event) {
        log.debug("Will save audit log event:" + CustomEmojiUploadEnabledAuditEvent.class);
        this.save(() -> AuditEvent.fromI18nKeys((String)AuditCategories.PERMISSIONS_CATEGORY, (String)ENABLE_CUSTOM_EMOJI_UPLOAD_EVENT_SUMMARY, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.ECOSYSTEM).build());
    }

    @EventListener
    public void onCustomEmojiDisabledEvent(CustomEmojiUploadDisabledAuditEvent event) {
        log.debug("Will save audit log event:" + CustomEmojiUploadDisabledAuditEvent.class);
        this.save(() -> AuditEvent.fromI18nKeys((String)AuditCategories.PERMISSIONS_CATEGORY, (String)DISABLE_CUSTOM_EMOJI_UPLOAD_EVENT_SUMMARY, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.ECOSYSTEM).build());
    }

    @EventListener
    public void onCustomEmojiUploadedEvent(CustomEmojiUploadedAuditEvent event) {
        log.debug("Will save audit log event:" + CustomEmojiUploadedAuditEvent.class);
        this.save(() -> AuditEvent.fromI18nKeys((String)AuditCategories.PAGES_CATEGORY, (String)CUSTOM_EMOJI_UPLOADED_EVENT_SUMMARY, (CoverageLevel)CoverageLevel.ADVANCED, (CoverageArea)CoverageArea.ECOSYSTEM).affectedObjects(this.buildAffectedObjects(event.getEmojiShorcut(), event.getEmojiName())).build());
    }

    @EventListener
    public void onUserDeletedOwnCustomEmojiEvent(CustomEmojiDeletedByUserAuditEvent event) {
        log.debug("Will save audit log event:" + CustomEmojiDeletedByUserAuditEvent.class);
        this.save(() -> AuditEvent.fromI18nKeys((String)AuditCategories.PAGES_CATEGORY, (String)CUSTOM_EMOJI_DELETED_BY_USER_EVENT_SUMMARY, (CoverageLevel)CoverageLevel.ADVANCED, (CoverageArea)CoverageArea.ECOSYSTEM).affectedObjects(this.buildAffectedObjects(event.getEmojiShorcut(), event.getEmojiName(), event.getCreatorUsername())).build());
    }

    @EventListener
    public void onAdminDeletedCustomEmojiEvent(CustomEmojiDeletedByAdminAuditEvent event) {
        log.debug("Will save audit log event:" + CustomEmojiDeletedByAdminAuditEvent.class);
        this.save(() -> AuditEvent.fromI18nKeys((String)AuditCategories.PAGES_CATEGORY, (String)CUSTOM_EMOJI_DELETED_BY_ADMIN_EVENT_SUMMARY, (CoverageLevel)CoverageLevel.ADVANCED, (CoverageArea)CoverageArea.ECOSYSTEM).affectedObjects(this.buildAffectedObjects(event.getEmojiShorcut(), event.getEmojiName(), event.getCreatorUsername())).build());
    }

    private List<AuditResource> buildAffectedObjects(String emojiShortcut, String emojiName) {
        ArrayList<AuditResource> auditResources = new ArrayList<AuditResource>();
        auditResources.add(this.buildResourceWithoutId(":" + emojiShortcut + ":", this.resourceTypes.attachment()));
        auditResources.add(this.buildResourceWithoutId(emojiName, this.resourceTypes.attachment()));
        return auditResources;
    }

    private List<AuditResource> buildAffectedObjects(String emojiShortcut, String emojiName, String creatorUsername) {
        List<AuditResource> auditResources = this.buildAffectedObjects(emojiShortcut, emojiName);
        User user = null;
        try {
            user = this.userManager.getUser(creatorUsername);
        }
        catch (EntityException e) {
            log.info("EmojiAuditListener could not find user with username=" + creatorUsername);
        }
        if (!(user instanceof ConfluenceUser)) {
            auditResources.add(this.buildResourceWithoutId(creatorUsername, this.resourceTypes.user()));
        } else {
            auditResources.add(this.buildResource(user.getFullName(), this.resourceTypes.user(), ((ConfluenceUser)user).getKey().getStringValue()));
        }
        return auditResources;
    }
}

