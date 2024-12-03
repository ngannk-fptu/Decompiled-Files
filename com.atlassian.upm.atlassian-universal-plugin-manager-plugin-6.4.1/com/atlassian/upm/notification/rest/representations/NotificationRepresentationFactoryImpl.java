/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.notification.rest.representations;

import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.upm.UpmHostApplicationInformation;
import com.atlassian.upm.UpmInformation;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.UpmUriEscaper;
import com.atlassian.upm.core.rest.representations.LinksMapBuilder;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.notification.Notification;
import com.atlassian.upm.notification.NotificationCache;
import com.atlassian.upm.notification.NotificationCollection;
import com.atlassian.upm.notification.NotificationType;
import com.atlassian.upm.notification.NotificationTypes;
import com.atlassian.upm.notification.rest.representations.NotificationGroupCollectionRepresentation;
import com.atlassian.upm.notification.rest.representations.NotificationGroupRepresentation;
import com.atlassian.upm.notification.rest.representations.NotificationRepresentation;
import com.atlassian.upm.notification.rest.representations.NotificationRepresentationFactory;
import com.atlassian.upm.request.PluginRequest;
import com.atlassian.upm.request.PluginRequestStore;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.rest.representations.InstalledMarketplacePluginRepresentation;
import com.atlassian.upm.rest.representations.UpmLinkBuilder;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationRepresentationFactoryImpl
implements NotificationRepresentationFactory {
    private static final Logger log = LoggerFactory.getLogger(NotificationRepresentationFactoryImpl.class);
    private static final String IMAGES_PLUGIN_ICON_DEFAULT_PNG = "images/plugin-icon-default.png";
    private final NotificationCache notificationCache;
    private final I18nResolver i18nResolver;
    private final UpmUriBuilder uriBuilder;
    private final PluginRetriever pluginRetriever;
    private final UpmLinkBuilder linkBuilder;
    private final UserManager userManager;
    private final WebResourceManager webResourceManager;
    private final PermissionEnforcer permissionEnforcer;
    private final NotificationTypes notificationTypes;
    private final PluginRequestStore pluginRequestStore;
    private final UpmRepresentationFactory representationFactory;
    private final UpmInformation upm;
    private final UpmHostApplicationInformation appInfo;

    public NotificationRepresentationFactoryImpl(NotificationCache notificationCache, I18nResolver i18nResolver, UpmUriBuilder uriBuilder, PluginRetriever pluginRetriever, UpmLinkBuilder linkBuilder, UserManager userManager, WebResourceManager webResourceManager, PermissionEnforcer permissionEnforcer, NotificationTypes notificationTypes, PluginRequestStore pluginRequestStore, UpmRepresentationFactory representationFactory, UpmInformation upm, UpmHostApplicationInformation appInfo) {
        this.notificationCache = Objects.requireNonNull(notificationCache, "notificationCache");
        this.i18nResolver = Objects.requireNonNull(i18nResolver, "i18nResolver");
        this.uriBuilder = Objects.requireNonNull(uriBuilder, "uriBuilder");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.linkBuilder = Objects.requireNonNull(linkBuilder, "linkBuilder");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.webResourceManager = Objects.requireNonNull(webResourceManager, "webResourceManager");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.notificationTypes = Objects.requireNonNull(notificationTypes, "notificationTypes");
        this.pluginRequestStore = Objects.requireNonNull(pluginRequestStore, "pluginRequestStore");
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
        this.upm = Objects.requireNonNull(upm, "upm");
        this.appInfo = Objects.requireNonNull(appInfo, "appInfo");
    }

    private String getMessageI18n(NotificationType type, int count) {
        return this.i18nResolver.getText(count == 1 ? this.notificationTypes.getSingularMessageI18nKey(type) : this.notificationTypes.getPluralMessageI18nKey(type), new Serializable[]{Integer.valueOf(count)});
    }

    @Override
    public NotificationGroupCollectionRepresentation getNotificationGroupCollection(Iterable<NotificationCollection> notificationCollections, Option<UserKey> userKey) {
        LinksMapBuilder links;
        ArrayList<NotificationGroupRepresentation> notificationGroups = new ArrayList<NotificationGroupRepresentation>();
        for (NotificationCollection notificationCollection : notificationCollections) {
            NotificationGroupRepresentation notificationGroup;
            if (!this.permissionEnforcer.hasPermission(notificationCollection.getType().getRequiredPermission()) || (notificationGroup = this.getNotificationGroup(notificationCollection, userKey)).getNotificationCount() <= 0) continue;
            notificationGroups.add(notificationGroup);
        }
        if (userKey.isDefined()) {
            links = this.linkBuilder.buildLinkForSelf(this.uriBuilder.buildNotificationCollectionUri(userKey.get()));
            for (NotificationType type : NotificationType.values()) {
                links.putIfPermitted(Permission.GET_NOTIFICATIONS, type.getKey() + "-notifications", this.uriBuilder.buildNotificationCollectionUri(userKey.get(), type));
            }
        } else {
            links = this.linkBuilder.buildLinkForSelf(this.uriBuilder.buildNotificationCollectionUri());
            links.putIfPermitted(Permission.GET_NOTIFICATIONS, "my-notifications", this.uriBuilder.buildNotificationCollectionUri(this.userManager.getRemoteUserKey()));
        }
        return new NotificationGroupCollectionRepresentation(Collections.unmodifiableList(notificationGroups), links.build());
    }

    @Override
    public NotificationGroupRepresentation getNotificationGroup(NotificationCollection notificationCollection, Option<UserKey> userKey) {
        Boolean dismissed;
        LinksMapBuilder links;
        NotificationType type = notificationCollection.getType();
        Collection notifications = StreamSupport.stream(notificationCollection.getDisplayableNotifications().spliterator(), false).map(this.toNotificationRepresentation(userKey)).filter(Objects::nonNull).collect(Collectors.toList());
        long count = StreamSupport.stream(notificationCollection.spliterator(), false).count();
        int filteredCount = count == 0L ? notificationCollection.getNotificationCount() : notifications.size();
        if (userKey.isDefined()) {
            links = this.linkBuilder.buildLinkForSelf(this.uriBuilder.buildNotificationCollectionUri(userKey.get(), type));
            links.putIfPermitted(Permission.MANAGE_NOTIFICATIONS, "post-notifications", this.uriBuilder.buildNotificationCollectionUri(userKey.get(), type));
            dismissed = this.notificationCache.isNotificationTypeDismissed(type, userKey);
        } else {
            links = this.linkBuilder.builder();
            dismissed = null;
        }
        links.put("default-icon", URI.create(this.webResourceManager.getStaticPluginResource(this.upm.getPluginKey() + ":upm-web-resources", IMAGES_PLUGIN_ICON_DEFAULT_PNG, UrlMode.AUTO))).put("target", this.getTargetLink(type, Option.none()));
        return new NotificationGroupRepresentation(type, (Collection<NotificationRepresentation>)notifications, filteredCount, dismissed, this.i18nResolver.getText(this.notificationTypes.getTitleI18nKey(type)), this.getMessageI18n(type, filteredCount), links.build());
    }

    @Override
    public NotificationRepresentation getNotification(Notification notification, Option<UserKey> userKey) {
        return this.toNotificationRepresentation(userKey).apply(notification);
    }

    private Function<Notification, NotificationRepresentation> toNotificationRepresentation(Option<UserKey> userKey) {
        return new ToNotificationRepresentation(userKey);
    }

    private URI getTargetLink(NotificationType type, Option<Plugin> installedPlugin) {
        switch (type) {
            case PLUGIN_REQUEST: {
                return this.uriBuilder.buildUpmViewPluginRequestsUri();
            }
            case PLUGIN_UPDATE_AVAILABLE: 
            case EXPIRED_EVALUATION_PLUGIN_LICENSE: 
            case NEARLY_EXPIRED_EVALUATION_PLUGIN_LICENSE: 
            case EDITION_MISMATCH_PLUGIN_LICENSE: 
            case MAINTENANCE_EXPIRED_PLUGIN_LICENSE: 
            case MAINTENANCE_NEARLY_EXPIRED_PLUGIN_LICENSE: 
            case DATA_CENTER_EXPIRED_PLUGIN_LICENSE: 
            case DATA_CENTER_NEARLY_EXPIRED_PLUGIN_LICENSE: {
                Iterator<Plugin> iterator = installedPlugin.iterator();
                if (iterator.hasNext()) {
                    Plugin plugin = iterator.next();
                    return this.uriBuilder.buildUpmUri("action-required", plugin.getKey(), false);
                }
                return this.uriBuilder.buildUpmUri("action-required", false);
            }
            case AUTO_UPDATED_PLUGIN: 
            case AUTO_UPDATED_UPM: {
                Iterator<Plugin> iterator = installedPlugin.iterator();
                if (iterator.hasNext()) {
                    Plugin plugin = iterator.next();
                    return this.uriBuilder.buildUpmUri("user-installed", plugin.getKey(), false);
                }
                return this.uriBuilder.buildUpmUri("user-installed", false);
            }
        }
        throw new IllegalArgumentException("Unhandled notification type: " + (Object)((Object)type));
    }

    private class ToNotificationRepresentation
    implements Function<Notification, NotificationRepresentation> {
        private final Option<UserKey> userKey;

        ToNotificationRepresentation(Option<UserKey> userKey) {
            this.userKey = userKey;
        }

        @Override
        public NotificationRepresentation apply(Notification notification) {
            Boolean dismissed;
            LinksMapBuilder links;
            NotificationType type = notification.getType();
            String pluginKey = notification.getPluginKey();
            Option<Plugin> maybePlugin = NotificationRepresentationFactoryImpl.this.pluginRetriever.getPlugin(pluginKey);
            InstalledMarketplacePluginRepresentation pluginSummaryRepresentation = null;
            if (this.userKey.isDefined()) {
                links = NotificationRepresentationFactoryImpl.this.linkBuilder.buildLinkForSelf(NotificationRepresentationFactoryImpl.this.uriBuilder.buildNotificationUri(this.userKey.get(), type, UpmUriEscaper.escape(pluginKey)));
                links.putIfPermitted(Permission.GET_NOTIFICATIONS, type.getKey() + "-notifications", NotificationRepresentationFactoryImpl.this.uriBuilder.buildNotificationCollectionUri(this.userKey.get(), type));
                links.putIfPermitted(Permission.MANAGE_NOTIFICATIONS, "post-notifications", NotificationRepresentationFactoryImpl.this.uriBuilder.buildNotificationUri(this.userKey.get(), type, UpmUriEscaper.escape(pluginKey)));
                dismissed = NotificationRepresentationFactoryImpl.this.notificationCache.isNotificationDismissed(type, this.userKey, pluginKey);
            } else {
                links = NotificationRepresentationFactoryImpl.this.linkBuilder.builder();
                dismissed = null;
            }
            links.put("target", NotificationRepresentationFactoryImpl.this.getTargetLink(type, maybePlugin));
            if (maybePlugin.isDefined()) {
                for (Plugin plugin : maybePlugin) {
                    pluginSummaryRepresentation = InstalledMarketplacePluginRepresentation.toEntry(NotificationRepresentationFactoryImpl.this.representationFactory, NotificationRepresentationFactoryImpl.this.appInfo, Collections.emptyList(), Collections.emptyList()).apply(plugin);
                }
            } else {
                List<PluginRequest> requests = NotificationRepresentationFactoryImpl.this.pluginRequestStore.getRequests(pluginKey);
                if (requests.isEmpty()) {
                    NotificationRepresentationFactoryImpl.this.pluginRequestStore.removeRequests(pluginKey);
                    log.debug("Notification existed and was removed for uninstalled and unrequested plugin: " + pluginKey);
                    return null;
                }
                Map<String, URI> pluginLinks = NotificationRepresentationFactoryImpl.this.linkBuilder.builder().putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, "plugin-icon", NotificationRepresentationFactoryImpl.this.uriBuilder.buildPluginIconLocationUri(pluginKey)).putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, "plugin-logo", NotificationRepresentationFactoryImpl.this.uriBuilder.buildPluginLogoLocationUri(pluginKey)).build();
                pluginSummaryRepresentation = new InstalledMarketplacePluginRepresentation(pluginLinks, pluginKey, requests.get(0).getPluginName(), null, false, null, pluginKey, false, null, false, false, null, false, false, false);
            }
            return new NotificationRepresentation(type, pluginSummaryRepresentation, dismissed, NotificationRepresentationFactoryImpl.this.i18nResolver.getText(NotificationRepresentationFactoryImpl.this.notificationTypes.getTitleI18nKey(type)), NotificationRepresentationFactoryImpl.this.i18nResolver.getText(NotificationRepresentationFactoryImpl.this.notificationTypes.getIndividualNotificationI18nKey(type), new Serializable[]{StringEscapeUtils.escapeHtml4(pluginSummaryRepresentation.getName())}), links.build());
        }
    }
}

