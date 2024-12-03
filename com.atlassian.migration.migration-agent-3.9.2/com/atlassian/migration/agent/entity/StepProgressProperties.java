/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.Id
 *  javax.persistence.Lob
 *  javax.persistence.Table
 */
package com.atlassian.migration.agent.entity;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Table(name="MIG_STEP_PROGRESS_PROPERTIES")
@Entity
public class StepProgressProperties {
    @Id
    @Column(name="stepId", unique=true)
    private String stepId;
    @Column(name="progressProperties")
    @Lob
    private String progressProperties;

    public String getStepId() {
        return this.stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public String getProgressProperties() {
        return this.progressProperties;
    }

    public void setProgressProperties(String progressProperties) {
        this.progressProperties = progressProperties;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        StepProgressProperties progressProperties2 = (StepProgressProperties)o;
        return Objects.equals(this.stepId, progressProperties2.stepId);
    }

    public int hashCode() {
        return Objects.hash(this.stepId);
    }
}

