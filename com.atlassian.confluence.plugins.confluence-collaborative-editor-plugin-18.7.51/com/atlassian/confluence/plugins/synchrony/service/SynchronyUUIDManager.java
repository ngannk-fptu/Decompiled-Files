/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.tenancy.api.event.TenantArrivedEvent
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.service;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.tenancy.api.event.TenantArrivedEvent;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="synchrony-uuid-manager")
public class SynchronyUUIDManager {
    private final BandanaManager bandanaManager;
    private static final String SYNCHRONY_COLLABORATIVE_EDITOR_UUID = "synchrony_collaborative_editor_UUID";

    @Autowired
    public SynchronyUUIDManager(BandanaManager bandanaManager, EventListenerRegistrar eventListenerRegistrar) {
        this.bandanaManager = bandanaManager;
        eventListenerRegistrar.register((Object)this);
    }

    @EventListener
    public void onTenantArrived(TenantArrivedEvent event) {
        this.setNewGlobalUuid();
    }

    String getGlobalUuid() {
        String globalUuid = (String)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, SYNCHRONY_COLLABORATIVE_EDITOR_UUID);
        if (globalUuid == null) {
            globalUuid = this.setNewGlobalUuid();
        }
        return globalUuid;
    }

    private String setNewGlobalUuid() {
        String newUuid = UUID.randomUUID().toString();
        this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, SYNCHRONY_COLLABORATIVE_EDITOR_UUID, (Object)newUuid);
        return newUuid;
    }
}

