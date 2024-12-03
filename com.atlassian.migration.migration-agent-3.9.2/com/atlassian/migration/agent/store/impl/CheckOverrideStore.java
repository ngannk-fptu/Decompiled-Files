/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.entity.CheckOverrideEntity;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;
import java.util.Optional;

public class CheckOverrideStore {
    private static final String EXECUTION_ID = "executionId";
    private final EntityManagerTemplate tmpl;

    public CheckOverrideStore(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    public CheckOverrideEntity create(CheckOverrideEntity entity) {
        this.tmpl.persist(entity);
        return entity;
    }

    public void createCheckOverrides(String executionId, List<String> types) {
        types.forEach(type -> {
            Optional<CheckOverrideEntity> entity = this.findByExecutionIdAndType(executionId, (String)type);
            if (!entity.isPresent()) {
                this.create(new CheckOverrideEntity(executionId, (String)type));
            }
        });
    }

    public int deleteByExecutionIdAndTypes(String executionId, List<String> types) {
        String query = "delete from CheckOverrideEntity checkOverride where checkOverride.executionId=:executionId and checkOverride.checkType in (:types)";
        return this.tmpl.query(query).param(EXECUTION_ID, (Object)executionId).param("types", types).update();
    }

    public Optional<CheckOverrideEntity> findByExecutionIdAndType(String executionId, String checkType) {
        String query = "select checkOverride from CheckOverrideEntity checkOverride where checkOverride.executionId=:executionId and checkOverride.checkType=:checkType";
        return this.tmpl.query(CheckOverrideEntity.class, query).param("checkType", (Object)checkType).param(EXECUTION_ID, (Object)executionId).first();
    }

    public List<CheckOverrideEntity> getByExecutionId(String executionId) {
        String query = "select checkOverride from CheckOverrideEntity checkOverride where checkOverride.executionId=:executionId";
        return this.tmpl.query(CheckOverrideEntity.class, query).param(EXECUTION_ID, (Object)executionId).list();
    }

    public void updateExecutionId(String currentExecutionId, String newExecutionId) {
        String query = "update CheckOverrideEntity checkOverride set executionId = :newExecutionId where checkOverride.executionId=:executionId";
        this.tmpl.query(query).param(EXECUTION_ID, (Object)currentExecutionId).param("newExecutionId", (Object)newExecutionId).update();
    }

    public List<CheckOverrideEntity> findByExecutionIdAndTypes(String executionId, List<String> checkTypes) {
        String query = "select checkOverride from CheckOverrideEntity checkOverride where checkOverride.executionId=:executionId and checkOverride.checkType in (:checkTypes)";
        return this.tmpl.query(CheckOverrideEntity.class, query).param("checkTypes", checkTypes).param(EXECUTION_ID, (Object)executionId).list();
    }
}

