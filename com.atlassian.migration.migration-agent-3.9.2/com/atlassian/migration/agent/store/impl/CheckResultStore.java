/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.entity.CheckExecutionStatus;
import com.atlassian.migration.agent.entity.CheckResultEntity;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class CheckResultStore {
    private static final String EXECUTION_ID = "executionId";
    private final EntityManagerTemplate tmpl;

    public CheckResultStore(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    public CheckResultEntity create(CheckResultEntity checkResult) {
        this.tmpl.persist(checkResult);
        return checkResult;
    }

    public void update(CheckResultEntity checkResult) {
        checkResult.setLastUpdated(Instant.now());
        this.tmpl.merge(checkResult);
    }

    public List<CheckResultEntity> getByExecutionId(String executionId) {
        String query = "select checkResult from CheckResultEntity checkResult where checkResult.executionId=:executionId";
        return this.tmpl.query(CheckResultEntity.class, query).param(EXECUTION_ID, (Object)executionId).list();
    }

    public Optional<CheckResultEntity> getByExecutionIdAndCheckType(String executionId, CheckType checkType) {
        String query = "select checkResult from CheckResultEntity checkResult where checkResult.executionId=:executionId and checkResult.checkType=:checkType";
        return this.tmpl.query(CheckResultEntity.class, query).param(EXECUTION_ID, (Object)executionId).param("checkType", (Object)checkType.value()).first();
    }

    public int deleteCheckResultsByExecutionId(String executionId) {
        String query = "delete from CheckResultEntity checkResult where checkResult.executionId=:executionId";
        return this.tmpl.query(query).param(EXECUTION_ID, (Object)executionId).update();
    }

    public void updateExecutionId(String currentExecutionId, String newExecutionId) {
        String query = "update CheckResultEntity checkResult set executionId = :newExecutionId where checkResult.executionId=:executionId";
        this.tmpl.query(query).param(EXECUTION_ID, (Object)currentExecutionId).param("newExecutionId", (Object)newExecutionId).update();
    }

    public void batchUpdateStatus(CheckExecutionStatus currentStatus, Instant lastUpdatedThreshold, CheckExecutionStatus newStatus) {
        String query = "update CheckResultEntity checkResult set status = :newStatus where checkResult.lastUpdated<:updatedThreshold and status = :currentStatus";
        this.tmpl.query(query).param("newStatus", (Object)newStatus).param("currentStatus", (Object)currentStatus).param("updatedThreshold", (Object)lastUpdatedThreshold).update();
    }

    public List<CheckResultEntity> getRunningChecks() {
        String query = "select checkResult from CheckResultEntity checkResult where status= :runningStatus";
        return this.tmpl.query(CheckResultEntity.class, query).param("runningStatus", (Object)CheckExecutionStatus.RUNNING).list();
    }
}

