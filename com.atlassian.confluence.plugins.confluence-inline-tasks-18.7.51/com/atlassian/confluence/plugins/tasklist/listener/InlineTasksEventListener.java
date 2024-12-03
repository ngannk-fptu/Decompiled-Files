/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.space.SpaceContentWillRemoveEvent
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.User
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.listener;

import com.atlassian.confluence.event.events.space.SpaceContentWillRemoveEvent;
import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.event.ConfluenceTaskV2RemoveEvent;
import com.atlassian.confluence.plugins.tasklist.event.ConfluenceTaskV2UpdateEvent;
import com.atlassian.confluence.plugins.tasklist.service.InlineTaskService;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.User;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InlineTasksEventListener {
    private final EventPublisher eventPublisher;
    private final InlineTaskService inlineTaskService;

    @Autowired
    public InlineTasksEventListener(EventPublisher eventPublisher, InlineTaskService inlineTaskService) {
        this.eventPublisher = eventPublisher;
        this.inlineTaskService = inlineTaskService;
    }

    @PostConstruct
    public final void setup() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public final void teardown() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onTaskRemovedEvent(ConfluenceTaskV2RemoveEvent event) {
        this.inlineTaskService.delete(event.getTask());
    }

    @EventListener
    public void onTaskUpdatedEvent(ConfluenceTaskV2UpdateEvent event) {
        if (!(event.hasStatusChanged() || event.hasBodyChanged() || event.hasAssigneeChanged())) {
            return;
        }
        Task task = event.getTask();
        User performer = event.getOriginatingUser();
        this.inlineTaskService.update(task, performer == null ? null : performer.getName(), event.hasStatusChanged(), event.hasBodyChanged());
    }

    @EventListener
    public void onSpaceDeletedEvent(SpaceContentWillRemoveEvent event) {
        Space space = event.getSpace();
        this.inlineTaskService.deleteBySpaceId(space.getId());
    }
}

