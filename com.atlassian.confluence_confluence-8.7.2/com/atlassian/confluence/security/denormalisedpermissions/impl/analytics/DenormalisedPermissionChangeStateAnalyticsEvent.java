/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionServiceState;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.domain.DenormalisedServiceStateRecord;
import com.atlassian.event.api.AsynchronousPreferred;
import org.codehaus.jackson.annotate.JsonProperty;

@AsynchronousPreferred
@EventName(value="confluence.denormalised-permissions-service.change-state")
public class DenormalisedPermissionChangeStateAnalyticsEvent {
    private final DenormalisedServiceStateRecord.ServiceType serviceType;
    private final DenormalisedPermissionServiceState newState;
    private final Long durationMillisecond;
    private final Integer processedSpacesCount;
    private final Integer processedContentRecordsCount;

    public static DenormalisedPermissionChangeStateAnalyticsEvent buildServiceReadyEvent(DenormalisedServiceStateRecord.ServiceType serviceType, Long durationMillisecond, Integer processedRecordsCount) {
        return new DenormalisedPermissionChangeStateAnalyticsEvent(serviceType, DenormalisedPermissionServiceState.SERVICE_READY, durationMillisecond, processedRecordsCount);
    }

    public static DenormalisedPermissionChangeStateAnalyticsEvent buildServiceDisabledEvent(DenormalisedServiceStateRecord.ServiceType serviceType, Long durationMillisecond) {
        return new DenormalisedPermissionChangeStateAnalyticsEvent(serviceType, DenormalisedPermissionServiceState.DISABLED, durationMillisecond, null);
    }

    public static DenormalisedPermissionChangeStateAnalyticsEvent buildChangeStateEvent(DenormalisedServiceStateRecord.ServiceType serviceType, DenormalisedPermissionServiceState newState) {
        return new DenormalisedPermissionChangeStateAnalyticsEvent(serviceType, newState, null, null);
    }

    private DenormalisedPermissionChangeStateAnalyticsEvent(DenormalisedServiceStateRecord.ServiceType serviceType, DenormalisedPermissionServiceState newState, Long durationMillisecond, Integer processedRecordsCount) {
        this.serviceType = serviceType;
        this.newState = newState;
        this.durationMillisecond = durationMillisecond;
        this.processedSpacesCount = serviceType == DenormalisedServiceStateRecord.ServiceType.SPACE ? processedRecordsCount : null;
        this.processedContentRecordsCount = serviceType == DenormalisedServiceStateRecord.ServiceType.CONTENT ? processedRecordsCount : null;
    }

    @JsonProperty(value="serviceType")
    public DenormalisedServiceStateRecord.ServiceType getServiceType() {
        return this.serviceType;
    }

    @JsonProperty(value="newState")
    public DenormalisedPermissionServiceState getNewState() {
        return this.newState;
    }

    @JsonProperty(value="durationMillisecond")
    public Long getDurationMillisecond() {
        return this.durationMillisecond;
    }

    @JsonProperty(value="processedSpacesCount")
    public Integer getProcessedSpacesCount() {
        return this.processedSpacesCount;
    }

    @JsonProperty(value="processedContentRecordsCount")
    public Integer getProcessedContentRecordsCount() {
        return this.processedContentRecordsCount;
    }
}

