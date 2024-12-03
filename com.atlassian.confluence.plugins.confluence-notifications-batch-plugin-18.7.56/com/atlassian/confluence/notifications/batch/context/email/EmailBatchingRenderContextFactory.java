/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.mail.notification.Notification$WatchType
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.NotificationUserService
 *  com.atlassian.confluence.notifications.RenderContextProviderTemplate
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.Spaced
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Iterables
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.atlassian.plugin.tracker.DefaultPluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableMap
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.notifications.batch.context.email;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationUserService;
import com.atlassian.confluence.notifications.RenderContextProviderTemplate;
import com.atlassian.confluence.notifications.batch.content.BatchingPayload;
import com.atlassian.confluence.notifications.batch.descriptor.BatchSectionProviderDescriptor;
import com.atlassian.confluence.notifications.batch.service.BatchSectionProvider;
import com.atlassian.confluence.notifications.batch.service.BatchTarget;
import com.atlassian.confluence.notifications.batch.service.BatchingRoleRecipient;
import com.atlassian.confluence.notifications.batch.template.BatchSection;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Iterables;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.plugin.tracker.DefaultPluginModuleTracker;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;

public class EmailBatchingRenderContextFactory
extends RenderContextProviderTemplate<BatchingPayload> {
    private static final BandanaContext BANDANA_EMAIL_GATEWAY_CONFIGURATION_CONTEXT = new ConfluenceBandanaContext("email-gateway-configuration");
    private static final String ALLOW_TO_CREATE_COMMENT_BY_EMAIL_KEY = "com.atlassian.confluence.plugins.emailgateway.allow.create.comment";
    private final UserAccessor userAccessor;
    private final UserManager userManager;
    private final ContentEntityManager contentEntityManager;
    private final PluginModuleTracker<BatchSectionProvider, BatchSectionProviderDescriptor> batchingPluginTracker;
    private final SettingsManager settingsManager;
    private final NotificationUserService notificationUserService;
    private final BandanaManager bandanaManager;

    public EmailBatchingRenderContextFactory(UserAccessor userAccessor, @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, PluginAccessor pluginAccessor, PluginEventManager pluginEventManager, UserManager userManager, SettingsManager settingsManager, NotificationUserService notificationUserService, BandanaManager bandanaManager) {
        this.userAccessor = userAccessor;
        this.contentEntityManager = contentEntityManager;
        this.userManager = userManager;
        this.settingsManager = settingsManager;
        this.notificationUserService = notificationUserService;
        this.bandanaManager = bandanaManager;
        this.batchingPluginTracker = new DefaultPluginModuleTracker(pluginAccessor, pluginEventManager, BatchSectionProviderDescriptor.class);
    }

    private static Maybe<Notification.WatchType> computeWatchTypeFrom(BatchingRoleRecipient roleRecipient) {
        for (UserRole role : roleRecipient.getUserRoles()) {
            try {
                return Option.some((Object)Notification.WatchType.valueOf((String)role.getID()));
            }
            catch (IllegalArgumentException illegalArgumentException) {
            }
        }
        return Option.none();
    }

    public Map<String, Object> createMessageOriginator(Notification<BatchingPayload> notification) {
        Set<UserKey> originators = ((BatchingPayload)notification.getPayload()).getOriginators();
        if (originators == null || originators.isEmpty() || originators.size() > 1) {
            return Collections.emptyMap();
        }
        return Collections.singletonMap("originatingUser", this.userManager.getUserProfile((UserKey)Iterables.first(originators).get()));
    }

    public Map<String, Object> createMessageMetadata(Notification<BatchingPayload> notification, ServerConfiguration serverConfiguration, Maybe<Either<NotificationAddress, RoleRecipient>> roleRecipient) {
        Set<UserKey> originators = ((BatchingPayload)notification.getPayload()).getOriginators();
        if (originators != null && originators.size() == 1) {
            return super.createMessageMetadata(notification, serverConfiguration, roleRecipient);
        }
        return ImmutableMap.builder().putAll(super.createMessageMetadata(notification, serverConfiguration, roleRecipient)).put((Object)"OVERRIDE_SYSTEM_FROM_FIELD", (Object)this.settingsManager.getGlobalSettings().getSiteTitle()).build();
    }

    protected Maybe<Map<String, Object>> checkedCreate(Notification<BatchingPayload> notification, ServerConfiguration serverConfiguration, Maybe<Either<NotificationAddress, RoleRecipient>> roleRecipient) {
        Either roleRecipientEither = (Either)roleRecipient.get();
        if (!roleRecipientEither.isRight()) {
            return Option.none();
        }
        NotificationContext context = new NotificationContext();
        BatchingRoleRecipient targetUserRecipient = (BatchingRoleRecipient)((Object)roleRecipientEither.right().get());
        ConfluenceUser user = this.userAccessor.getUserByKey(targetUserRecipient.getUserKey());
        if (user != null) {
            context.put("userName", (Object)user.getFullName());
        }
        context.put("modifier", (Object)this.notificationUserService.findUserByKey((User)user, ((BatchingPayload)notification.getPayload()).getOriginatorUserKey()));
        BatchingPayload payload = (BatchingPayload)notification.getPayload();
        LinkedHashMap<ModuleCompleteKey, Object> payloads = payload.getPayloads();
        Map<Option, List<Map.Entry>> groupedSections = payloads.entrySet().stream().collect(Collectors.groupingBy(x -> this.findPayloadProcessor((ModuleCompleteKey)x.getKey())));
        HashMap<ModuleCompleteKey, Integer> payloadIndexes = new HashMap<ModuleCompleteKey, Integer>();
        int index = 0;
        for (ModuleCompleteKey key : payloads.keySet()) {
            payloadIndexes.put(key, index++);
        }
        List<Option<BatchSectionProvider>> sortedProviders = this.getSortedProviders(groupedSections.keySet());
        ArrayList<BatchSection> sections = new ArrayList<BatchSection>();
        BatchTarget batchTarget = null;
        for (Option<BatchSectionProvider> provider : sortedProviders) {
            if (provider.isEmpty()) continue;
            Object contexts = groupedSections.get(provider).stream().map(x -> {
                if (targetUserRecipient.isPayloadIdx((Integer)payloadIndexes.get(x.getKey()))) {
                    return x.getValue();
                }
                return null;
            }).collect(Collectors.toList());
            BatchSectionProvider.BatchOutput output = ((BatchSectionProvider)provider.get()).handle(targetUserRecipient, contexts, serverConfiguration);
            if (output.section().isDefined()) {
                sections.add((BatchSection)output.section().get());
            }
            if (!output.target().isDefined()) continue;
            BatchTarget target = (BatchTarget)output.target().get();
            batchTarget = batchTarget == null || target.getWeight() < batchTarget.getWeight() ? target : batchTarget;
        }
        if (sections.isEmpty()) {
            return Option.none();
        }
        Maybe<Notification.WatchType> maybeWatchType = EmailBatchingRenderContextFactory.computeWatchTypeFrom(targetUserRecipient);
        if (maybeWatchType.isDefined()) {
            context.setWatchType((Notification.WatchType)maybeWatchType.get());
        }
        context.put("messageId", (Object)payload.getBatchingId());
        context.put("sections", sections);
        context.put("userAvatarUrl", (Object)this.userAccessor.getUserProfilePicture((User)user).getDownloadPath());
        String contentType = payload.getContentType();
        context.put("contentType", (Object)contentType);
        switch (contentType) {
            case "page": 
            case "blogpost": 
            case "comment": {
                String batchingId = payload.getBatchingId();
                ContentEntityObject ceo = this.contentEntityManager.getById(ContentId.deserialise((String)batchingId).asLong());
                ConfluenceUser creator = ceo.getCreator();
                Space space = ((Spaced)ceo).getSpace();
                context.put("isAuthor", (Object)(creator != null && targetUserRecipient.getUserKey().equals((Object)creator.getKey()) ? 1 : 0));
                if (sections.size() > 1) {
                    batchTarget = new BatchTarget(ceo.getIdAsString(), 0);
                }
                ContentEntityObject target = batchTarget != null && !batchingId.equals(batchTarget.getContentId()) ? this.contentEntityManager.getById(ContentId.deserialise((String)batchTarget.getContentId()).asLong()) : ceo;
                context.put("contentId", (Object)ceo.getContentId().serialise());
                context.put("contentName", (Object)ceo.getDisplayTitle());
                context.put("contentLink", (Object)target.getUrlPath());
                context.put("spaceKey", (Object)space.getKey());
                context.put("spaceName", (Object)space.getName());
                context.put("spaceUrlPath", (Object)space.getUrlPath());
                context.put("soyInjectedData", Collections.singletonMap("batchTarget", batchTarget == null ? new BatchTarget() : batchTarget));
                break;
            }
            case "user": {
                ConfluenceUser targetUser = this.userAccessor.getUserByKey(new UserKey(payload.getBatchingId()));
                context.put("contentName", (Object)targetUser.getFullName());
                context.put("contentLink", (Object)("/display/~" + targetUser.getName()));
                context.put("space", null);
            }
        }
        if (batchTarget != null && batchTarget.getWeight() == 1) {
            boolean replyByEmailEnabled = (Boolean)Optional.ofNullable(this.bandanaManager.getValue(BANDANA_EMAIL_GATEWAY_CONFIGURATION_CONTEXT, ALLOW_TO_CREATE_COMMENT_BY_EMAIL_KEY)).orElse(false);
            context.put("replyByEmailEnabled", (Object)replyByEmailEnabled);
        }
        return Option.some((Object)context.getMap());
    }

    private List<Option<BatchSectionProvider>> getSortedProviders(Collection<Option<BatchSectionProvider>> providers) {
        HashMap providersWeight = new HashMap();
        this.batchingPluginTracker.getModuleDescriptors().forEach(descriptor -> providersWeight.put(descriptor.getModuleClass(), descriptor.getWeight()));
        ArrayList<Option<BatchSectionProvider>> sectionProviders = new ArrayList<Option<BatchSectionProvider>>(providers);
        sectionProviders.sort((o1, o2) -> {
            if (o1.isDefined() && o2.isDefined()) {
                return (Integer)providersWeight.get(((BatchSectionProvider)o1.get()).getClass()) - (Integer)providersWeight.get(((BatchSectionProvider)o2.get()).getClass());
            }
            return 0;
        });
        return sectionProviders;
    }

    private Option<BatchSectionProvider> findPayloadProcessor(ModuleCompleteKey key) {
        for (BatchSectionProviderDescriptor batchingDescriptor : this.batchingPluginTracker.getModuleDescriptors()) {
            if (!batchingDescriptor.getNotificationKeys().contains(key)) continue;
            return Option.some((Object)((BatchSectionProvider)batchingDescriptor.getModule()));
        }
        return Option.none();
    }
}

