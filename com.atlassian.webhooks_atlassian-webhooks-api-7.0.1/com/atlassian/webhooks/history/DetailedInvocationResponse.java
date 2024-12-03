/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.history;

import com.atlassian.webhooks.history.DetailedInvocationResult;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface DetailedInvocationResponse
extends DetailedInvocationResult {
    @Nonnull
    public Optional<String> getBody();

    @Nonnull
    public Map<String, String> getHeaders();

    public int getStatusCode();
}

