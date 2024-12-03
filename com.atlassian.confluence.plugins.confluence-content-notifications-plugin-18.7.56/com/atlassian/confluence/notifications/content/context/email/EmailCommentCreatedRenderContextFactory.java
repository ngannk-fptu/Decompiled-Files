/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentBody
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.notifications.CachedContentFinder
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.NotificationUserService
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.notifications.content.context.email;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.notifications.CachedContentFinder;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationUserService;
import com.atlassian.confluence.notifications.content.CommentPayload;
import com.atlassian.confluence.notifications.content.CommonContentExpansions;
import com.atlassian.confluence.notifications.content.context.AbstractCommentCreatedRenderContextFactory;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;

@Deprecated
public class EmailCommentCreatedRenderContextFactory
extends AbstractCommentCreatedRenderContextFactory {
    private static final BandanaContext BANDANA_EMAIL_GATEWAY_CONFIGURATION_CONTEXT = new ConfluenceBandanaContext("email-gateway-configuration");
    private static final String ALLOW_TO_CREATE_COMMENT_BY_EMAIL_KEY = "com.atlassian.confluence.plugins.emailgateway.allow.create.comment";
    private static final Expansion ANCESTOR_HISTORY = ExpansionsParser.parseSingle((String)"ancestors.history");
    private final CachedContentFinder cachedContentFinder;
    private final NotificationUserService notificationUserService;
    private final ContentEntityManager contentEntityManager;
    private final BandanaManager bandanaManager;

    public EmailCommentCreatedRenderContextFactory(CachedContentFinder cachedContentFinder, NotificationUserService notificationUserService, LocaleManager localeManager, @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, BandanaManager bandanaManager) {
        super(cachedContentFinder, notificationUserService, localeManager);
        this.cachedContentFinder = cachedContentFinder;
        this.notificationUserService = notificationUserService;
        this.contentEntityManager = contentEntityManager;
        this.bandanaManager = bandanaManager;
    }

    @Override
    public Expansion[] getMediumSpecificExpansions() {
        Expansions ancestorCachedParent = new Expansions(new Expansion[]{this.cachedContentFinder.exportBody()}).prepend("ancestors");
        return new Expansions(new Expansion[]{CommonContentExpansions.SPACE, this.cachedContentFinder.exportBody(), CommonContentExpansions.CONTAINER, CommonContentExpansions.ANCESTORS, ANCESTOR_HISTORY}).merge(ancestorCachedParent).toArray();
    }

    @Override
    public Maybe<Map<String, Object>> getMediumSpecificContext(Notification<CommentPayload> notification, ServerConfiguration serverConfiguration, User recipient, Content content) {
        ImmutableMap.Builder contextBuilder = ImmutableMap.builder();
        contextBuilder.put((Object)"contentHtml", (Object)((ContentBody)content.getBody().get(this.cachedContentFinder.exportRepresentation())).getValue());
        if (!content.getAncestors().isEmpty()) {
            Content parent = (Content)content.getAncestors().get(0);
            Person createdBy = parent.getHistory().getCreatedBy();
            User createdByUser = this.notificationUserService.findUserForPerson(recipient, createdBy);
            String parentBodyContent = ((ContentBody)parent.getBody().get(this.cachedContentFinder.exportRepresentation())).getValue();
            contextBuilder.put((Object)"parentCommentHtml", (Object)parentBodyContent);
            contextBuilder.put((Object)"parentUser", (Object)createdByUser);
            contextBuilder.put((Object)"parentInlineContext", ((CommentPayload)notification.getPayload()).getParentInlineContext().getOrNull());
        }
        contextBuilder.putAll(this.buildInlineCommentContext(((CommentPayload)notification.getPayload()).getContentId()));
        boolean replyByEmailEnabled = (Boolean)Optional.ofNullable(this.bandanaManager.getValue(BANDANA_EMAIL_GATEWAY_CONFIGURATION_CONTEXT, ALLOW_TO_CREATE_COMMENT_BY_EMAIL_KEY)).orElse(false);
        contextBuilder.put((Object)"replyByEmailEnabled", (Object)replyByEmailEnabled);
        return Option.some((Object)contextBuilder.build());
    }

    private Map<String, Object> buildInlineCommentContext(long contentId) {
        String originalSelection;
        ContentEntityObject entity = this.contentEntityManager.getById(contentId);
        if (entity != null && Boolean.valueOf(entity.getProperties().getStringProperty("inline-comment")).booleanValue() && StringUtils.isNotEmpty((CharSequence)(originalSelection = entity.getProperties().getStringProperty("inline-original-selection")))) {
            return ImmutableMap.of((Object)"inlineContext", (Object)originalSelection);
        }
        return ImmutableMap.of();
    }
}

