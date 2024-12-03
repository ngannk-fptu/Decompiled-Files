/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.content.render.xhtml.view.excerpt.Excerpter
 *  com.atlassian.confluence.notifications.batch.service.BatchSectionProvider
 *  com.atlassian.confluence.notifications.batch.service.BatchSectionProvider$BatchOutput
 *  com.atlassian.confluence.notifications.batch.service.BatchTarget
 *  com.atlassian.confluence.notifications.batch.service.BatchingRoleRecipient
 *  com.atlassian.confluence.notifications.batch.template.BatchSection
 *  com.atlassian.confluence.notifications.batch.template.BatchTemplateCommentPattern$Builder
 *  com.atlassian.confluence.notifications.batch.template.BatchTemplateElement
 *  com.atlassian.confluence.notifications.batch.template.BatchTemplateGroup$Builder
 *  com.atlassian.confluence.notifications.batch.template.BatchTemplateHtml
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.notification.UserNotificationPreferences
 *  com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.like.notifications.batch;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.content.render.xhtml.view.excerpt.Excerpter;
import com.atlassian.confluence.notifications.batch.service.BatchSectionProvider;
import com.atlassian.confluence.notifications.batch.service.BatchTarget;
import com.atlassian.confluence.notifications.batch.service.BatchingRoleRecipient;
import com.atlassian.confluence.notifications.batch.template.BatchSection;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateCommentPattern;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateElement;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateGroup;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateHtml;
import com.atlassian.confluence.plugins.like.notifications.LikePayload;
import com.atlassian.confluence.plugins.like.notifications.batch.LikeContext;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferences;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailLikeCreatedBatchingSectionProvider
implements BatchSectionProvider<LikeContext> {
    private final I18nResolver i18nResolver;
    private final UserNotificationPreferencesManager preferencesManager;
    private final ContentService contentService;
    private final Excerpter excerpter;
    private static final Logger log = LoggerFactory.getLogger(EmailLikeCreatedBatchingSectionProvider.class);
    private static final String LIKE_CREATED_SECTION_HEADER = "notifications.batch.like.created.section.header";
    private static final String LIKE_CREATED_SECTION_NAME = "notifications.batch.like.created.section.name";
    private static final String LIKE_CREATED_USERS_MSG = "notifications.batch.like.created.users.msg";

    public EmailLikeCreatedBatchingSectionProvider(I18nResolver i18nResolver, UserNotificationPreferencesManager preferencesManager, ContentService contentService, Excerpter excerpter) {
        this.i18nResolver = i18nResolver;
        this.preferencesManager = preferencesManager;
        this.contentService = contentService;
        this.excerpter = excerpter;
    }

    public BatchSectionProvider.BatchOutput handle(BatchingRoleRecipient batchingRoleRecipient, List<LikeContext> contexts, ServerConfiguration serverConfiguration) {
        if (contexts == null || contexts.isEmpty() || contexts.size() > 1 || contexts.get(0) == null) {
            return new BatchSectionProvider.BatchOutput();
        }
        UserNotificationPreferences preferences = this.preferencesManager.getPreferences(batchingRoleRecipient.getUserKey());
        LikeContext context = contexts.get(0);
        Set<UserKey> contributors = context.getUserKeys().stream().collect(Collectors.toSet());
        ContentType contentType = context.getContentType();
        if (preferences.isOwnEventNotificationsEnabled(serverConfiguration)) {
            return this.processBatch(contentType, context.getContentId(), contributors);
        }
        contributors.remove(batchingRoleRecipient.getUserKey());
        return this.processBatch(contentType, context.getContentId(), contributors);
    }

    private BatchSectionProvider.BatchOutput processBatch(ContentType contentType, long contentId, Set<UserKey> contributors) {
        Object likeCreatedMsgKey;
        Content content = (Content)this.contentService.find(new Expansion[]{ExpansionsParser.parseSingle((String)("body." + ContentRepresentation.EXPORT_VIEW.getRepresentation()))}).withId(ContentId.of((long)contentId)).fetchOrNull();
        if (content == null || contributors.isEmpty()) {
            return new BatchSectionProvider.BatchOutput();
        }
        switch (contentType.getType()) {
            case "page": 
            case "blogpost": 
            case "comment": {
                likeCreatedMsgKey = "notifications.batch.like.created.users.msg." + contentType.getType();
                break;
            }
            default: {
                likeCreatedMsgKey = LIKE_CREATED_USERS_MSG;
            }
        }
        BatchTemplateGroup.Builder group = new BatchTemplateGroup.Builder();
        if ("comment".equals(contentType.getType())) {
            String excerpt = null;
            try {
                excerpt = this.excerpter.createExcerpt(content);
            }
            catch (Exception exception) {
                log.warn("Could not create excerpt for content", (Throwable)exception);
            }
            if (excerpt != null) {
                group.line().element((BatchTemplateElement)new BatchTemplateHtml(excerpt)).end();
            }
        }
        group.line().element((BatchTemplateElement)new BatchTemplateCommentPattern.Builder().authors(contributors).message((String)likeCreatedMsgKey).build()).end();
        int count = contributors.size();
        return new BatchSectionProvider.BatchOutput(new BatchSection(count, this.i18nResolver.getText(LIKE_CREATED_SECTION_HEADER, new Serializable[]{Integer.valueOf(count)}), this.i18nResolver.getText(LIKE_CREATED_SECTION_NAME, new Serializable[]{Integer.valueOf(count)}), Collections.singletonList(group.build())), new BatchTarget(Long.toString(contentId), 0));
    }

    public Class getPayloadType() {
        return LikePayload.class;
    }
}

