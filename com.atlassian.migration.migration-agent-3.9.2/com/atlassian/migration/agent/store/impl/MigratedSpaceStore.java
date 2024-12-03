/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.migration.agent.entity.MigratedSpace;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.atlassian.migration.agent.store.jpa.QueryBuilder;
import java.util.List;
import java.util.stream.Collectors;

public class MigratedSpaceStore {
    private final EntityManagerTemplate tmpl;

    public MigratedSpaceStore(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    public List<String> getAllSpaces() {
        String query = "from MigratedSpace s";
        QueryBuilder<MigratedSpace> builder = this.tmpl.query(MigratedSpace.class, query);
        return builder.list().stream().map(MigratedSpace::getKey).collect(Collectors.toList());
    }

    public boolean addSpace(Space space, String cloudId) {
        MigratedSpace mSpace = new MigratedSpace();
        mSpace.setId(space.getId());
        mSpace.setKey(space.getKey());
        mSpace.setName(space.getName());
        mSpace.setCloud(cloudId);
        this.tmpl.persist(mSpace);
        return true;
    }

    public boolean removeSpaces(List<String> spaces, String cloudId) {
        spaces.forEach(space -> this.tmpl.query("delete MigratedSpace s where s.key=:key and s.cloud=:cloud").param("key", space).param("cloud", (Object)cloudId).update());
        return true;
    }

    public boolean hasSpace(com.atlassian.migration.agent.entity.Space s, String cloudId) {
        String query = "from MigratedSpace s where s.key=:key and s.cloud=:cloud";
        QueryBuilder<MigratedSpace> builder = this.tmpl.query(MigratedSpace.class, query).param("key", (Object)s.getKey()).param("cloud", (Object)cloudId);
        return !builder.list().isEmpty();
    }
}

