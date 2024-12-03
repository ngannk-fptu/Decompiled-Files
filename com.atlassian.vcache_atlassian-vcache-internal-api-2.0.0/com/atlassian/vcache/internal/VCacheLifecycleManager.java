/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.vcache.internal;

import com.atlassian.vcache.internal.RequestContext;
import com.atlassian.vcache.internal.RequestMetrics;
import java.util.Set;

public interface VCacheLifecycleManager {
    public void transactionSync(RequestContext var1);

    public Set<String> transactionDiscard(RequestContext var1);

    public RequestMetrics metrics(RequestContext var1);
}

