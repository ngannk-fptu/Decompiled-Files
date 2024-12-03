/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.client.api.ClientConfiguration
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.oauth2.client.lib.flow;

import com.atlassian.oauth2.client.api.ClientConfiguration;
import com.atlassian.oauth2.client.lib.ClientConfigurationImpl;
import com.google.common.base.MoreObjects;
import java.io.Serializable;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FlowRequestData
implements Serializable {
    private static final long serialVersionUID = -2394690665405404948L;
    private final ClientConfiguration clientConfiguration;
    private final String clientRedirectUrl;
    private final String flowRequestId;
    private final String state;

    public FlowRequestData(@Nonnull ClientConfiguration clientConfiguration, @Nonnull String clientRedirectUrl, @Nonnull String flowRequestId, @Nullable String state) {
        this.clientConfiguration = ClientConfigurationImpl.from(Objects.requireNonNull(clientConfiguration, "Client configuration cannot be null"));
        this.clientRedirectUrl = Objects.requireNonNull(clientRedirectUrl, "Client redirect url cannot be null");
        this.flowRequestId = Objects.requireNonNull(flowRequestId, "Flow request ID cannot be null");
        this.state = state;
    }

    @Nonnull
    public ClientConfiguration getClientConfiguration() {
        return this.clientConfiguration;
    }

    @Nonnull
    public String getClientRedirectUrl() {
        return this.clientRedirectUrl;
    }

    @Nonnull
    public String getFlowRequestId() {
        return this.flowRequestId;
    }

    @Nonnull
    public String getState() {
        return this.state;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FlowRequestData that = (FlowRequestData)o;
        return Objects.equals(this.clientConfiguration, that.clientConfiguration) && Objects.equals(this.clientRedirectUrl, that.clientRedirectUrl) && Objects.equals(this.flowRequestId, that.flowRequestId) && Objects.equals(this.state, that.state);
    }

    public int hashCode() {
        return Objects.hash(this.clientConfiguration, this.clientRedirectUrl, this.flowRequestId, this.state);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("clientConfiguration", (Object)this.clientConfiguration).add("clientRedirectUrl", (Object)this.clientRedirectUrl).add("flowRequestId", (Object)this.flowRequestId).add("state", (Object)this.state).toString();
    }
}

