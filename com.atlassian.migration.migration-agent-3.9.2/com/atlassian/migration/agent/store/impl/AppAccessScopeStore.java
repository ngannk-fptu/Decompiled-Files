/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityExistsException
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.entity.AppAccessScope;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.atlassian.migration.app.AccessScope;
import java.util.List;
import javax.persistence.EntityExistsException;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppAccessScopeStore {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AppAccessScopeStore.class);
    private static final String APP_KEY = "appKey";
    private final EntityManagerTemplate tmpl;

    public AppAccessScopeStore(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    public void saveAppAccessScope(String appKey, AccessScope accessScope) {
        AppAccessScope entry = new AppAccessScope(appKey, accessScope.name());
        try {
            this.tmpl.persist(entry);
        }
        catch (EntityExistsException ex) {
            log.warn("{} already exists in DB. Skipping creation...", (Object)entry);
        }
    }

    public int deleteAppAccessScopesByAppKey(String appKey) {
        String query = "delete from AppAccessScope appAccessScope where appAccessScope.serverAppKey=:appKey";
        return this.tmpl.query(query).param(APP_KEY, (Object)appKey).update();
    }

    public List<String> getAccessScopesByAppKey(String appKey) {
        String query = "select appAccessScope.accessScope from AppAccessScope appAccessScope where appAccessScope.serverAppKey=:appKey";
        return this.tmpl.query(String.class, query).param(APP_KEY, (Object)appKey).list();
    }
}

