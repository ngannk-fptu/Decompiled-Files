/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.EnumType
 *  javax.persistence.Enumerated
 *  javax.persistence.FetchType
 *  javax.persistence.JoinColumn
 *  javax.persistence.Lob
 *  javax.persistence.ManyToOne
 *  javax.persistence.Table
 *  lombok.Generated
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.GuardrailsResponseGroup;
import com.atlassian.migration.agent.entity.WithId;
import com.atlassian.migration.agent.store.guardrails.GuardrailsResponseType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Generated;

@Entity
@Table(name="GUARDRAILS_RESPONSE")
public class GuardrailsResponse
extends WithId {
    @Column(name="guardrailsResponse")
    @Lob
    private String queryResponse;
    @Column(name="guardrailsResponseType")
    @Enumerated(value=EnumType.STRING)
    private GuardrailsResponseType guardrailsResponseType;
    @Column(name="queryId", nullable=false)
    private String queryId;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="responseGroupId", nullable=false, updatable=false, insertable=false)
    private GuardrailsResponseGroup responseGroup;
    @Column(name="responseGroupId")
    private String responseGroupId;
    @Column(name="success", nullable=false)
    private boolean success;
    @Column(name="queryComplexity")
    private String queryComplexity;
    @Column(name="queryStatus")
    private String queryStatus;

    public GuardrailsResponse(String queryResponse, String queryId, boolean success, String queryStatus) {
        this.queryResponse = queryResponse;
        this.queryId = queryId;
        this.success = success;
        this.queryStatus = queryStatus;
    }

    @Generated
    public static GuardrailsResponseBuilder builder() {
        return new GuardrailsResponseBuilder();
    }

    @Generated
    public String getQueryResponse() {
        return this.queryResponse;
    }

    @Generated
    public GuardrailsResponseType getGuardrailsResponseType() {
        return this.guardrailsResponseType;
    }

    @Generated
    public String getQueryId() {
        return this.queryId;
    }

    @Generated
    public GuardrailsResponseGroup getResponseGroup() {
        return this.responseGroup;
    }

    @Generated
    public String getResponseGroupId() {
        return this.responseGroupId;
    }

    @Generated
    public boolean isSuccess() {
        return this.success;
    }

    @Generated
    public String getQueryComplexity() {
        return this.queryComplexity;
    }

    @Generated
    public String getQueryStatus() {
        return this.queryStatus;
    }

    @Generated
    public void setQueryResponse(String queryResponse) {
        this.queryResponse = queryResponse;
    }

    @Generated
    public void setGuardrailsResponseType(GuardrailsResponseType guardrailsResponseType) {
        this.guardrailsResponseType = guardrailsResponseType;
    }

    @Generated
    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    @Generated
    public void setResponseGroup(GuardrailsResponseGroup responseGroup) {
        this.responseGroup = responseGroup;
    }

    @Generated
    public void setResponseGroupId(String responseGroupId) {
        this.responseGroupId = responseGroupId;
    }

    @Generated
    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Generated
    public void setQueryComplexity(String queryComplexity) {
        this.queryComplexity = queryComplexity;
    }

    @Generated
    public void setQueryStatus(String queryStatus) {
        this.queryStatus = queryStatus;
    }

    @Override
    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GuardrailsResponse)) {
            return false;
        }
        GuardrailsResponse other = (GuardrailsResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$queryResponse = this.getQueryResponse();
        String other$queryResponse = other.getQueryResponse();
        if (this$queryResponse == null ? other$queryResponse != null : !this$queryResponse.equals(other$queryResponse)) {
            return false;
        }
        GuardrailsResponseType this$guardrailsResponseType = this.getGuardrailsResponseType();
        GuardrailsResponseType other$guardrailsResponseType = other.getGuardrailsResponseType();
        if (this$guardrailsResponseType == null ? other$guardrailsResponseType != null : !((Object)((Object)this$guardrailsResponseType)).equals((Object)other$guardrailsResponseType)) {
            return false;
        }
        String this$queryId = this.getQueryId();
        String other$queryId = other.getQueryId();
        if (this$queryId == null ? other$queryId != null : !this$queryId.equals(other$queryId)) {
            return false;
        }
        GuardrailsResponseGroup this$responseGroup = this.getResponseGroup();
        GuardrailsResponseGroup other$responseGroup = other.getResponseGroup();
        if (this$responseGroup == null ? other$responseGroup != null : !((Object)this$responseGroup).equals(other$responseGroup)) {
            return false;
        }
        String this$responseGroupId = this.getResponseGroupId();
        String other$responseGroupId = other.getResponseGroupId();
        if (this$responseGroupId == null ? other$responseGroupId != null : !this$responseGroupId.equals(other$responseGroupId)) {
            return false;
        }
        if (this.isSuccess() != other.isSuccess()) {
            return false;
        }
        String this$queryComplexity = this.getQueryComplexity();
        String other$queryComplexity = other.getQueryComplexity();
        if (this$queryComplexity == null ? other$queryComplexity != null : !this$queryComplexity.equals(other$queryComplexity)) {
            return false;
        }
        String this$queryStatus = this.getQueryStatus();
        String other$queryStatus = other.getQueryStatus();
        return !(this$queryStatus == null ? other$queryStatus != null : !this$queryStatus.equals(other$queryStatus));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof GuardrailsResponse;
    }

    @Override
    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $queryResponse = this.getQueryResponse();
        result = result * 59 + ($queryResponse == null ? 43 : $queryResponse.hashCode());
        GuardrailsResponseType $guardrailsResponseType = this.getGuardrailsResponseType();
        result = result * 59 + ($guardrailsResponseType == null ? 43 : ((Object)((Object)$guardrailsResponseType)).hashCode());
        String $queryId = this.getQueryId();
        result = result * 59 + ($queryId == null ? 43 : $queryId.hashCode());
        GuardrailsResponseGroup $responseGroup = this.getResponseGroup();
        result = result * 59 + ($responseGroup == null ? 43 : ((Object)$responseGroup).hashCode());
        String $responseGroupId = this.getResponseGroupId();
        result = result * 59 + ($responseGroupId == null ? 43 : $responseGroupId.hashCode());
        result = result * 59 + (this.isSuccess() ? 79 : 97);
        String $queryComplexity = this.getQueryComplexity();
        result = result * 59 + ($queryComplexity == null ? 43 : $queryComplexity.hashCode());
        String $queryStatus = this.getQueryStatus();
        result = result * 59 + ($queryStatus == null ? 43 : $queryStatus.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "GuardrailsResponse(queryResponse=" + this.getQueryResponse() + ", guardrailsResponseType=" + (Object)((Object)this.getGuardrailsResponseType()) + ", queryId=" + this.getQueryId() + ", responseGroup=" + this.getResponseGroup() + ", responseGroupId=" + this.getResponseGroupId() + ", success=" + this.isSuccess() + ", queryComplexity=" + this.getQueryComplexity() + ", queryStatus=" + this.getQueryStatus() + ")";
    }

    @Generated
    public GuardrailsResponse() {
    }

    @Generated
    public GuardrailsResponse(String queryResponse, GuardrailsResponseType guardrailsResponseType, String queryId, GuardrailsResponseGroup responseGroup, String responseGroupId, boolean success, String queryComplexity, String queryStatus) {
        this.queryResponse = queryResponse;
        this.guardrailsResponseType = guardrailsResponseType;
        this.queryId = queryId;
        this.responseGroup = responseGroup;
        this.responseGroupId = responseGroupId;
        this.success = success;
        this.queryComplexity = queryComplexity;
        this.queryStatus = queryStatus;
    }

    @Generated
    public static class GuardrailsResponseBuilder {
        @Generated
        private String queryResponse;
        @Generated
        private GuardrailsResponseType guardrailsResponseType;
        @Generated
        private String queryId;
        @Generated
        private GuardrailsResponseGroup responseGroup;
        @Generated
        private String responseGroupId;
        @Generated
        private boolean success;
        @Generated
        private String queryComplexity;
        @Generated
        private String queryStatus;

        @Generated
        GuardrailsResponseBuilder() {
        }

        @Generated
        public GuardrailsResponseBuilder queryResponse(String queryResponse) {
            this.queryResponse = queryResponse;
            return this;
        }

        @Generated
        public GuardrailsResponseBuilder guardrailsResponseType(GuardrailsResponseType guardrailsResponseType) {
            this.guardrailsResponseType = guardrailsResponseType;
            return this;
        }

        @Generated
        public GuardrailsResponseBuilder queryId(String queryId) {
            this.queryId = queryId;
            return this;
        }

        @Generated
        public GuardrailsResponseBuilder responseGroup(GuardrailsResponseGroup responseGroup) {
            this.responseGroup = responseGroup;
            return this;
        }

        @Generated
        public GuardrailsResponseBuilder responseGroupId(String responseGroupId) {
            this.responseGroupId = responseGroupId;
            return this;
        }

        @Generated
        public GuardrailsResponseBuilder success(boolean success) {
            this.success = success;
            return this;
        }

        @Generated
        public GuardrailsResponseBuilder queryComplexity(String queryComplexity) {
            this.queryComplexity = queryComplexity;
            return this;
        }

        @Generated
        public GuardrailsResponseBuilder queryStatus(String queryStatus) {
            this.queryStatus = queryStatus;
            return this;
        }

        @Generated
        public GuardrailsResponse build() {
            return new GuardrailsResponse(this.queryResponse, this.guardrailsResponseType, this.queryId, this.responseGroup, this.responseGroupId, this.success, this.queryComplexity, this.queryStatus);
        }

        @Generated
        public String toString() {
            return "GuardrailsResponse.GuardrailsResponseBuilder(queryResponse=" + this.queryResponse + ", guardrailsResponseType=" + (Object)((Object)this.guardrailsResponseType) + ", queryId=" + this.queryId + ", responseGroup=" + this.responseGroup + ", responseGroupId=" + this.responseGroupId + ", success=" + this.success + ", queryComplexity=" + this.queryComplexity + ", queryStatus=" + this.queryStatus + ")";
        }
    }
}

