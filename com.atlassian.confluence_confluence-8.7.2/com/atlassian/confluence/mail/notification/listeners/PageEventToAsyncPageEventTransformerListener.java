/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.mail.notification.listeners;

import com.atlassian.confluence.core.SynchronizationManager;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageEvent;
import com.atlassian.confluence.event.events.content.page.PageMoveEvent;
import com.atlassian.confluence.event.events.content.page.PageTrashedEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.event.events.content.page.async.PageCreatedEvent;
import com.atlassian.confluence.event.events.content.page.async.PageEditedEvent;
import com.atlassian.confluence.event.events.content.page.async.PageMovedEvent;
import com.atlassian.confluence.mail.notification.listeners.async.PageEventListener;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.user.UserKey;

@Deprecated
public class PageEventToAsyncPageEventTransformerListener {
    public static final String SYNC_EVENT_FEATURE_KEY = "sync.event";
    private final EventPublisher eventPublisher;
    private final SynchronizationManager synchronizationManager;
    private final PageEventListener pageEventListener;
    private final DarkFeaturesManager darkFeaturesManager;

    public PageEventToAsyncPageEventTransformerListener(EventPublisher eventPublisher, SynchronizationManager synchronizationManager, PageEventListener pageEventListener, DarkFeaturesManager darkFeaturesManager) {
        this.eventPublisher = eventPublisher;
        this.synchronizationManager = synchronizationManager;
        this.pageEventListener = pageEventListener;
        this.darkFeaturesManager = darkFeaturesManager;
    }

    @EventListener
    public void handleCreated(PageCreateEvent pageEvent) {
        PageCreatedEvent event = new PageCreatedEvent(this, this.getUserKey(pageEvent), pageEvent.getPage().getId(), pageEvent.getPage().getVersion(), pageEvent.isSuppressNotifications());
        this.publishOnSuccessfulCommit(event);
    }

    @EventListener
    public void handleUpdated(PageUpdateEvent pageEvent) {
        Page oldPage = (Page)pageEvent.getOld();
        Long originalPageId = oldPage != null ? Long.valueOf(oldPage.getId()) : null;
        Integer originalPageVersion = oldPage != null ? Integer.valueOf(oldPage.getVersion()) : null;
        PageEditedEvent event = new PageEditedEvent(this, this.getUserKey(pageEvent), pageEvent.getNew().getId(), ((Page)pageEvent.getNew()).getVersion(), originalPageId, originalPageVersion, pageEvent.isSuppressNotifications());
        this.publishOnSuccessfulCommit(event);
    }

    @EventListener
    public void handleTrashed(PageTrashedEvent pageEvent) {
        ConfluenceUser originator = (ConfluenceUser)pageEvent.getOriginatingUser();
        com.atlassian.confluence.event.events.content.page.async.PageTrashedEvent event = new com.atlassian.confluence.event.events.content.page.async.PageTrashedEvent(this, originator != null ? originator.getKey() : null, pageEvent.getPage().getId(), pageEvent.getPage().getVersion(), pageEvent.isSuppressNotifications());
        this.publishOnSuccessfulCommit(event);
    }

    @EventListener
    public void handleMoved(PageMoveEvent pageEvent) {
        ConfluenceUser user = (ConfluenceUser)pageEvent.getOriginatingUser();
        UserKey userKey = user != null ? user.getKey() : null;
        Page page = pageEvent.getPage();
        Long pageId = page.getId();
        Integer pageVersion = page.getVersion();
        Page originalParentPage = pageEvent.getOldParentPage();
        Page currentParentPage = page.getParent();
        Long originalParentPageId = originalParentPage != null ? Long.valueOf(originalParentPage.getId()) : null;
        Long currentParentPageId = currentParentPage != null ? Long.valueOf(currentParentPage.getId()) : null;
        Space originalSpace = pageEvent.getOldSpace();
        String originalSpaceKey = originalSpace.getKey();
        String currentSpaceKey = page.getSpaceKey();
        boolean movedBecauseOfParent = pageEvent.isMovedBecauseOfParent();
        boolean movedChildren = pageEvent.hasMovedChildren();
        PageMovedEvent event = new PageMovedEvent(this, userKey, pageId, pageVersion, originalSpaceKey, currentSpaceKey, originalParentPageId, currentParentPageId, movedBecauseOfParent, movedChildren, pageEvent.isSuppressNotifications());
        this.publishOnSuccessfulCommit(event);
    }

    private void publishOnSuccessfulCommit(com.atlassian.confluence.event.events.content.page.async.PageEvent event) {
        if (this.darkFeaturesManager.getDarkFeatures().isFeatureEnabled(SYNC_EVENT_FEATURE_KEY)) {
            this.pageEventListener.handleEvent(event);
        } else {
            this.synchronizationManager.runOnSuccessfulCommit(() -> this.eventPublisher.publish((Object)event));
        }
    }

    private UserKey getUserKey(PageEvent pageEvent) {
        ConfluenceUser modifier = pageEvent.getPage().getLastModifier();
        return modifier != null ? modifier.getKey() : null;
    }
}

