/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.LockModeType
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.entity.ExecutionStatus;
import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.entity.PlanActiveStatus;
import com.atlassian.migration.agent.entity.PlanSchedulerVersion;
import com.atlassian.migration.agent.store.PlanStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.atlassian.migration.agent.store.jpa.QueryBuilder;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.persistence.LockModeType;
import org.apache.commons.lang3.StringUtils;

public class PlanStoreImpl
implements PlanStore {
    private final EntityManagerTemplate tmpl;

    public PlanStoreImpl(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public Plan getPlan(String id) {
        return this.tmpl.query(Plan.class, "select plan from Plan plan where plan.id=:id").param("id", (Object)id).single();
    }

    @Override
    public Plan getPlanAndLock(String id) {
        return this.tmpl.query(Plan.class, "select plan from Plan plan where plan.id=:id").param("id", (Object)id).lock(LockModeType.PESSIMISTIC_WRITE).single();
    }

    @Override
    public List<Plan> getAllPlans(Set<PlanActiveStatus> validStatuses) {
        return this.tmpl.query(Plan.class, "select plan from Plan plan where activeStatus in :validStatuses order by plan.lastUpdate desc").param("validStatuses", validStatuses).list();
    }

    @Override
    public List<Plan> getAllPlansByCloudId(String cloudId) {
        return this.tmpl.query(Plan.class, "select plan from Plan plan where plan.cloudSite.cloudId=:cloudId").param("cloudId", (Object)cloudId).list();
    }

    @Override
    public List<String> getPlanIdsInStatusForSchedulerVersion(List<ExecutionStatus> statuses, PlanSchedulerVersion schedulerVersion) {
        return this.tmpl.query(String.class, "select plan.id from Plan plan where plan.progress.status in :statuses and plan.schedulerVersion = :schedulerVersion").param("statuses", statuses).param("schedulerVersion", (Object)schedulerVersion).list();
    }

    @Override
    public Plan createPlan(Plan plan) {
        Instant now = Instant.now();
        plan.setCreatedTime(now);
        plan.setLastUpdate(now);
        plan.setActiveStatus(PlanActiveStatus.ACTIVE);
        this.tmpl.persist(plan);
        return plan;
    }

    @Override
    public void updatePlan(Plan plan) {
        plan.setLastUpdate(Instant.now());
        this.tmpl.merge(plan);
    }

    @Override
    public void deletePlan(String id) {
        this.tmpl.query("delete Plan plan where plan.id=:id").param("id", (Object)id).update();
    }

    @Override
    public void removeTasks(Plan entity) {
        this.tmpl.query("delete Task task where task.plan.id=:planId").param("planId", (Object)entity.getId()).update();
        entity.setTasks(Collections.emptyList());
    }

    @Override
    public boolean hasPlans(ExecutionStatus ... statuses) {
        if (statuses.length == 0) {
            return this.tmpl.query(Integer.class, "select 1 from Plan plan").first().isPresent();
        }
        return this.tmpl.query(Integer.class, "select 1 from Plan plan where plan.progress.status in :status").param("status", Arrays.asList(statuses)).first().isPresent();
    }

    @Override
    public boolean planNameExists(String name, String planId) {
        StringBuilder query = new StringBuilder("select 1 from Plan plan where plan.name=:name");
        if (StringUtils.isNotEmpty((CharSequence)planId)) {
            query.append(" and plan.id != :planId");
        }
        QueryBuilder<Integer> queryBuilder = this.tmpl.query(Integer.class, query.toString()).param("name", (Object)name);
        if (StringUtils.isNotEmpty((CharSequence)planId)) {
            queryBuilder = queryBuilder.param("planId", (Object)planId);
        }
        return queryBuilder.first().isPresent();
    }

    @Override
    public List<String> getPlanNamesStartingWithPrefix(String prefix) {
        return this.tmpl.query(String.class, "select plan.name from Plan plan where planName like :prefix").param("prefix", (Object)(prefix + "%")).list();
    }

    @Override
    public boolean hasPlansRunningOrStopping() {
        List<ExecutionStatus> statuses = Arrays.asList(ExecutionStatus.RUNNING, ExecutionStatus.STOPPING);
        return this.tmpl.query(Integer.class, "select 1 from Plan plan where plan.progress.status in :statuses").param("statuses", statuses).first().isPresent();
    }
}

