/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.admin.GlobalSettingsChangedEvent
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.searchui.filter;

import com.atlassian.confluence.event.events.admin.GlobalSettingsChangedEvent;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.concurrent.Semaphore;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class SearchUIRequestSemaphore {
    private final SettingsManager settingsManager;
    private final EventPublisher eventPublisher;
    private volatile Semaphore semaphore;

    public SearchUIRequestSemaphore(@ComponentImport SettingsManager settingsManager, @ComponentImport EventPublisher eventPublisher) {
        this.settingsManager = settingsManager;
        this.eventPublisher = eventPublisher;
    }

    Semaphore getSemaphore() {
        if (this.semaphore == null) {
            this.refreshSemaphore();
        }
        return this.semaphore;
    }

    @PostConstruct
    public void setup() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void teardown() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onGlobalSettingsUpdate(GlobalSettingsChangedEvent event) {
        int newMax;
        int oldMax = event.getOldSettings().getMaxSimultaneousQuickNavRequests();
        if (oldMax != (newMax = event.getNewSettings().getMaxSimultaneousQuickNavRequests())) {
            this.refreshSemaphore();
        }
    }

    private void refreshSemaphore() {
        this.semaphore = new Semaphore(this.settingsManager.getGlobalSettings().getMaxSimultaneousQuickNavRequests(), true);
    }
}

