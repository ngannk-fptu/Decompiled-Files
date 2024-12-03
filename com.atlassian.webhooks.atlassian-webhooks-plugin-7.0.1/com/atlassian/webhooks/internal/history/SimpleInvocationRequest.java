/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.history.InvocationRequest
 *  com.atlassian.webhooks.request.Method
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.internal.history;

import com.atlassian.webhooks.history.InvocationRequest;
import com.atlassian.webhooks.request.Method;
import java.util.Objects;
import javax.annotation.Nonnull;

public class SimpleInvocationRequest
implements InvocationRequest {
    private final Method method;
    private final String url;

    public SimpleInvocationRequest(Method method, String url) {
        this.method = Objects.requireNonNull(method, "method");
        this.url = Objects.requireNonNull(url, "url");
    }

    @Nonnull
    public Method getMethod() {
        return this.method;
    }

    @Nonnull
    public String getUrl() {
        return this.url;
    }
}

