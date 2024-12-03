/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.vcache.internal;

import com.atlassian.vcache.internal.RequestContext;

@FunctionalInterface
public interface BegunTransactionalActivityHandler {
    public void onRequest(RequestContext var1);
}

