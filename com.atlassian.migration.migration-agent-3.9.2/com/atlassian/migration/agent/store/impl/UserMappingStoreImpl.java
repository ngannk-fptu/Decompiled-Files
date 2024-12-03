/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.entity.UserMapping;
import com.atlassian.migration.agent.store.UserMappingStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;

public class UserMappingStoreImpl
implements UserMappingStore {
    private final EntityManagerTemplate tmpl;

    public UserMappingStoreImpl(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public List<UserMapping> getUserMappings() {
        return this.tmpl.query(UserMapping.class, "select userMapping from UserMapping userMapping").list();
    }
}

