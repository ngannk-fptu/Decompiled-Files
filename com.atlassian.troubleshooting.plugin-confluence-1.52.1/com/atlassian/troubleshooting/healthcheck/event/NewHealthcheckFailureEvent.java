/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.troubleshooting.healthcheck.event;

import com.atlassian.troubleshooting.api.healthcheck.HealthCheckStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class NewHealthcheckFailureEvent {
    private final HealthCheckStatus status;

    public NewHealthcheckFailureEvent(HealthCheckStatus status) {
        this.status = status;
    }

    public HealthCheckStatus getStatus() {
        return this.status;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NewHealthcheckFailureEvent that = (NewHealthcheckFailureEvent)o;
        return new EqualsBuilder().append((Object)this.status, (Object)that.status).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append((Object)this.status).toHashCode();
    }
}

