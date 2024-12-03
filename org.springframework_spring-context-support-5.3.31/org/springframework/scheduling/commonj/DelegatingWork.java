/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  commonj.work.Work
 *  org.springframework.scheduling.SchedulingAwareRunnable
 *  org.springframework.util.Assert
 */
package org.springframework.scheduling.commonj;

import commonj.work.Work;
import org.springframework.scheduling.SchedulingAwareRunnable;
import org.springframework.util.Assert;

@Deprecated
public class DelegatingWork
implements Work {
    private final Runnable delegate;

    public DelegatingWork(Runnable delegate) {
        Assert.notNull((Object)delegate, (String)"Delegate must not be null");
        this.delegate = delegate;
    }

    public final Runnable getDelegate() {
        return this.delegate;
    }

    public void run() {
        this.delegate.run();
    }

    public boolean isDaemon() {
        return this.delegate instanceof SchedulingAwareRunnable && ((SchedulingAwareRunnable)this.delegate).isLongLived();
    }

    public void release() {
    }
}

