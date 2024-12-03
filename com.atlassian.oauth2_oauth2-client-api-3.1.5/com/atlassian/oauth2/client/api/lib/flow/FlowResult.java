/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.client.api.lib.flow;

import com.atlassian.oauth2.client.api.ClientToken;
import com.atlassian.oauth2.client.api.lib.flow.FlowRequestError;

public interface FlowResult {
    public boolean indicatesSuccess();

    public ClientToken toSuccessResult();

    public FlowRequestError toErrorResult();
}

