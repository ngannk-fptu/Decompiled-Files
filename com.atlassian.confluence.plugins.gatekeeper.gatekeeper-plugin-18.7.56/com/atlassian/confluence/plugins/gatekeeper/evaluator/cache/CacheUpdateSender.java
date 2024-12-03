/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.core.SynchronizationManager
 *  com.atlassian.confluence.event.events.admin.AsyncImportFinishedEvent
 *  com.atlassian.confluence.event.events.permission.GlobalPermissionRemoveEvent
 *  com.atlassian.confluence.event.events.permission.GlobalPermissionSaveEvent
 *  com.atlassian.confluence.event.events.permission.SpacePermissionRemoveEvent
 *  com.atlassian.confluence.event.events.permission.SpacePermissionSaveEvent
 *  com.atlassian.confluence.event.events.space.SpaceArchivedEvent
 *  com.atlassian.confluence.event.events.space.SpaceCreateEvent
 *  com.atlassian.confluence.event.events.space.SpaceRemoveEvent
 *  com.atlassian.confluence.event.events.space.SpaceUnArchivedEvent
 *  com.atlassian.confluence.event.events.space.SpaceUpdateEvent
 *  com.atlassian.confluence.event.events.user.UserDeactivateEvent
 *  com.atlassian.confluence.event.events.user.UserReactivateEvent
 *  com.atlassian.confluence.importexport.ImportContext
 *  com.atlassian.confluence.security.SpacePermission
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.event.application.ApplicationDirectoryOrderUpdatedEvent
 *  com.atlassian.crowd.event.application.ApplicationUpdatedEvent
 *  com.atlassian.crowd.event.directory.DirectoryUpdatedEvent
 *  com.atlassian.crowd.event.group.GroupCreatedEvent
 *  com.atlassian.crowd.event.group.GroupDeletedEvent
 *  com.atlassian.crowd.event.group.GroupMembershipDeletedEvent
 *  com.atlassian.crowd.event.group.GroupMembershipsCreatedEvent
 *  com.atlassian.crowd.event.group.GroupUpdatedEvent
 *  com.atlassian.crowd.event.user.UserCreatedEvent
 *  com.atlassian.crowd.event.user.UserDeletedEvent
 *  com.atlassian.crowd.event.user.UserRenamedEvent
 *  com.atlassian.crowd.event.user.UserUpdatedEvent
 *  com.atlassian.crowd.model.membership.MembershipType
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.gatekeeper.evaluator.cache;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.core.SynchronizationManager;
import com.atlassian.confluence.event.events.admin.AsyncImportFinishedEvent;
import com.atlassian.confluence.event.events.permission.GlobalPermissionRemoveEvent;
import com.atlassian.confluence.event.events.permission.GlobalPermissionSaveEvent;
import com.atlassian.confluence.event.events.permission.SpacePermissionRemoveEvent;
import com.atlassian.confluence.event.events.permission.SpacePermissionSaveEvent;
import com.atlassian.confluence.event.events.space.SpaceArchivedEvent;
import com.atlassian.confluence.event.events.space.SpaceCreateEvent;
import com.atlassian.confluence.event.events.space.SpaceRemoveEvent;
import com.atlassian.confluence.event.events.space.SpaceUnArchivedEvent;
import com.atlassian.confluence.event.events.space.SpaceUpdateEvent;
import com.atlassian.confluence.event.events.user.UserDeactivateEvent;
import com.atlassian.confluence.event.events.user.UserReactivateEvent;
import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.DistributedQueue;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyApplicationEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyGlobalPermissionEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyGroupEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyMembershipEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinySpaceEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinySpacePermissionEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyUserDirectoryEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyUserEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.space.TinySpace;
import com.atlassian.confluence.plugins.gatekeeper.service.ConfluenceService;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.event.application.ApplicationDirectoryOrderUpdatedEvent;
import com.atlassian.crowd.event.application.ApplicationUpdatedEvent;
import com.atlassian.crowd.event.directory.DirectoryUpdatedEvent;
import com.atlassian.crowd.event.group.GroupCreatedEvent;
import com.atlassian.crowd.event.group.GroupDeletedEvent;
import com.atlassian.crowd.event.group.GroupMembershipDeletedEvent;
import com.atlassian.crowd.event.group.GroupMembershipsCreatedEvent;
import com.atlassian.crowd.event.group.GroupUpdatedEvent;
import com.atlassian.crowd.event.user.UserCreatedEvent;
import com.atlassian.crowd.event.user.UserDeletedEvent;
import com.atlassian.crowd.event.user.UserRenamedEvent;
import com.atlassian.crowd.event.user.UserUpdatedEvent;
import com.atlassian.crowd.model.membership.MembershipType;
import com.atlassian.crowd.model.user.User;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CacheUpdateSender
implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(CacheUpdateSender.class);
    private final EventPublisher eventPublisher;
    private final DistributedQueue<TinyEvent> distributedTinyEventQueue;
    private final LinkedBlockingQueue<TinyEvent> localEventQueue;
    private final ConfluenceService confluenceService;
    private final SynchronizationManager synchronizationManager;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public CacheUpdateSender(EventPublisher eventPublisher, ConfluenceService confluenceService, DistributedQueue<TinyEvent> distributedQueue, @ComponentImport SynchronizationManager synchronizationManager) {
        this.eventPublisher = eventPublisher;
        this.distributedTinyEventQueue = distributedQueue;
        this.confluenceService = confluenceService;
        this.localEventQueue = new LinkedBlockingQueue();
        this.synchronizationManager = synchronizationManager;
    }

    @PostConstruct
    public void start() {
        this.eventPublisher.register((Object)this);
        Thread thread = new Thread((Runnable)this, "perm-delta-cache-sender");
        thread.start();
    }

    @PreDestroy
    public void stop() {
        try {
            this.running.set(false);
            this.eventPublisher.unregister((Object)this);
            this.localEventQueue.put(TinyEvent.POISON_PILL);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @EventListener
    public void spaceAddedEvent(SpaceCreateEvent event) {
        Space space = event.getSpace();
        this.addEventToClusteredCache(TinySpaceEvent.added(space.getKey(), space.getName()));
        this.addEventToClusteredCache(TinySpacePermissionEvent.added(space.getPermissions()));
    }

    @EventListener
    public void spaceAddedEvent(AsyncImportFinishedEvent event) {
        String spaceKey;
        ImportContext context = event.getImportContext();
        if (context != null && (spaceKey = context.getSpaceKeyOfSpaceImport()) != null) {
            TinySpace space = this.confluenceService.getSpace(spaceKey);
            if (space != null) {
                this.addEventToClusteredCache(TinySpaceEvent.added(spaceKey, space.getName()));
                this.addEventToClusteredCache(TinySpacePermissionEvent.added(this.confluenceService.getPermissions(spaceKey)));
            } else {
                logger.warn("Space import for spaceKey " + spaceKey + " did not import correctly. Unable to retrieve the space for permission cache update.");
            }
        }
    }

    @EventListener
    public void spaceUpdatedEvent(SpaceUpdateEvent event) {
        Space space = event.getSpace();
        this.addEventToClusteredCache(TinySpaceEvent.update(space.getKey(), space.getName()));
    }

    @EventListener
    public void spaceArchivedEvent(SpaceArchivedEvent event) {
        this.addEventToClusteredCache(TinySpaceEvent.archived(event.getSpace().getKey()));
    }

    @EventListener
    public void spaceUnarchivedEvent(SpaceUnArchivedEvent event) {
        this.addEventToClusteredCache(TinySpaceEvent.unarchived(event.getSpace().getKey()));
    }

    @EventListener
    public void spaceDeletedEvent(SpaceRemoveEvent event) {
        this.addEventToClusteredCache(TinySpaceEvent.delete(event.getSpace().getKey()));
    }

    @EventListener
    public void userAddedEvent(UserCreatedEvent event) {
        User user = event.getUser();
        this.addEventToClusteredCache(TinyUserEvent.added(user.getName(), user.getDisplayName(), user.isActive()));
    }

    @EventListener
    public void userUpdatedEvent(UserUpdatedEvent event) {
        User user = event.getUser();
        if (event instanceof UserRenamedEvent) {
            if (event.getDirectoryType() == DirectoryType.INTERNAL) {
                this.addEventToClusteredCache(TinyUserEvent.renamed(((UserRenamedEvent)event).getOldUsername(), user.getName()));
            } else {
                this.addEventToClusteredCache(TinyUserDirectoryEvent.updated(event.getDirectory()));
            }
        } else {
            this.addEventToClusteredCache(TinyUserEvent.updated(user.getName(), user.getDisplayName()));
        }
    }

    @EventListener
    public void userDeletedEvent(UserDeletedEvent event) {
        this.addEventToClusteredCache(TinyUserEvent.deleted(event.getUsername()));
    }

    @EventListener
    public void userActivateEvent(UserReactivateEvent event) {
        this.addEventToClusteredCache(TinyUserEvent.activated(event.getUser().getName()));
    }

    @EventListener
    public void userDeactivateEvent(UserDeactivateEvent event) {
        this.addEventToClusteredCache(TinyUserEvent.deactivated(event.getUser().getName()));
    }

    @EventListener
    public void groupAddedEvent(GroupCreatedEvent event) {
        this.addEventToClusteredCache(TinyGroupEvent.added(event.getGroup().getName()));
    }

    @EventListener
    public void groupDeletedEvent(GroupDeletedEvent event) {
        this.addEventToClusteredCache(TinyGroupEvent.deleted(event.getGroupName()));
    }

    @EventListener
    public void groupUpdatedEvent(GroupUpdatedEvent event) {
        this.addEventToClusteredCache(TinyGroupEvent.updated());
    }

    @EventListener
    public void membershipsCreatedEvent(GroupMembershipsCreatedEvent event) {
        if (event.getMembershipType() == MembershipType.GROUP_USER) {
            this.addEventToClusteredCache(TinyMembershipEvent.addedUsers(event.getGroupName()));
        } else if (event.getMembershipType() == MembershipType.GROUP_GROUP) {
            this.addEventToClusteredCache(TinyMembershipEvent.addedGroups(event.getGroupName()));
        } else {
            logger.error("Unexpected membership created type: {}", (Object)event.getMembershipType());
        }
    }

    @EventListener
    public void membershipsDeletedEvent(GroupMembershipDeletedEvent event) {
        if (event.getMembershipType() == MembershipType.GROUP_USER) {
            this.addEventToClusteredCache(TinyMembershipEvent.deletedUsers(event.getGroupName()));
        } else if (event.getMembershipType() == MembershipType.GROUP_GROUP) {
            this.addEventToClusteredCache(TinyMembershipEvent.deletedGroups(event.getGroupName()));
        } else {
            logger.error("Unexpected membership deleted type: {}", (Object)event.getMembershipType());
        }
    }

    @EventListener
    public void spacePermissionAdded(SpacePermissionSaveEvent event) {
        this.addEventToClusteredCache(TinySpacePermissionEvent.added(event.getPermissions()));
    }

    @EventListener
    public void spacePermissionRemoved(SpacePermissionRemoveEvent event) {
        this.addEventToClusteredCache(TinySpacePermissionEvent.deleted(event.getSpace().getKey(), event.getPermissions()));
    }

    @EventListener
    public void userDirectoryOrderChanged(ApplicationDirectoryOrderUpdatedEvent event) {
        this.addEventToClusteredCache(TinyUserDirectoryEvent.updated(event.getDirectory()));
    }

    @EventListener
    public void userDirectoryUpdated(DirectoryUpdatedEvent event) {
        this.addEventToClusteredCache(TinyUserDirectoryEvent.updated(event.getDirectory()));
    }

    @EventListener
    public void globalPermissionAdded(GlobalPermissionSaveEvent event) {
        SpacePermission sp = event.getPermission();
        if ("USECONFLUENCE".equals(sp.getType())) {
            this.addEventToClusteredCache(TinyGlobalPermissionEvent.added(sp));
        }
    }

    @EventListener
    public void globalPermissionRemoved(GlobalPermissionRemoveEvent event) {
        SpacePermission sp = event.getPermission();
        if ("USECONFLUENCE".equals(sp.getType())) {
            this.addEventToClusteredCache(TinyGlobalPermissionEvent.deleted(sp));
        }
    }

    @EventListener
    public void applicationUpdatedEvent(ApplicationUpdatedEvent event) {
        this.addEventToClusteredCache(TinyApplicationEvent.updated(event.getApplication()));
    }

    @Override
    public void run() {
        this.running.set(true);
        logger.debug("Event sender thread started");
        while (this.running.get()) {
            try {
                TinyEvent event = this.localEventQueue.take();
                if (TinyEvent.POISON_PILL.equals(event)) {
                    logger.debug("Found poison pill. Exiting thread.");
                    return;
                }
                this.distributedTinyEventQueue.sender().accept(event);
                logger.debug("Sent {}: [{}]", (Object)event);
            }
            catch (InterruptedException e) {
                this.running.set(false);
                logger.debug("Event sender thread interrupted", (Throwable)e);
            }
            catch (Exception e) {
                logger.error("Event sender failed! WARNING: Permission cache is inconsistent : {}", (Object)e.getMessage());
                logger.debug("", (Throwable)e);
            }
        }
    }

    private void addEventToClusteredCache(TinyEvent event) {
        logger.trace("Adding {} event for processing before synchronizationManager", (Object)event.getEventType());
        this.synchronizationManager.runOnSuccessfulCommit(() -> {
            if (!this.localEventQueue.offer(event)) {
                logger.error("Unable to add event [{}] to the eventQueue.", (Object)event);
            }
        });
        logger.trace("Adding {} event for processing after synchronizationManager", (Object)event.getEventType());
    }

    @VisibleForTesting
    List<TinyEvent> getLocalEventQueueAsList() {
        return new ArrayList<TinyEvent>(this.localEventQueue);
    }
}

