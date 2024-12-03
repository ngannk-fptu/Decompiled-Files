/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.Index
 *  javax.persistence.Table
 *  lombok.Generated
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.WithId;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Generated;

@Entity
@Table(name="INST_ANALYSIS_CTRL", indexes={@Index(name="INSTANCE_ANALYSIS_CONTROL_ANALYSIS_TYPE_IDX", columnList="analysisType")})
public class InstanceAnalysisControl
extends WithId {
    @Column(name="analysisType", nullable=false)
    private String analysisType;
    @Column(name="startTimestamp", nullable=false)
    private long startTimestamp;
    @Column(name="endTimestamp")
    private Long endTimestamp;

    public InstanceAnalysisControl(String analysisType, long startTimestamp) {
        this.analysisType = analysisType;
        this.startTimestamp = startTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        InstanceAnalysisControl that = (InstanceAnalysisControl)o;
        return Objects.equals(this.analysisType, that.analysisType) && Objects.equals(this.startTimestamp, that.startTimestamp) && Objects.equals(this.endTimestamp, that.endTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.analysisType, this.startTimestamp, this.endTimestamp);
    }

    @Generated
    public InstanceAnalysisControl() {
    }

    @Generated
    public String getAnalysisType() {
        return this.analysisType;
    }

    @Generated
    public long getStartTimestamp() {
        return this.startTimestamp;
    }

    @Generated
    public Long getEndTimestamp() {
        return this.endTimestamp;
    }

    @Generated
    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    @Generated
    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    @Generated
    public void setEndTimestamp(Long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }
}

