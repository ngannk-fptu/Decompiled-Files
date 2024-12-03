/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.attribute.AttributePredicates
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.event.EventStore
 *  com.atlassian.crowd.event.application.ApplicationUpdatedEvent
 *  com.atlassian.crowd.event.directory.DirectoryDeletedEvent
 *  com.atlassian.crowd.event.directory.DirectoryUpdatedEvent
 *  com.atlassian.crowd.event.group.GroupAttributeDeletedEvent
 *  com.atlassian.crowd.event.group.GroupAttributeStoredEvent
 *  com.atlassian.crowd.event.group.GroupCreatedEvent
 *  com.atlassian.crowd.event.group.GroupDeletedEvent
 *  com.atlassian.crowd.event.group.GroupMembershipsCreatedEvent
 *  com.atlassian.crowd.event.group.GroupMembershipsDeletedEvent
 *  com.atlassian.crowd.event.group.GroupUpdatedEvent
 *  com.atlassian.crowd.event.migration.XMLRestoreFinishedEvent
 *  com.atlassian.crowd.event.user.UserAttributeDeletedEvent
 *  com.atlassian.crowd.event.user.UserAttributeStoredEvent
 *  com.atlassian.crowd.event.user.UserCreatedEvent
 *  com.atlassian.crowd.event.user.UserRenamedEvent
 *  com.atlassian.crowd.event.user.UserUpdatedEvent
 *  com.atlassian.crowd.event.user.UsersDeletedEvent
 *  com.atlassian.crowd.manager.webhook.WebhookService
 *  com.atlassian.crowd.model.event.AliasEvent
 *  com.atlassian.crowd.model.event.GroupEvent
 *  com.atlassian.crowd.model.event.GroupMembershipEvent
 *  com.atlassian.crowd.model.event.Operation
 *  com.atlassian.crowd.model.event.OperationEvent
 *  com.atlassian.crowd.model.event.UserEvent
 *  com.atlassian.crowd.model.event.UserMembershipEvent
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.ImmutableGroup
 *  com.atlassian.crowd.model.user.ImmutableUser
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.crowd.event;

import com.atlassian.crowd.attribute.AttributePredicates;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.EventStore;
import com.atlassian.crowd.event.application.ApplicationUpdatedEvent;
import com.atlassian.crowd.event.directory.DirectoryDeletedEvent;
import com.atlassian.crowd.event.directory.DirectoryUpdatedEvent;
import com.atlassian.crowd.event.group.GroupAttributeDeletedEvent;
import com.atlassian.crowd.event.group.GroupAttributeStoredEvent;
import com.atlassian.crowd.event.group.GroupCreatedEvent;
import com.atlassian.crowd.event.group.GroupDeletedEvent;
import com.atlassian.crowd.event.group.GroupMembershipsCreatedEvent;
import com.atlassian.crowd.event.group.GroupMembershipsDeletedEvent;
import com.atlassian.crowd.event.group.GroupUpdatedEvent;
import com.atlassian.crowd.event.migration.XMLRestoreFinishedEvent;
import com.atlassian.crowd.event.user.UserAttributeDeletedEvent;
import com.atlassian.crowd.event.user.UserAttributeStoredEvent;
import com.atlassian.crowd.event.user.UserCreatedEvent;
import com.atlassian.crowd.event.user.UserRenamedEvent;
import com.atlassian.crowd.event.user.UserUpdatedEvent;
import com.atlassian.crowd.event.user.UsersDeletedEvent;
import com.atlassian.crowd.manager.webhook.WebhookService;
import com.atlassian.crowd.model.event.AliasEvent;
import com.atlassian.crowd.model.event.GroupEvent;
import com.atlassian.crowd.model.event.GroupMembershipEvent;
import com.atlassian.crowd.model.event.Operation;
import com.atlassian.crowd.model.event.OperationEvent;
import com.atlassian.crowd.model.event.UserEvent;
import com.atlassian.crowd.model.event.UserMembershipEvent;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.ImmutableGroup;
import com.atlassian.crowd.model.user.ImmutableUser;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StoringEventListener {
    private final EventStore eventStore;
    private final WebhookService webhookService;

    public StoringEventListener(EventStore eventStore, EventPublisher eventPublisher, WebhookService webhookService) {
        this.eventStore = eventStore;
        this.webhookService = webhookService;
        eventPublisher.register((Object)this);
    }

    private void storeEventAndNotifyWebhooks(OperationEvent event) {
        this.eventStore.storeOperationEvent(event);
        this.webhookService.notifyWebhooks();
    }

    private void storeEventsAndNotifyWebhooks(Stream<OperationEvent> events) {
        events.forEach(event -> this.eventStore.storeOperationEvent(event));
        this.webhookService.notifyWebhooks();
    }

    private void invalidateEventsAndNotifyWebhooks(Object event) {
        this.eventStore.handleApplicationEvent(event);
        this.webhookService.notifyWebhooks();
    }

    @EventListener
    public void handleEvent(UserCreatedEvent event) {
        this.storeEventAndNotifyWebhooks((OperationEvent)new UserEvent(Operation.CREATED, event.getDirectoryId(), event.getUser(), null, null));
    }

    @EventListener
    public void handleEvent(UserUpdatedEvent event) {
        Map<String, Set> storedAttributes = null;
        Set<String> deletedAttributes = null;
        if (event instanceof UserAttributeStoredEvent) {
            UserAttributeStoredEvent userAttributeStoredEvent = (UserAttributeStoredEvent)event;
            storedAttributes = userAttributeStoredEvent.getAttributeNames().stream().filter(AttributePredicates.SYNCING_ATTRIBUTE).collect(Collectors.toMap(attrName -> attrName, arg_0 -> ((UserAttributeStoredEvent)userAttributeStoredEvent).getAttributeValues(arg_0)));
            if (storedAttributes.isEmpty()) {
                return;
            }
        } else if (event instanceof UserAttributeDeletedEvent) {
            String attributeName = ((UserAttributeDeletedEvent)event).getAttributeName();
            if (!AttributePredicates.SYNCING_ATTRIBUTE.test(attributeName)) {
                return;
            }
            deletedAttributes = Collections.singleton(attributeName);
        } else if (event instanceof UserRenamedEvent) {
            this.invalidateEventsAndNotifyWebhooks(event);
            return;
        }
        this.storeEventAndNotifyWebhooks((OperationEvent)new UserEvent(Operation.UPDATED, event.getDirectoryId(), event.getUser(), storedAttributes, deletedAttributes));
    }

    @EventListener
    public void handleEvent(UsersDeletedEvent event) {
        Directory directory = event.getDirectory();
        Long directoryId = directory.getId();
        this.storeEventsAndNotifyWebhooks(event.getUsernames().stream().map(username -> new UserEvent(Operation.DELETED, directoryId, (User)new ImmutableUser((User)new UserTemplate(username, directoryId.longValue())), null, null)));
    }

    @EventListener
    public void handleEvent(GroupCreatedEvent event) {
        this.storeEventAndNotifyWebhooks((OperationEvent)new GroupEvent(Operation.CREATED, event.getDirectory().getId(), event.getGroup(), null, null));
    }

    @EventListener
    public void handleEvent(GroupUpdatedEvent event) {
        Map<String, Set> storedAttributes = null;
        Set<String> deletedAttributes = null;
        if (event instanceof GroupAttributeStoredEvent) {
            GroupAttributeStoredEvent groupAttributeStoredEvent = (GroupAttributeStoredEvent)event;
            storedAttributes = groupAttributeStoredEvent.getAttributeNames().stream().filter(AttributePredicates.SYNCING_ATTRIBUTE).collect(Collectors.toMap(attrName -> attrName, arg_0 -> ((GroupAttributeStoredEvent)groupAttributeStoredEvent).getAttributeValues(arg_0)));
            if (storedAttributes.isEmpty()) {
                return;
            }
        } else if (event instanceof GroupAttributeDeletedEvent) {
            String attributeName = ((GroupAttributeDeletedEvent)event).getAttributeName();
            if (!AttributePredicates.SYNCING_ATTRIBUTE.test(attributeName)) {
                return;
            }
            deletedAttributes = Collections.singleton(attributeName);
        }
        this.storeEventAndNotifyWebhooks((OperationEvent)new GroupEvent(Operation.UPDATED, event.getDirectory().getId(), event.getGroup(), storedAttributes, deletedAttributes));
    }

    @EventListener
    public void handleEvent(GroupDeletedEvent event) {
        ImmutableGroup group = ImmutableGroup.builder((long)event.getDirectoryId(), (String)event.getGroupName()).build();
        this.storeEventAndNotifyWebhooks((OperationEvent)new GroupEvent(Operation.DELETED, event.getDirectory().getId(), (Group)group, null, null));
    }

    @EventListener
    public void handleEvent(GroupMembershipsCreatedEvent event) {
        Stream<OperationEvent> eventStream;
        switch (event.getMembershipType()) {
            case GROUP_USER: {
                eventStream = event.getEntityNames().stream().map(entityName -> new UserMembershipEvent(Operation.CREATED, event.getDirectory().getId(), entityName, event.getGroupName()));
                break;
            }
            case GROUP_GROUP: {
                eventStream = event.getEntityNames().stream().map(entityName -> new GroupMembershipEvent(Operation.CREATED, event.getDirectory().getId(), entityName, event.getGroupName()));
                break;
            }
            default: {
                throw new IllegalArgumentException("MembershipType " + event.getMembershipType() + " is not supported");
            }
        }
        this.storeEventsAndNotifyWebhooks(eventStream);
    }

    @EventListener
    public void handleEvent(GroupMembershipsDeletedEvent event) {
        Stream<OperationEvent> eventStream;
        switch (event.getMembershipType()) {
            case GROUP_USER: {
                eventStream = event.getEntityNames().stream().map(entityName -> new UserMembershipEvent(Operation.DELETED, event.getDirectory().getId(), entityName, event.getGroupName()));
                break;
            }
            case GROUP_GROUP: {
                eventStream = event.getEntityNames().stream().map(entityName -> new GroupMembershipEvent(Operation.DELETED, event.getDirectory().getId(), entityName, event.getGroupName()));
                break;
            }
            default: {
                throw new IllegalArgumentException("MembershipType " + event.getMembershipType() + " is not supported");
            }
        }
        this.storeEventsAndNotifyWebhooks(eventStream);
    }

    @EventListener
    public void handleEvent(AliasEvent event) {
        this.eventStore.storeOperationEvent((OperationEvent)event);
    }

    @EventListener
    public void handleEvent(DirectoryUpdatedEvent event) {
        this.invalidateEventsAndNotifyWebhooks(event);
    }

    @EventListener
    public void handleEvent(DirectoryDeletedEvent event) {
        this.invalidateEventsAndNotifyWebhooks(event);
    }

    @EventListener
    public void handleEvent(XMLRestoreFinishedEvent event) {
        this.invalidateEventsAndNotifyWebhooks(event);
    }

    @EventListener
    public void handleEvent(ApplicationUpdatedEvent event) {
        this.invalidateEventsAndNotifyWebhooks(event);
    }
}

