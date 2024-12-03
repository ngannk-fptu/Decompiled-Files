/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.google.common.base.Throwables
 */
package com.atlassian.confluence.tenant;

import com.atlassian.confluence.impl.tenant.ThreadLocalTenantGate;
import com.atlassian.confluence.util.longrunning.DelegatingLongRunningTask;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.google.common.base.Throwables;
import java.util.concurrent.Executors;

@Deprecated(forRemoval=true)
public class OpenTenantGateLongRunningTask
extends DelegatingLongRunningTask {
    public OpenTenantGateLongRunningTask(LongRunningTask delegate) {
        super(delegate);
    }

    @Override
    public void run() {
        try {
            ThreadLocalTenantGate.withTenantPermit(Executors.callable((Runnable)this.delegate)).call();
        }
        catch (Exception e) {
            Throwables.throwIfUnchecked((Throwable)e);
            throw new RuntimeException(e);
        }
    }
}

