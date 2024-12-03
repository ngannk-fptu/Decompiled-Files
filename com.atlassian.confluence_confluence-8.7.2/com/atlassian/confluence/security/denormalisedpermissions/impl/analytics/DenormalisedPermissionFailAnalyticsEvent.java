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
import com.atlassian.event.api.AsynchronousPreferred;
import org.codehaus.jackson.annotate.JsonProperty;

@AsynchronousPreferred
@EventName(value="confluence.denormalised-permissions-service.fail")
public class DenormalisedPermissionFailAnalyticsEvent {
    private final Action failedAction;
    private final ErrorType errorType;
    private final Integer numberOfSids;

    public DenormalisedPermissionFailAnalyticsEvent(Action failedAction) {
        this(failedAction, ErrorType.GENERAL_EXCEPTION);
    }

    public DenormalisedPermissionFailAnalyticsEvent(Action failedAction, ErrorType errorType) {
        this(failedAction, errorType, 0);
    }

    public DenormalisedPermissionFailAnalyticsEvent(Action failedAction, ErrorType errorType, Integer numberOfSids) {
        this.failedAction = failedAction;
        this.errorType = errorType;
        this.numberOfSids = numberOfSids;
    }

    @JsonProperty(value="failedAction")
    public Action getFailedAction() {
        return this.failedAction;
    }

    @JsonProperty(value="errorType")
    public ErrorType getErrorType() {
        return this.errorType;
    }

    @JsonProperty(value="numberOfSids")
    public Integer getNumberOfSids() {
        return this.numberOfSids;
    }

    public static enum ErrorType {
        GENERAL_EXCEPTION,
        REJECTED_BY_FAST_PERMISSIONS,
        TOO_MANY_SIDS;

    }

    public static enum Action {
        GET_PERMITTED_SPACES,
        GET_PERMITTED_SPACE_IDS,
        GET_ALL_SPACE_KEYS_WITH_PERMISSION_STATUSES,
        GET_VISIBLE_CHILD_PAGES,
        GET_VISIBLE_TOP_LEVEL_PAGES,
        GET_ALL_VISIBLE_PAGES_IN_SPACE,
        GET_PERMITTED_PAGE_IDS,
        GET_ALL_USER_SIDS;

    }
}

