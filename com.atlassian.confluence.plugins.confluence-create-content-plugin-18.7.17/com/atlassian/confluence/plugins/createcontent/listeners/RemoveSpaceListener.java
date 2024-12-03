/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.event.events.space.SpaceRemoveEvent
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.listeners;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.event.events.space.SpaceRemoveEvent;
import com.atlassian.confluence.plugins.createcontent.SpaceBandanaContext;
import com.atlassian.confluence.plugins.createcontent.api.services.ContentBlueprintService;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RemoveSpaceListener {
    private final BandanaManager bandanaManager;
    private final EventPublisher eventPublisher;
    private final ContentBlueprintService contentBlueprintService;

    @Autowired
    public RemoveSpaceListener(@ComponentImport BandanaManager bandanaManager, @ComponentImport EventPublisher eventPublisher, ContentBlueprintService contentBlueprintService) {
        this.bandanaManager = bandanaManager;
        this.eventPublisher = eventPublisher;
        this.contentBlueprintService = contentBlueprintService;
    }

    @EventListener
    public void onSpaceRemoveEvent(SpaceRemoveEvent event) {
        Space deletedSpace = event.getSpace();
        SpaceBandanaContext spaceBandanaContext = new SpaceBandanaContext(deletedSpace);
        for (String key : this.bandanaManager.getKeys((BandanaContext)spaceBandanaContext)) {
            this.bandanaManager.removeValue((BandanaContext)spaceBandanaContext, key);
        }
        this.contentBlueprintService.deleteContentBlueprintsForSpace(deletedSpace.getKey());
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

