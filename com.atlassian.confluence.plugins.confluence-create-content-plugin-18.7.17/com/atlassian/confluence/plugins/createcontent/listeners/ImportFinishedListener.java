/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.admin.ImportFinishedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.listeners;

import com.atlassian.confluence.event.events.admin.ImportFinishedEvent;
import com.atlassian.confluence.plugins.createcontent.ContentBlueprintCleaner;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImportFinishedListener {
    private final EventPublisher eventPublisher;
    private final ContentBlueprintCleaner contentBlueprintCleaner;

    @Autowired
    public ImportFinishedListener(@ComponentImport EventPublisher eventPublisher, ContentBlueprintCleaner contentBlueprintCleaner) {
        this.eventPublisher = eventPublisher;
        this.contentBlueprintCleaner = contentBlueprintCleaner;
    }

    @EventListener
    public void onImportFinishedEvent(ImportFinishedEvent event) {
        if (event.isSiteImport() && event.isOriginalEvent()) {
            this.contentBlueprintCleaner.cleanUp();
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

