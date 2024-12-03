/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.app;

import com.atlassian.migration.agent.store.impl.AppAccessScopeStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.atlassian.migration.app.AccessScope;
import com.atlassian.migration.app.DefaultRegistrar;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class AppAccessScopeService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AppAccessScopeService.class);
    private final DefaultRegistrar cloudMigrationRegistrar;
    private final AppAccessScopeStore appAccessScopeStore;
    private final PluginTransactionTemplate ptx;

    public AppAccessScopeService(DefaultRegistrar cloudMigrationRegistrar, AppAccessScopeStore appAccessScopeStore, PluginTransactionTemplate ptx) {
        this.cloudMigrationRegistrar = cloudMigrationRegistrar;
        this.appAccessScopeStore = appAccessScopeStore;
        this.ptx = ptx;
    }

    public Set<AccessScope> getAccessScopesDeclaredByApp(String appKey) {
        return this.cloudMigrationRegistrar.getAccessScopesByApp(appKey);
    }

    public void updatedAppAssessScopes(String appKey) {
        log.info("Add access scopes by {}", (Object)appKey);
        this.ptx.write(() -> {
            log.info("Deleting app access scopes by appKey: {}", (Object)appKey);
            this.appAccessScopeStore.deleteAppAccessScopesByAppKey(appKey);
            this.getAccessScopesDeclaredByApp(appKey).forEach(accessScope -> this.appAccessScopeStore.saveAppAccessScope(appKey, (AccessScope)((Object)((Object)accessScope))));
        });
    }

    public int removeAccessScopesByAppKey(String appKey) {
        log.info("Deleting app access scopes by appKey: {}", (Object)appKey);
        int deleted = this.ptx.write(() -> this.appAccessScopeStore.deleteAppAccessScopesByAppKey(appKey));
        log.info("Deleted {} app access scopes", (Object)deleted);
        return deleted;
    }

    public boolean savedAccessScopesAreCurrent(String appKey) {
        Set<AccessScope> accessScopesFromPlugin = this.getAccessScopesDeclaredByApp(appKey);
        Set<AccessScope> savedAccessScopes = this.getSavedAccessScopes(appKey);
        return accessScopesFromPlugin.equals(savedAccessScopes);
    }

    private Set<AccessScope> getSavedAccessScopes(String appKey) {
        List accessScopes = this.ptx.read(() -> this.appAccessScopeStore.getAccessScopesByAppKey(appKey));
        return accessScopes.stream().map(AccessScope::valueOf).collect(Collectors.toSet());
    }
}

