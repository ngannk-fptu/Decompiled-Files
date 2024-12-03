/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.internal.RequestContext
 */
package com.atlassian.vcache.internal.core;

import com.atlassian.vcache.internal.RequestContext;
import com.atlassian.vcache.internal.core.TransactionControl;
import java.util.Set;

public interface TransactionControlManager {
    public void registerTransactionalExternalCache(RequestContext var1, String var2, TransactionControl var3);

    public void syncAll(RequestContext var1);

    public Set<String> discardAll(RequestContext var1);
}

