/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.client.api.lib.flow.FlowRequest
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.client.lib.flow;

import com.atlassian.oauth2.client.api.lib.flow.FlowRequest;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import javax.annotation.Nonnull;

public class FlowRequestImpl
implements FlowRequest {
    private final String id;
    private final String initFlowUrl;

    public FlowRequestImpl(@Nonnull String id, @Nonnull String initFlowUrl) {
        this.id = id;
        this.initFlowUrl = initFlowUrl;
    }

    @Nonnull
    public String getId() {
        return this.id;
    }

    @Nonnull
    public String getInitFlowUrl() {
        return this.initFlowUrl;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FlowRequestImpl that = (FlowRequestImpl)o;
        return this.id.equals(that.id) && Objects.equals(this.initFlowUrl, that.initFlowUrl);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.initFlowUrl);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.id).add("initFlowUrl", (Object)this.initFlowUrl).toString();
    }
}

