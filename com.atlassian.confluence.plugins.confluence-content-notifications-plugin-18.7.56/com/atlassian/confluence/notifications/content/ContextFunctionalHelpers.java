/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.mail.notification.Notification$WatchType
 *  com.atlassian.confluence.notifications.CachedContentFinder
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.fugue.Effect
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.google.common.base.Function
 */
package com.atlassian.confluence.notifications.content;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.notifications.CachedContentFinder;
import com.atlassian.confluence.notifications.content.ContentException;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.fugue.Effect;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.ModuleCompleteKey;
import com.google.common.base.Function;
import java.util.Locale;
import java.util.UUID;

public class ContextFunctionalHelpers {
    public static ToContentFunction toContent(CachedContentFinder cachedContentFinder, ContentType type, UUID uuid, ModuleCompleteKey key, Locale locale, String expansionExpression) {
        return new ToContentFunction(uuid, key, locale, type, expansionExpression, cachedContentFinder);
    }

    public static ToContentFunction toContent(CachedContentFinder cachedContentFinder, ContentType type, UUID uuid, ModuleCompleteKey key, Locale locale, Expansion ... expansionExpression) {
        return new ToContentFunction(uuid, key, locale, type, expansionExpression, cachedContentFinder);
    }

    public static AddToContext addToContext(NotificationContext context, String key) {
        return new AddToContext(context, key);
    }

    public static SetWatchType setWatchType(NotificationContext context) {
        return new SetWatchType(context);
    }

    public static <X, Y> Function<Either<X, Y>, Option<X>> toLeft() {
        return input -> input.left().toOption();
    }

    public static <X, Y> Function<Either<X, Y>, Option<Y>> toRight() {
        return input -> input.right().toOption();
    }

    public static class SetWatchType
    implements Effect<Notification.WatchType> {
        private NotificationContext notificationContext;

        public SetWatchType(NotificationContext notificationContext) {
            this.notificationContext = notificationContext;
        }

        public void apply(Notification.WatchType watchType) {
            this.notificationContext.setWatchType(watchType);
        }
    }

    public static class ToContentFunction
    implements Function<Long, Either<ContentException, Content>> {
        private final ContentType contentType;
        private final Expansion[] expansions;
        private final CachedContentFinder cachedContentFinder;
        private final UUID uuid;
        private final ModuleCompleteKey key;
        private final Locale locale;

        public ToContentFunction(UUID uuid, ModuleCompleteKey key, Locale locale, ContentType contentType, String expansionExpression, CachedContentFinder cachedContentFinder) {
            this.uuid = uuid;
            this.key = key;
            this.locale = locale;
            this.contentType = contentType;
            this.expansions = ExpansionsParser.parse((String)expansionExpression);
            this.cachedContentFinder = cachedContentFinder;
        }

        public ToContentFunction(UUID uuid, ModuleCompleteKey key, Locale locale, ContentType contentType, Expansion[] expansions, CachedContentFinder cachedContentFinder) {
            this.uuid = uuid;
            this.key = key;
            this.locale = locale;
            this.contentType = contentType;
            this.expansions = expansions;
            this.cachedContentFinder = cachedContentFinder;
        }

        public Either<ContentException, Content> apply(Long id) {
            if (id == null) {
                return Either.left((Object)new ContentException("Unable to load content - id is null", new Object[0]));
            }
            ContentId contentId = ContentId.of((ContentType)this.contentType, (long)id);
            return this.cachedContentFinder.getContent(this.uuid, this.key, this.locale, contentId, this.expansions).toRight(ContentException.contentExceptionSupplier("Unable to find content with id %d", id));
        }
    }

    public static class AddToContext
    implements Effect<Content> {
        private final NotificationContext notificationContext;
        private String key;

        public AddToContext(NotificationContext notificationContext, String key) {
            this.notificationContext = notificationContext;
            this.key = key;
        }

        public void apply(Content content) {
            this.notificationContext.put(this.key, (Object)content);
        }
    }
}

