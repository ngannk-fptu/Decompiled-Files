/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.plugin.manager.PluginPersistentState
 *  com.atlassian.plugin.manager.PluginPersistentState$Builder
 *  com.atlassian.plugin.manager.PluginPersistentStateStore
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.impl.tenant.ThreadLocalTenantGate;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.plugin.manager.PluginPersistentState;
import com.atlassian.plugin.manager.PluginPersistentStateStore;
import java.util.HashMap;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BandanaPluginStateStore
implements PluginPersistentStateStore {
    private static final Logger log = LoggerFactory.getLogger(BandanaPluginStateStore.class);
    private BandanaManager bandanaManager;
    private static final String PLUGIN_MANAGER_STATE_MAP_BANDANA_KEY = "plugin.manager.state.Map";
    private volatile PluginPersistentState inMemoryState = PluginPersistentState.Builder.create().toState();

    public void save(PluginPersistentState state) {
        if (!BandanaPluginStateStore.isDatabaseConfigured()) {
            this.inMemoryState = state;
            return;
        }
        Callable<Void> saveState = ThreadLocalTenantGate.withTenantPermit(() -> {
            this.getBandanaManager().setValue((BandanaContext)new ConfluenceBandanaContext(), PLUGIN_MANAGER_STATE_MAP_BANDANA_KEY, new HashMap(state.getMap()));
            return null;
        });
        try {
            saveState.call();
        }
        catch (Exception e) {
            log.error("Exception when saving plugins state to the database. Saving to in memory state.", (Throwable)e);
            this.inMemoryState = state;
        }
    }

    public PluginPersistentState load() {
        if (!BandanaPluginStateStore.isDatabaseConfigured()) {
            return this.inMemoryState;
        }
        Callable<PluginPersistentState> getStateFromDB = ThreadLocalTenantGate.withTenantPermit(() -> {
            HashMap<String, Boolean> m = (HashMap<String, Boolean>)this.getBandanaManager().getValue((BandanaContext)new ConfluenceBandanaContext(), PLUGIN_MANAGER_STATE_MAP_BANDANA_KEY);
            if (m == null) {
                m = new HashMap<String, Boolean>();
            }
            HashMap stateMap = new HashMap();
            m.forEach(stateMap::put);
            return PluginPersistentState.Builder.create().addState(stateMap).toState();
        });
        try {
            return getStateFromDB.call();
        }
        catch (Exception e) {
            log.error("Exception when loading plugins state from the database. Falling back to in memory state.", (Throwable)e);
            return this.inMemoryState;
        }
    }

    public BandanaManager getBandanaManager() {
        return this.bandanaManager;
    }

    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    private static boolean isDatabaseConfigured() {
        AtlassianBootstrapManager bootstrapManager = BootstrapUtils.getBootstrapManager();
        return bootstrapManager != null && bootstrapManager.getHibernateConfig() != null && bootstrapManager.getHibernateConfig().isHibernateSetup();
    }
}

