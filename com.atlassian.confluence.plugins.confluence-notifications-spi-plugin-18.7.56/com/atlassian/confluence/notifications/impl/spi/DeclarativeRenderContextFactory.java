/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.user.AuthenticatedUserImpersonator
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.notifications.api.event.EventContextBuilder
 *  com.atlassian.plugin.notifications.api.event.NotificationEvent
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.RenderContextFactoryTemplate
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.atlassian.plugin.notifications.spi.salext.UserI18nResolver
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.user.User
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.notifications.impl.spi;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationKeyCondition;
import com.atlassian.confluence.notifications.RenderContextProvider;
import com.atlassian.confluence.notifications.impl.FakeHttpRequestInjector;
import com.atlassian.confluence.notifications.impl.NotificationDescriptorLocator;
import com.atlassian.confluence.notifications.impl.ObjectMapperFactory;
import com.atlassian.confluence.notifications.impl.descriptors.NotificationTemplateDescriptor;
import com.atlassian.confluence.notifications.impl.spi.AnalyticsContext;
import com.atlassian.confluence.user.AuthenticatedUserImpersonator;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.notifications.api.event.EventContextBuilder;
import com.atlassian.plugin.notifications.api.event.NotificationEvent;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.RenderContextFactoryTemplate;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.plugin.notifications.spi.salext.UserI18nResolver;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.user.User;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeclarativeRenderContextFactory
extends RenderContextFactoryTemplate<NotificationEvent> {
    static final String CONTEXT_ANALYTICS_CONTEXT = "analyticsContext";
    private static final Logger log = LoggerFactory.getLogger(DeclarativeRenderContextFactory.class);
    private static final String CONTEXT_WEB_FRAGMENT_CONTEXT = "webFragmentContext";
    private static final String CONTEXT_ACTION_TYPE = "actionType";
    private static final String CONTEXT_RECIPIENT = "recipient";
    private static final String CONTEXT_RECIPIENT_KEY = "recipientKey";
    private static final String CONTEXT_CONTENT = "content";
    private static final String CONTEXT_CONTENT_ID = "contentId";
    private static final String CONTEXT_MODIFIER = "modifier";
    private static final String CONTEXT_ACTOR = "actor";
    private static final String CONTEXT_ACTOR_KEY = "actorKey";
    private final UserI18nResolver i18nResolver;
    private final UserAccessor userAccessor;
    private final NotificationDescriptorLocator locator;
    private final UserManager userManager;
    private final FakeHttpRequestInjector requestWrapper;
    private final ObjectMapperFactory objectMapperFactory;
    private final TransactionTemplate transactionTemplate;

    public DeclarativeRenderContextFactory(UserI18nResolver i18nResolver, UserAccessor userAccessor, NotificationDescriptorLocator locator, UserManager userManager, FakeHttpRequestInjector requestWrapper, ObjectMapperFactory objectMapperFactory, TransactionTemplate transactionTemplate) {
        this.i18nResolver = i18nResolver;
        this.userAccessor = userAccessor;
        this.locator = locator;
        this.userManager = userManager;
        this.requestWrapper = requestWrapper;
        this.objectMapperFactory = objectMapperFactory;
        this.transactionTemplate = transactionTemplate;
    }

    private static Map<String, Object> buildEmailTrackingContext(Map<String, Object> renderContext, ConfluenceUser recipient, ModuleCompleteKey notificationKey) {
        Object modifier;
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put((Object)CONTEXT_ACTION_TYPE, (Object)notificationKey.getCompleteKey());
        builder.put((Object)CONTEXT_RECIPIENT, (Object)recipient);
        builder.put((Object)CONTEXT_RECIPIENT_KEY, (Object)recipient.getKey());
        if (renderContext.get(CONTEXT_CONTENT) instanceof Content) {
            builder.put((Object)CONTEXT_CONTENT_ID, (Object)((Content)renderContext.get(CONTEXT_CONTENT)).getId());
        }
        if ((modifier = renderContext.get(CONTEXT_MODIFIER)) instanceof ConfluenceUser) {
            builder.put((Object)CONTEXT_ACTOR, modifier);
            builder.put((Object)CONTEXT_ACTOR_KEY, (Object)((ConfluenceUser)modifier).getKey());
        }
        return builder.build();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Option<Map<String, Object>> createContextChecked(NotificationEvent event, ServerConfiguration serverConfiguration, Either<NotificationAddress, RoleRecipient> recipientData) {
        ModuleCompleteKey notificationKey = new ModuleCompleteKey(event.getKey());
        UserKeyRoleRecipient roleRecipient = recipientData.isRight() ? (RoleRecipient)recipientData.right().get() : UserKeyRoleRecipient.UNKNOWN;
        UserKey userKey = roleRecipient.getUserKey();
        ConfluenceUser recipient = this.userAccessor.getExistingUserByKey(userKey);
        try {
            this.i18nResolver.setUser(userKey);
            Option option = (Option)this.transactionTemplate.execute(() -> this.lambda$createContextChecked$1(event, userKey, (RoleRecipient)roleRecipient, serverConfiguration, recipientData, notificationKey, recipient));
            return option;
        }
        finally {
            this.i18nResolver.setUser(null);
        }
    }

    private Callable<Option<Map<String, Object>>> buildNotificationContext(NotificationEvent event, UserKey userKey, RoleRecipient roleRecipient, ServerConfiguration serverConfiguration, Either<NotificationAddress, RoleRecipient> recipientData, ModuleCompleteKey notificationKey, ConfluenceUser recipient) {
        return () -> {
            HashMap<String, Object> context = new HashMap<String, Object>();
            Notification notification = (Notification)event.getOriginalEvent();
            String mediumKey = serverConfiguration.getNotificationMedium().getKey();
            RenderContextProvider contextProvider = (RenderContextProvider)((NotificationTemplateDescriptor)((Object)((Object)this.locator.findTemplateDescriptor(notification, mediumKey).get()))).getModule();
            context.putAll(EventContextBuilder.buildContext((NotificationEvent)event, (I18nResolver)this.i18nResolver, (UserKey)userKey, (UserRole)roleRecipient.getRole(), (ServerConfiguration)serverConfiguration));
            context.putAll(this.buildOriginatorContext(notification, contextProvider));
            Maybe<Map<String, Object>> recipientContext = this.buildRecipientProviderContext(event, serverConfiguration, recipientData, contextProvider);
            if (recipientContext.isEmpty()) {
                return Option.none();
            }
            context.putAll((Map)recipientContext.get());
            context.putAll(this.buildAnalyticsContext(event, serverConfiguration, recipientData));
            context.put("messageMetadata", this.buildMetadataContext(event, serverConfiguration, recipientData, contextProvider));
            if (UserKeyRoleRecipient.UNKNOWN.equals((Object)roleRecipient)) {
                context.put(CONTEXT_WEB_FRAGMENT_CONTEXT, Collections.EMPTY_MAP);
            } else {
                context.put(CONTEXT_WEB_FRAGMENT_CONTEXT, DeclarativeRenderContextFactory.buildEmailTrackingContext(context, recipient, notificationKey));
            }
            return Option.some(NotificationKeyCondition.copyWithNotificationKey(context, notificationKey));
        };
    }

    private Maybe<Map<String, Object>> buildRecipientProviderContext(NotificationEvent event, ServerConfiguration configuration, Either<NotificationAddress, RoleRecipient> recipientData, RenderContextProvider contextProvider) {
        Notification notification = (Notification)event.getOriginalEvent();
        Maybe<Map<String, Object>> maybe = contextProvider.create(notification, configuration, (Maybe<Either<NotificationAddress, RoleRecipient>>)Option.some(recipientData));
        if (maybe.isEmpty() && log.isDebugEnabled()) {
            Either serializedForm = this.objectMapperFactory.verifyObjectSerializable(notification.getPayload());
            if (serializedForm.isLeft()) {
                throw new IllegalStateException("The payload " + notification.getPayload().getClass().getName() + " is not serializable!", (Throwable)serializedForm.left().get());
            }
            log.debug(String.format("event [%s] did not produce any recipient context for notification payload [%s]", event.getKey(), serializedForm.right().get()));
        }
        return maybe;
    }

    private Map<String, Object> buildOriginatorContext(Notification notification, RenderContextProvider contextProvider) {
        Map<String, Object> originatorContext = contextProvider.createMessageOriginator(notification);
        if (!originatorContext.isEmpty()) {
            return originatorContext;
        }
        Maybe<UserKey> userKey = notification.getOriginator();
        UserKey originator = (UserKey)userKey.getOrNull();
        return Collections.singletonMap("originatingUser", this.userManager.getUserProfile(originator));
    }

    private Map<String, Object> buildMetadataContext(NotificationEvent event, ServerConfiguration configuration, Either<NotificationAddress, RoleRecipient> recipientData, RenderContextProvider contextProvider) {
        return contextProvider.createMessageMetadata((Notification)event.getOriginalEvent(), configuration, (Maybe<Either<NotificationAddress, RoleRecipient>>)Option.some(recipientData));
    }

    private Map<String, Object> buildAnalyticsContext(NotificationEvent notification, ServerConfiguration serverConfiguration, Either<NotificationAddress, RoleRecipient> recipientData) {
        Date timestamp = new Date();
        String mediumKey = serverConfiguration.getNotificationMedium().getKey();
        Option recipientUserKey = recipientData.right().toOption().map(this.toUserKey());
        ModuleCompleteKey notificationKey = new ModuleCompleteKey(notification.getKey());
        AnalyticsContext analyticsContext = new AnalyticsContext(timestamp, mediumKey, (Option<UserKey>)recipientUserKey, notificationKey);
        return Collections.singletonMap(CONTEXT_ANALYTICS_CONTEXT, analyticsContext);
    }

    private Function<RoleRecipient, UserKey> toUserKey() {
        return input -> input.getUserKey();
    }

    private /* synthetic */ Option lambda$createContextChecked$1(NotificationEvent event, UserKey userKey, RoleRecipient roleRecipient, ServerConfiguration serverConfiguration, Either recipientData, ModuleCompleteKey notificationKey, ConfluenceUser recipient) {
        return this.requestWrapper.withRequest(() -> (Option)AuthenticatedUserImpersonator.REQUEST_AGNOSTIC.asUser(this.buildNotificationContext(event, userKey, roleRecipient, serverConfiguration, (Either<NotificationAddress, RoleRecipient>)recipientData, notificationKey, recipient), (User)recipient));
    }
}

