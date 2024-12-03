/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.client.api.ClientToken
 *  com.atlassian.oauth2.client.api.lib.flow.FlowRequestError
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpSession
 */
package com.atlassian.oauth2.client.lib.flow;

import com.atlassian.oauth2.client.api.ClientToken;
import com.atlassian.oauth2.client.api.lib.flow.FlowRequestError;
import com.atlassian.oauth2.client.lib.flow.FlowRequestData;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpSession;

public interface ServletFlowRequestService {
    @Nonnull
    public FlowRequestData fetchFlowRequestDataById(@Nonnull HttpSession var1, @Nonnull String var2) throws IllegalArgumentException;

    @Nonnull
    public FlowRequestData fetchFlowRequestDataByState(@Nonnull HttpSession var1, @Nonnull String var2) throws IllegalArgumentException;

    @Deprecated
    public void updateFlowRequest(@Nonnull HttpSession var1, @Nonnull String var2, @Nonnull ClientToken var3) throws IllegalArgumentException;

    @Deprecated
    public void updateFlowRequest(@Nonnull HttpSession var1, @Nonnull String var2, @Nonnull FlowRequestError var3) throws IllegalArgumentException;

    public void updateFlowRequest(@Nonnull HttpSession var1, @Nonnull FlowRequestData var2, @Nonnull ClientToken var3) throws IllegalArgumentException;

    public void updateFlowRequest(@Nonnull HttpSession var1, @Nonnull FlowRequestData var2, @Nonnull FlowRequestError var3) throws IllegalArgumentException;
}

