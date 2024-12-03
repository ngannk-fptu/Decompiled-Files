/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.activeobjects.tx.Transactional
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.plugin.notifications.config.scheme;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.notifications.api.event.EventRepresentation;
import com.atlassian.plugin.notifications.api.event.ServerConfigurationEvent;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.ServerManager;
import com.atlassian.plugin.notifications.api.medium.recipient.RecipientRepresentation;
import com.atlassian.plugin.notifications.api.notification.FilterConfiguration;
import com.atlassian.plugin.notifications.api.notification.NotificationRepresentation;
import com.atlassian.plugin.notifications.api.notification.NotificationSchemeRepresentation;
import com.atlassian.plugin.notifications.api.notification.NotificationSchemeStore;
import com.atlassian.plugin.notifications.config.ao.scheme.Event;
import com.atlassian.plugin.notifications.config.ao.scheme.FilterParam;
import com.atlassian.plugin.notifications.config.ao.scheme.Notification;
import com.atlassian.plugin.notifications.config.ao.scheme.NotificationScheme;
import com.atlassian.plugin.notifications.config.ao.scheme.Recipient;
import com.atlassian.plugin.notifications.spi.NotificationEventProvider;
import com.atlassian.plugin.notifications.spi.NotificationFilterProvider;
import com.atlassian.plugin.notifications.spi.NotificationRecipientProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.java.ao.DBParam;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;

@Transactional
public class NotificationSchemeStoreImpl
implements NotificationSchemeStore,
InitializingBean,
DisposableBean {
    private final ActiveObjects ao;
    private final ServerManager serverManager;
    private final I18nResolver i18n;
    private final EventPublisher eventPublisher;
    private final NotificationEventProvider eventProvider;
    private final NotificationFilterProvider filterProvider;
    private final NotificationRecipientProvider recipientProvider;

    public NotificationSchemeStoreImpl(ActiveObjects ao, ServerManager serverManager, @Qualifier(value="i18nResolver") I18nResolver i18n, EventPublisher eventPublisher, NotificationEventProvider eventProvider, NotificationFilterProvider filterProvider, NotificationRecipientProvider recipientProvider) {
        this.ao = ao;
        this.serverManager = serverManager;
        this.i18n = i18n;
        this.eventPublisher = eventPublisher;
        this.eventProvider = eventProvider;
        this.filterProvider = filterProvider;
        this.recipientProvider = recipientProvider;
    }

    @Override
    public Iterable<NotificationRepresentation> getNotificationsForEvent(String eventKey) {
        Event[] events = (Event[])this.ao.find(Event.class, Query.select().where("EVENT_KEY = ? OR EVENT_KEY = ?", new Object[]{eventKey, "all_events"}));
        ArrayList notifications = Lists.newArrayList();
        for (Event event : events) {
            notifications.add(event.getNotification());
        }
        return this.transformNotifications(notifications.toArray(new Notification[notifications.size()]));
    }

    @Override
    public NotificationSchemeRepresentation getScheme() {
        NotificationScheme scheme = this.getDefaultScheme();
        if (scheme != null) {
            Iterable<NotificationRepresentation> notifications = this.transformNotifications(scheme.getNotifications());
            return new NotificationSchemeRepresentation(scheme.getID(), scheme.getName(), scheme.getDescription(), notifications);
        }
        return null;
    }

    @Override
    public NotificationRepresentation addNotification(NotificationRepresentation notification) {
        NotificationScheme notificationScheme = this.getDefaultScheme();
        Notification newNotification = (Notification)this.ao.create(Notification.class, new DBParam[]{new DBParam("NOTIFICATION_SCHEME_ID", (Object)notificationScheme.getID())});
        for (EventRepresentation eventRepresentation : notification.getEvents()) {
            this.ao.create(Event.class, this.createEventParams(newNotification.getID(), eventRepresentation));
        }
        for (RecipientRepresentation recipientRepresentation : notification.getRecipients()) {
            this.ao.create(Recipient.class, this.createRecipientParams(newNotification.getID(), recipientRepresentation));
        }
        this.createFilterParams(notification, newNotification);
        return (NotificationRepresentation)Iterables.getFirst(this.transformNotifications(newNotification), null);
    }

    @Override
    public NotificationRepresentation updateNotification(NotificationRepresentation notification) {
        Notification notificationEntity = (Notification)this.ao.get(Notification.class, (Object)notification.getId());
        ArrayList newEventIds = Lists.newArrayList();
        for (EventRepresentation eventRepresentation : notification.getEvents()) {
            Event theEvent = this.findEvent(eventRepresentation.getId(), notificationEntity.getEvents());
            if (theEvent != null) {
                theEvent.setEventKey(eventRepresentation.getEventKey());
                theEvent.save();
            } else {
                theEvent = (Event)this.ao.create(Event.class, this.createEventParams(notification.getId(), eventRepresentation));
            }
            newEventIds.add(theEvent.getID());
        }
        for (Event event : notificationEntity.getEvents()) {
            if (newEventIds.contains(event.getID())) continue;
            this.ao.delete(new RawEntity[]{event});
        }
        ArrayList newRecipientIds = Lists.newArrayList();
        for (RecipientRepresentation recipientRepresentation : notification.getRecipients()) {
            Recipient theRecipient = this.findRecipient(recipientRepresentation.getId(), notificationEntity.getRecipients());
            if (theRecipient != null) {
                theRecipient.setIndividual(recipientRepresentation.isIndividual());
                theRecipient.setParamDisplay(recipientRepresentation.getParamDisplay());
                theRecipient.setParamValue(recipientRepresentation.getParamValue());
                theRecipient.setServerId(recipientRepresentation.getServerId());
                theRecipient.setType(recipientRepresentation.getType());
                theRecipient.save();
            } else {
                theRecipient = (Recipient)this.ao.create(Recipient.class, this.createRecipientParams(notification.getId(), recipientRepresentation));
            }
            newRecipientIds.add(theRecipient.getID());
        }
        for (Recipient recipient : notificationEntity.getRecipients()) {
            if (newRecipientIds.contains(recipient.getID())) continue;
            this.ao.delete(new RawEntity[]{recipient});
        }
        for (FilterParam filterParam : notificationEntity.getFilterParams()) {
            this.ao.delete(new RawEntity[]{filterParam});
        }
        this.createFilterParams(notification, notificationEntity);
        return (NotificationRepresentation)Iterables.getFirst(this.transformNotifications((Notification)this.ao.get(Notification.class, (Object)notification.getId())), null);
    }

    private void createFilterParams(NotificationRepresentation notification, Notification notificationEntity) {
        for (Map.Entry<String, String> param : notification.getFilterConfiguration().getParams().entrySet()) {
            this.ao.create(FilterParam.class, new DBParam[]{new DBParam("PARAM_KEY", (Object)param.getKey()), new DBParam("PARAM_VALUE", (Object)param.getValue()), new DBParam("NOTIFICATION_ID", (Object)notificationEntity.getID())});
        }
    }

    @Override
    public void removeNotification(int notificationId) {
        Notification notification = (Notification)this.ao.get(Notification.class, (Object)notificationId);
        this.ao.delete((RawEntity[])notification.getFilterParams());
        this.ao.delete((RawEntity[])notification.getRecipients());
        this.ao.delete((RawEntity[])notification.getEvents());
        this.ao.delete(new RawEntity[]{notification});
    }

    private Event findEvent(int id, Event[] events) {
        for (Event event : events) {
            if (event.getID() != id) continue;
            return event;
        }
        return null;
    }

    private Recipient findRecipient(int id, Recipient[] recipients) {
        for (Recipient recipient : recipients) {
            if (recipient.getID() != id) continue;
            return recipient;
        }
        return null;
    }

    private DBParam[] createEventParams(int notificationId, EventRepresentation eventRepresentation) {
        return new DBParam[]{new DBParam("NOTIFICATION_ID", (Object)notificationId), new DBParam("EVENT_KEY", (Object)eventRepresentation.getEventKey())};
    }

    private DBParam[] createRecipientParams(int notificationId, RecipientRepresentation recipientRepresentation) {
        return new DBParam[]{new DBParam("INDIVIDUAL", (Object)recipientRepresentation.isIndividual()), new DBParam("TYPE", (Object)recipientRepresentation.getType()), new DBParam("SERVER_ID", (Object)recipientRepresentation.getServerId()), new DBParam("PARAM_VALUE", (Object)recipientRepresentation.getParamValue()), new DBParam("PARAM_DISPLAY", (Object)recipientRepresentation.getParamDisplay()), new DBParam("NOTIFICATION_ID", (Object)notificationId)};
    }

    private Iterable<NotificationRepresentation> transformNotifications(Notification ... notificationEntities) {
        NotificationTransformer transformer = new NotificationTransformer(this.serverManager, this.i18n, this.eventProvider, this.filterProvider, this.recipientProvider);
        LinkedHashSet notifications = Sets.newLinkedHashSet();
        for (Notification notification : notificationEntities) {
            notifications.add(transformer.apply(notification));
        }
        return notifications;
    }

    @EventListener
    public void onServerDeleted(ServerConfigurationEvent config) {
        if (config.getType().equals((Object)ServerConfigurationEvent.ConfigEventType.REMOVED)) {
            Recipient[] recipients;
            int serverId = config.getId();
            for (Recipient recipient : recipients = (Recipient[])this.ao.find(Recipient.class, Query.select().where("SERVER_ID = ?", new Object[]{serverId}))) {
                Notification notification = recipient.getNotification();
                if (notification.getRecipients().length == 1) {
                    this.removeNotification(notification.getID());
                    continue;
                }
                this.ao.delete(new RawEntity[]{recipient});
            }
        }
    }

    private NotificationScheme getDefaultScheme() {
        NotificationScheme[] notificationSchemes = (NotificationScheme[])this.ao.find(NotificationScheme.class);
        NotificationScheme defaultScheme = notificationSchemes.length == 0 ? (NotificationScheme)this.ao.create(NotificationScheme.class, new DBParam[]{new DBParam("SCHEME_NAME", (Object)"System created default scheme")}) : notificationSchemes[0];
        return defaultScheme;
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    private static class NotificationTransformer
    implements Function<Notification, NotificationRepresentation> {
        private final ServerManager serverManager;
        private final I18nResolver i18n;
        private final NotificationEventProvider eventProvider;
        private final NotificationFilterProvider filterProvider;
        private final NotificationRecipientProvider recipientProvider;

        private NotificationTransformer(ServerManager serverManager, I18nResolver i18n, NotificationEventProvider eventProvider, NotificationFilterProvider filterProvider, NotificationRecipientProvider recipientProvider) {
            this.serverManager = serverManager;
            this.i18n = i18n;
            this.eventProvider = eventProvider;
            this.filterProvider = filterProvider;
            this.recipientProvider = recipientProvider;
        }

        public NotificationRepresentation apply(@Nullable Notification input) {
            if (input == null) {
                return null;
            }
            ArrayList events = Lists.newArrayList();
            for (Event event : input.getEvents()) {
                if (event.getEventKey().equals("all_events")) {
                    events.add(EventRepresentation.allEventsRepresentation(this.i18n));
                    continue;
                }
                EventRepresentation eventRep = this.eventProvider.getEvent(event.getEventKey());
                if (eventRep == null) continue;
                events.add(new EventRepresentation(event.getID(), event.getEventKey(), eventRep.getName()));
            }
            ArrayList recipients = Lists.newArrayList();
            Iterable<RecipientRepresentation> allRecipients = this.recipientProvider.getAllRecipients();
            HashMap recipientMap = Maps.newHashMap();
            for (RecipientRepresentation recipient : allRecipients) {
                recipientMap.put(recipient.getType(), recipient);
            }
            for (Recipient recipient : input.getRecipients()) {
                String recipientName = recipient.getType();
                if (recipient.getType().equals("server_notification_type")) {
                    ServerConfiguration server = this.serverManager.getServer(recipient.getServerId());
                    if (server == null) continue;
                    recipientName = server.getFullName(this.i18n);
                } else {
                    RecipientRepresentation recipientRepresentation = (RecipientRepresentation)recipientMap.get(recipient.getType());
                    if (recipientRepresentation != null) {
                        recipientName = recipientRepresentation.getName();
                    }
                }
                recipients.add(new RecipientRepresentation(recipient.getID(), recipient.isIndividual(), recipient.getType(), recipientName, recipient.getServerId(), recipient.getParamValue(), recipient.getParamDisplay()));
            }
            ArrayList arrayList = Lists.newArrayList();
            for (FilterParam filterParam : input.getFilterParams()) {
                arrayList.add(new FilterConfiguration.FilterParam(filterParam.getParamKey(), filterParam.getParamValue()));
            }
            FilterConfiguration filterConfig = new FilterConfiguration(arrayList);
            return new NotificationRepresentation(input.getID(), filterConfig, this.filterProvider.getSummary(filterConfig), input.getNotificationScheme().getID(), recipients, events);
        }
    }
}

