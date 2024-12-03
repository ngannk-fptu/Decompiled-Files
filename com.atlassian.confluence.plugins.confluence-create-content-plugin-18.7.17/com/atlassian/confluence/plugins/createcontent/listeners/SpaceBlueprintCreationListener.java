/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.SpaceLabelManager
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.listeners;

import com.atlassian.confluence.labels.SpaceLabelManager;
import com.atlassian.confluence.plugins.createcontent.events.SpaceBlueprintCreateEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpaceBlueprintCreationListener {
    private final SpaceLabelManager spaceLabelManager;
    private final EventPublisher eventPublisher;

    @Autowired
    public SpaceBlueprintCreationListener(@ComponentImport SpaceLabelManager spaceLabelManager, @ComponentImport EventPublisher eventPublisher) {
        this.spaceLabelManager = spaceLabelManager;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void onSpaceBlueprintCreateEvent(SpaceBlueprintCreateEvent event) {
        if (event.getSpaceBlueprint().getCategory() != null) {
            this.spaceLabelManager.addLabel(event.getSpace(), event.getSpaceBlueprint().getCategory());
        }
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @PostConstruct
    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }
}

