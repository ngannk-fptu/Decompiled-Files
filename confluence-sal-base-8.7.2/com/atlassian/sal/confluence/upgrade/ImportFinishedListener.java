/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.admin.ImportFinishedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.sal.api.upgrade.PluginUpgradeManager
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.sal.confluence.upgrade;

import com.atlassian.confluence.event.events.admin.ImportFinishedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.sal.api.upgrade.PluginUpgradeManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class ImportFinishedListener
implements InitializingBean,
DisposableBean {
    private final EventListenerRegistrar eventListenerRegistrar;
    private final PluginUpgradeManager pluginUpgradeManager;

    public ImportFinishedListener(EventListenerRegistrar eventListenerRegistrar, PluginUpgradeManager pluginUpgradeManager) {
        this.eventListenerRegistrar = eventListenerRegistrar;
        this.pluginUpgradeManager = pluginUpgradeManager;
    }

    public void afterPropertiesSet() throws Exception {
        this.eventListenerRegistrar.register((Object)this);
    }

    @EventListener
    public void onImportFinishedEvent(ImportFinishedEvent event) {
        if (event.isSiteImport() && event.isOriginalEvent()) {
            this.pluginUpgradeManager.upgrade();
        }
    }

    public void destroy() throws Exception {
        this.eventListenerRegistrar.unregister((Object)this);
    }
}

