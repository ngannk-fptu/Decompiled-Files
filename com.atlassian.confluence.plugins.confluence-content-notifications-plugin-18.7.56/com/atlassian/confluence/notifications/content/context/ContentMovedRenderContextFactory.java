/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.core.MaybeNot
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.notifications.CachedContentFinder
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.NotificationUserService
 *  com.atlassian.confluence.notifications.RenderContextProviderTemplate
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Effect
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 */
package com.atlassian.confluence.notifications.content.context;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.notifications.CachedContentFinder;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationUserService;
import com.atlassian.confluence.notifications.RenderContextProviderTemplate;
import com.atlassian.confluence.notifications.content.CommonContentExpansions;
import com.atlassian.confluence.notifications.content.ContentMovedPayload;
import com.atlassian.confluence.notifications.content.ContextFunctionalHelpers;
import com.atlassian.confluence.notifications.content.WatchTypeUtil;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Effect;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

public class ContentMovedRenderContextFactory
extends RenderContextProviderTemplate<ContentMovedPayload> {
    private static final int MAX_CHILDREN_TO_DISPLAY_FOR_MOVE_NOTIFICATION = 10;
    private final CachedContentFinder cachedContentFinder;
    private final UserAccessor userAccessor;
    private final SpaceService spaceService;
    private final PageManager pageManager;
    private final LocaleManager localeManager;
    private final NotificationUserService notificationUserService;

    public ContentMovedRenderContextFactory(CachedContentFinder cachedContentFinder, UserAccessor userAccessor, SpaceService spaceService, PageManager pageManager, LocaleManager localeManager, NotificationUserService notificationUserService) {
        this.cachedContentFinder = cachedContentFinder;
        this.userAccessor = userAccessor;
        this.spaceService = spaceService;
        this.pageManager = pageManager;
        this.localeManager = localeManager;
        this.notificationUserService = notificationUserService;
    }

    protected Maybe<Map<String, Object>> checkedCreate(Notification<ContentMovedPayload> notification, ServerConfiguration serverConfiguration, Maybe<Either<NotificationAddress, RoleRecipient>> recipientData) {
        Option currentParent;
        if (recipientData.isEmpty() || ((Either)recipientData.get()).isLeft()) {
            return MaybeNot.becauseOf((String)"This factory exposes content, thus recipient has to be provided in order to perform a VIEW permission check.", (Object[])new Object[0]);
        }
        ContentMovedPayload payload = (ContentMovedPayload)notification.getPayload();
        RoleRecipient roleRecipient = (RoleRecipient)((Either)recipientData.get()).right().get();
        UserKey recipientKey = roleRecipient.getUserKey();
        NotificationContext notificationContext = new NotificationContext();
        Locale recipientLocale = this.localeManager.getLocale(this.notificationUserService.findUserForKey(recipientKey));
        ConfluenceUser modifier = this.userAccessor.getExistingUserByKey((UserKey)notification.getOriginator().get());
        ContentId currentContentId = ContentId.of((ContentType)payload.getContentType(), (long)payload.getContentId());
        Option maybeContent = this.cachedContentFinder.getContent(notification.getId(), notification.getKey(), recipientLocale, currentContentId, new Expansion[]{CommonContentExpansions.SPACE});
        if (maybeContent.isEmpty()) {
            return MaybeNot.becauseOf((String)"Unable to find content with id [%s], this might be because it does not exist or recipient [%s] does not have VIEW permission.", (Object[])new Object[]{payload.getContentId(), recipientKey});
        }
        Option maybeOldSpace = this.spaceService.find(new Expansion[0]).withKeys(new String[]{payload.getOriginalSpaceKey()}).fetchOne();
        if (maybeOldSpace.isEmpty()) {
            return MaybeNot.becauseOf((String)"Unable to find space with space key [%s], this might be because it does not exist or recipient [%s] does not have VIEW permission.", (Object[])new Object[]{payload.getOriginalSpaceKey(), recipientKey});
        }
        ContextFunctionalHelpers.ToContentFunction toContent = ContextFunctionalHelpers.toContent(this.cachedContentFinder, payload.getContentType(), notification.getId(), notification.getKey(), recipientLocale, new Expansion[0]);
        Option originalParent = payload.getOriginalParentPageId().map((Function)toContent);
        Either sequenceRight = Either.sequenceRight((Iterable)Iterables.concat((Iterable)originalParent, (Iterable)(currentParent = payload.getCurrentParentPageId().map((Function)toContent))));
        if (sequenceRight.isLeft()) {
            return MaybeNot.becauseOfException((Exception)((Exception)sequenceRight.left().get()));
        }
        originalParent.flatMap(ContextFunctionalHelpers.toRight()).foreach((Effect)ContextFunctionalHelpers.addToContext(notificationContext, "oldParentPage"));
        currentParent.flatMap(ContextFunctionalHelpers.toRight()).foreach((Effect)ContextFunctionalHelpers.addToContext(notificationContext, "oldParentPage"));
        if (payload.hasMovedChildren()) {
            Collection descendants = this.pageManager.getDescendantIds(this.pageManager.getPage(currentContentId.asLong()));
            Either titles = Either.sequenceRight((Iterable)Iterables.transform((Iterable)Iterables.limit((Iterable)descendants, (int)10), (Function)ContextFunctionalHelpers.toContent(this.cachedContentFinder, payload.getContentType(), notification.getId(), notification.getKey(), recipientLocale, new Expansion[0])));
            if (titles.isLeft()) {
                return MaybeNot.becauseOfException((Exception)((Exception)titles.left().get()));
            }
            titles.right().foreach(movedChildPages -> notificationContext.put("movedChildPages", movedChildPages));
        }
        Content content = (Content)maybeContent.get();
        Space oldSpace = (Space)maybeOldSpace.get();
        Space currentSpace = content.getSpace();
        notificationContext.put("modifier", (Object)modifier);
        notificationContext.put("content", (Object)content);
        notificationContext.put("space", (Object)currentSpace);
        notificationContext.put("oldSpace", (Object)oldSpace);
        WatchTypeUtil.computeWatchTypeFrom(roleRecipient.getRole()).foreach((Effect)ContextFunctionalHelpers.setWatchType(notificationContext));
        return Option.some((Object)notificationContext.getMap());
    }
}

