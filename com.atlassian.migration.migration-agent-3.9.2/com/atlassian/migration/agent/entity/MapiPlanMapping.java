/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.Id
 *  javax.persistence.Table
 *  lombok.Generated
 */
package com.atlassian.migration.agent.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Generated;

@Entity
@Table(name="MIG_MAPI_PLAN_MAPPING")
public class MapiPlanMapping {
    @Id
    @Column(name="jobId")
    private String jobId;
    @Column(name="planId")
    private String planId;
    @Column(name="migrationId")
    private String migrationId;

    @Generated
    public MapiPlanMapping(String jobId, String planId, String migrationId) {
        this.jobId = jobId;
        this.planId = planId;
        this.migrationId = migrationId;
    }

    @Generated
    public MapiPlanMapping() {
    }

    @Generated
    public String getJobId() {
        return this.jobId;
    }

    @Generated
    public String getPlanId() {
        return this.planId;
    }

    @Generated
    public String getMigrationId() {
        return this.migrationId;
    }

    @Generated
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @Generated
    public void setPlanId(String planId) {
        this.planId = planId;
    }

    @Generated
    public void setMigrationId(String migrationId) {
        this.migrationId = migrationId;
    }

    @Generated
    public String toString() {
        return "MapiPlanMapping(jobId=" + this.getJobId() + ", planId=" + this.getPlanId() + ", migrationId=" + this.getMigrationId() + ")";
    }
}

