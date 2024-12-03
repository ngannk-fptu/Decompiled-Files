/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpSession
 */
package com.atlassian.oauth2.client.api.lib.flow;

import com.atlassian.annotations.PublicApi;
import com.atlassian.oauth2.client.api.ClientConfiguration;
import com.atlassian.oauth2.client.api.lib.flow.FlowRequest;
import com.atlassian.oauth2.client.api.lib.flow.FlowResult;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpSession;

@PublicApi
public interface FlowRequestService {
    @Nonnull
    public FlowRequest createFlowRequest(@Nonnull HttpSession var1, @Nonnull ClientConfiguration var2, @Nonnull Function<String, String> var3) throws IllegalArgumentException, IllegalStateException;

    @Nonnull
    public FlowResult getFlowResult(@Nonnull HttpSession var1, @Nonnull String var2) throws IllegalArgumentException;
}

