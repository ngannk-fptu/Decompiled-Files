/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.client.api.ClientToken
 *  com.atlassian.oauth2.client.api.lib.flow.FlowRequestError
 *  com.atlassian.oauth2.client.api.lib.flow.FlowResult
 *  com.google.common.base.Preconditions
 */
package com.atlassian.oauth2.client.lib.flow;

import com.atlassian.oauth2.client.api.ClientToken;
import com.atlassian.oauth2.client.api.lib.flow.FlowRequestError;
import com.atlassian.oauth2.client.api.lib.flow.FlowResult;
import com.atlassian.oauth2.client.lib.ClientTokenImpl;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Objects;

public class FlowResultImpl
implements FlowResult,
Serializable {
    private static final long serialVersionUID = 1158688286283470466L;
    private final ClientTokenImpl clientToken;
    private final FlowRequestError flowRequestError;

    public FlowResultImpl(FlowRequestError flowRequestError) {
        this.clientToken = null;
        this.flowRequestError = Objects.requireNonNull(flowRequestError, "Flow request error cannot be null");
    }

    public FlowResultImpl(ClientToken clientToken) {
        this.clientToken = ClientTokenImpl.from(Objects.requireNonNull(clientToken, "Client token cannot be null"));
        this.flowRequestError = null;
    }

    public boolean indicatesSuccess() {
        return this.clientToken != null;
    }

    public ClientToken toSuccessResult() {
        Preconditions.checkState((this.clientToken != null ? 1 : 0) != 0, (Object)"Result doesn't contain a client token");
        return this.clientToken;
    }

    public FlowRequestError toErrorResult() {
        Preconditions.checkState((this.flowRequestError != null ? 1 : 0) != 0, (Object)"Result doesn't contain an error");
        return this.flowRequestError;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FlowResultImpl that = (FlowResultImpl)o;
        return Objects.equals(this.clientToken, that.clientToken) && Objects.equals(this.flowRequestError, that.flowRequestError);
    }

    public int hashCode() {
        return Objects.hash(this.clientToken, this.flowRequestError);
    }
}

