/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.spi.work.Work
 *  org.springframework.util.Assert
 */
package org.springframework.jca.work;

import javax.resource.spi.work.Work;
import org.springframework.util.Assert;

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

    public void release() {
    }
}

