/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.ConstraintMode
 *  javax.persistence.Entity
 *  javax.persistence.ForeignKey
 *  javax.persistence.JoinColumn
 *  javax.persistence.OneToMany
 *  javax.persistence.Table
 *  lombok.Generated
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.GuardrailsResponse;
import com.atlassian.migration.agent.entity.WithId;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Generated;

@Entity
@Table(name="GR_RESPONSE_GROUP")
public class GuardrailsResponseGroup
extends WithId {
    @OneToMany
    @JoinColumn(name="responseGroupId", nullable=false, updatable=false, insertable=false, foreignKey=@ForeignKey(value=ConstraintMode.NO_CONSTRAINT))
    List<GuardrailsResponse> guardrailsResponses;
    @Column(name="jobId", nullable=false)
    String jobId;
    @Column(name="nodeId", nullable=false)
    String nodeId;
    @Column(name="startTimestamp", nullable=false)
    long startTimestamp;
    @Column(name="endTimestamp")
    long endTimestamp;

    public GuardrailsResponseGroup(String jobId, String nodeId) {
        this.jobId = jobId;
        this.nodeId = nodeId;
        this.startTimestamp = System.currentTimeMillis();
    }

    public String toString() {
        return "GuardrailsResponseGroup{jobId='" + this.jobId + '\'' + ", nodeId='" + this.nodeId + '\'' + ", startTimestamp=" + this.startTimestamp + ", endTimestamp=" + this.endTimestamp + '}';
    }

    @Generated
    public GuardrailsResponseGroup() {
    }

    @Generated
    public List<GuardrailsResponse> getGuardrailsResponses() {
        return this.guardrailsResponses;
    }

    @Generated
    public String getJobId() {
        return this.jobId;
    }

    @Generated
    public String getNodeId() {
        return this.nodeId;
    }

    @Generated
    public long getStartTimestamp() {
        return this.startTimestamp;
    }

    @Generated
    public long getEndTimestamp() {
        return this.endTimestamp;
    }

    @Generated
    public void setGuardrailsResponses(List<GuardrailsResponse> guardrailsResponses) {
        this.guardrailsResponses = guardrailsResponses;
    }

    @Generated
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @Generated
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    @Generated
    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    @Generated
    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    @Override
    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GuardrailsResponseGroup)) {
            return false;
        }
        GuardrailsResponseGroup other = (GuardrailsResponseGroup)o;
        if (!other.canEqual(this)) {
            return false;
        }
        List<GuardrailsResponse> this$guardrailsResponses = this.getGuardrailsResponses();
        List<GuardrailsResponse> other$guardrailsResponses = other.getGuardrailsResponses();
        if (this$guardrailsResponses == null ? other$guardrailsResponses != null : !((Object)this$guardrailsResponses).equals(other$guardrailsResponses)) {
            return false;
        }
        String this$jobId = this.getJobId();
        String other$jobId = other.getJobId();
        if (this$jobId == null ? other$jobId != null : !this$jobId.equals(other$jobId)) {
            return false;
        }
        String this$nodeId = this.getNodeId();
        String other$nodeId = other.getNodeId();
        if (this$nodeId == null ? other$nodeId != null : !this$nodeId.equals(other$nodeId)) {
            return false;
        }
        if (this.getStartTimestamp() != other.getStartTimestamp()) {
            return false;
        }
        return this.getEndTimestamp() == other.getEndTimestamp();
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof GuardrailsResponseGroup;
    }

    @Override
    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        List<GuardrailsResponse> $guardrailsResponses = this.getGuardrailsResponses();
        result = result * 59 + ($guardrailsResponses == null ? 43 : ((Object)$guardrailsResponses).hashCode());
        String $jobId = this.getJobId();
        result = result * 59 + ($jobId == null ? 43 : $jobId.hashCode());
        String $nodeId = this.getNodeId();
        result = result * 59 + ($nodeId == null ? 43 : $nodeId.hashCode());
        long $startTimestamp = this.getStartTimestamp();
        result = result * 59 + (int)($startTimestamp >>> 32 ^ $startTimestamp);
        long $endTimestamp = this.getEndTimestamp();
        result = result * 59 + (int)($endTimestamp >>> 32 ^ $endTimestamp);
        return result;
    }
}

