/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.dto;

import lombok.Generated;

public class GuardrailsResponseFileRecord {
    private String queryItem;
    private String queryResult;
    private String queryStatus;
    private String productName;
    private String productVersion;
    private String sen;
    private String serverId;
    private String instanceTimezone;
    private String assessmentDate;

    @Generated
    public String getQueryItem() {
        return this.queryItem;
    }

    @Generated
    public String getQueryResult() {
        return this.queryResult;
    }

    @Generated
    public String getQueryStatus() {
        return this.queryStatus;
    }

    @Generated
    public String getProductName() {
        return this.productName;
    }

    @Generated
    public String getProductVersion() {
        return this.productVersion;
    }

    @Generated
    public String getSen() {
        return this.sen;
    }

    @Generated
    public String getServerId() {
        return this.serverId;
    }

    @Generated
    public String getInstanceTimezone() {
        return this.instanceTimezone;
    }

    @Generated
    public String getAssessmentDate() {
        return this.assessmentDate;
    }

    @Generated
    public void setQueryItem(String queryItem) {
        this.queryItem = queryItem;
    }

    @Generated
    public void setQueryResult(String queryResult) {
        this.queryResult = queryResult;
    }

    @Generated
    public void setQueryStatus(String queryStatus) {
        this.queryStatus = queryStatus;
    }

    @Generated
    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Generated
    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    @Generated
    public void setSen(String sen) {
        this.sen = sen;
    }

    @Generated
    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    @Generated
    public void setInstanceTimezone(String instanceTimezone) {
        this.instanceTimezone = instanceTimezone;
    }

    @Generated
    public void setAssessmentDate(String assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    @Generated
    public GuardrailsResponseFileRecord(String queryItem, String queryResult, String queryStatus, String productName, String productVersion, String sen, String serverId, String instanceTimezone, String assessmentDate) {
        this.queryItem = queryItem;
        this.queryResult = queryResult;
        this.queryStatus = queryStatus;
        this.productName = productName;
        this.productVersion = productVersion;
        this.sen = sen;
        this.serverId = serverId;
        this.instanceTimezone = instanceTimezone;
        this.assessmentDate = assessmentDate;
    }
}

