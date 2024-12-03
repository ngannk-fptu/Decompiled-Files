/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Either
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.plugin.notifications.config.scheme;

import com.atlassian.fugue.Either;
import com.atlassian.plugin.notifications.api.ErrorCollection;
import com.atlassian.plugin.notifications.api.medium.Server;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.ServerFactory;
import com.atlassian.plugin.notifications.api.medium.ServerManager;
import com.atlassian.plugin.notifications.api.medium.recipient.RecipientRepresentation;
import com.atlassian.plugin.notifications.api.notification.NotificationRepresentation;
import com.atlassian.plugin.notifications.api.notification.NotificationSchemeRepresentation;
import com.atlassian.plugin.notifications.api.notification.NotificationSchemeService;
import com.atlassian.plugin.notifications.api.notification.NotificationSchemeStore;
import com.atlassian.plugin.notifications.spi.NotificationEventProvider;
import com.atlassian.plugin.notifications.spi.NotificationFilterProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import java.io.Serializable;
import org.springframework.beans.factory.annotation.Qualifier;

public class NotificationSchemeServiceImpl
implements NotificationSchemeService {
    private final I18nResolver i18n;
    private final NotificationSchemeStore store;
    private final NotificationEventProvider eventProvider;
    private final NotificationFilterProvider filterProvider;
    private final ServerManager serverManager;
    private final ServerFactory serverFactory;
    private final UserManager userManager;

    public NotificationSchemeServiceImpl(UserManager userManager, NotificationSchemeStore store, @Qualifier(value="i18nResolver") I18nResolver i18n, NotificationEventProvider eventProvider, NotificationFilterProvider filterProvider, ServerManager serverManager, ServerFactory serverFactory) {
        this.userManager = userManager;
        this.i18n = i18n;
        this.store = store;
        this.eventProvider = eventProvider;
        this.filterProvider = filterProvider;
        this.serverManager = serverManager;
        this.serverFactory = serverFactory;
    }

    @Override
    public Either<ErrorCollection, NotificationSchemeRepresentation> getScheme(String loggedInUser) {
        ErrorCollection errors = new ErrorCollection();
        if (!this.userManager.isSystemAdmin(loggedInUser)) {
            errors.addErrorMessage(this.i18n.getText("perm.violation.desc"), ErrorCollection.Reason.FORBIDDEN);
            return Either.left((Object)errors);
        }
        NotificationSchemeRepresentation scheme = this.store.getScheme();
        if (scheme == null) {
            errors.addErrorMessage(this.i18n.getText("notifications.plugin.scheme.not.found"), ErrorCollection.Reason.NOT_FOUND);
            return Either.left((Object)errors);
        }
        return Either.right((Object)scheme);
    }

    @Override
    public Either<ErrorCollection, NotificationRepresentation> getSchemeNotification(String loggedInUser, int notificationId) {
        ErrorCollection errors = new ErrorCollection();
        if (!this.userManager.isSystemAdmin(loggedInUser)) {
            errors.addErrorMessage(this.i18n.getText("perm.violation.desc"), ErrorCollection.Reason.FORBIDDEN);
            return Either.left((Object)errors);
        }
        NotificationSchemeRepresentation scheme = this.store.getScheme();
        if (scheme == null) {
            errors.addErrorMessage(this.i18n.getText("notifications.plugin.scheme.not.found"), ErrorCollection.Reason.NOT_FOUND);
            return Either.left((Object)errors);
        }
        for (NotificationRepresentation notificationRepresentation : scheme.getNotifications()) {
            if (notificationRepresentation.getId() != notificationId) continue;
            return Either.right((Object)notificationRepresentation);
        }
        errors.addErrorMessage(this.i18n.getText("notifications.plugin.scheme.notification.not.found", new Serializable[]{Integer.valueOf(notificationId)}), ErrorCollection.Reason.NOT_FOUND);
        return Either.left((Object)errors);
    }

    @Override
    public NotificationRepresentation addNotification(String loggedInUser, NotificationRepresentation input) {
        return this.store.addNotification(input);
    }

    @Override
    public Either<ErrorCollection, NotificationRepresentation> validateAddNotification(String loggedInUser, NotificationRepresentation notification) {
        ErrorCollection errors = this.validateNotification(loggedInUser, notification);
        if (errors.hasAnyErrors()) {
            return Either.left((Object)errors);
        }
        return Either.right((Object)notification);
    }

    @Override
    public Either<ErrorCollection, NotificationRepresentation> validateUpdateNotification(String loggedInUser, int notificationId, NotificationRepresentation notification) {
        ErrorCollection errors = this.validateNotification(loggedInUser, notification);
        NotificationSchemeRepresentation theScheme = this.store.getScheme();
        boolean found = false;
        for (NotificationRepresentation notificationRepresentation : theScheme.getNotifications()) {
            if (notificationRepresentation.getId() != notificationId) continue;
            found = true;
            break;
        }
        if (!found) {
            errors.addErrorMessage(this.i18n.getText("notifications.plugin.scheme.notification.not.found", new Serializable[]{Integer.valueOf(notificationId)}), ErrorCollection.Reason.NOT_FOUND);
        }
        if (errors.hasAnyErrors()) {
            return Either.left((Object)errors);
        }
        return Either.right((Object)notification);
    }

    private ErrorCollection validateNotification(String loggedInUser, NotificationRepresentation notification) {
        ErrorCollection errors = new ErrorCollection();
        if (!this.userManager.isSystemAdmin(loggedInUser)) {
            errors.addErrorMessage(this.i18n.getText("perm.violation.desc"), ErrorCollection.Reason.FORBIDDEN);
            return errors;
        }
        NotificationSchemeRepresentation theScheme = this.store.getScheme();
        if (theScheme == null) {
            errors.addErrorMessage(this.i18n.getText("notifications.plugin.scheme.not.found"), ErrorCollection.Reason.NOT_FOUND);
            return errors;
        }
        if (notification.getEvents().isEmpty() || notification.getRecipients().isEmpty()) {
            errors.addErrorMessage(this.i18n.getText("notifications.plugin.notification.no.events"), ErrorCollection.Reason.VALIDATION_FAILED);
            return errors;
        }
        for (RecipientRepresentation recipientRepresentation : notification.getRecipients()) {
            if (recipientRepresentation.isIndividual()) continue;
            int serverId = recipientRepresentation.getServerId();
            ServerConfiguration serverConfig = this.serverManager.getServer(serverId);
            if (serverConfig == null) {
                errors.addErrorMessage(this.i18n.getText("notifications.plugin.error.invalid.server", new Serializable[]{recipientRepresentation.getName(), Integer.valueOf(recipientRepresentation.getServerId())}), ErrorCollection.Reason.VALIDATION_FAILED);
                continue;
            }
            Server server = this.serverFactory.getServer(serverConfig);
            errors.addErrorCollection(server.validateGroup(this.i18n, recipientRepresentation.getParamValue()));
        }
        errors.addErrorCollection(this.filterProvider.validateFilter(this.i18n, notification.getFilterConfiguration().getParams()));
        return errors;
    }

    @Override
    public NotificationRepresentation updateNotification(String loggedInUser, int notificationId, NotificationRepresentation input) {
        return this.store.updateNotification(input);
    }

    @Override
    public ErrorCollection validateRemoveNotification(String loggedInUser, int notificationId) {
        ErrorCollection errors = new ErrorCollection();
        if (!this.userManager.isSystemAdmin(loggedInUser)) {
            errors.addErrorMessage(this.i18n.getText("perm.violation.desc"), ErrorCollection.Reason.FORBIDDEN);
            return errors;
        }
        NotificationSchemeRepresentation theScheme = this.store.getScheme();
        if (theScheme == null) {
            errors.addErrorMessage(this.i18n.getText("notifications.plugin.scheme.not.found"), ErrorCollection.Reason.NOT_FOUND);
            return errors;
        }
        boolean found = false;
        for (NotificationRepresentation notificationRepresentation : theScheme.getNotifications()) {
            if (notificationRepresentation.getId() != notificationId) continue;
            found = true;
            break;
        }
        if (!found) {
            errors.addErrorMessage(this.i18n.getText("notifications.plugin.scheme.notification.not.found", new Serializable[]{Integer.valueOf(notificationId)}), ErrorCollection.Reason.NOT_FOUND);
            return errors;
        }
        return errors;
    }

    @Override
    public void removeNotification(int notificationId) {
        this.store.removeNotification(notificationId);
    }

    @Override
    public Iterable<NotificationRepresentation> getNotificationsForEvent(Object event) {
        String eventKey = this.eventProvider.getEventKey(event);
        return this.store.getNotificationsForEvent(eventKey);
    }
}

