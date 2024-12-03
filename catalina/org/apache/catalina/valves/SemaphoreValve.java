/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 */
package org.apache.catalina.valves;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import javax.servlet.ServletException;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

public class SemaphoreValve
extends ValveBase {
    protected Semaphore semaphore = null;
    protected int concurrency = 10;
    protected boolean fairness = false;
    protected boolean block = true;
    protected boolean interruptible = false;

    public SemaphoreValve() {
        super(true);
    }

    public int getConcurrency() {
        return this.concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public boolean getFairness() {
        return this.fairness;
    }

    public void setFairness(boolean fairness) {
        this.fairness = fairness;
    }

    public boolean getBlock() {
        return this.block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public boolean getInterruptible() {
        return this.interruptible;
    }

    public void setInterruptible(boolean interruptible) {
        this.interruptible = interruptible;
    }

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        this.semaphore = new Semaphore(this.concurrency, this.fairness);
        super.startInternal();
    }

    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        super.stopInternal();
        this.semaphore = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        if (this.controlConcurrency(request, response)) {
            boolean shouldRelease = true;
            try {
                if (this.block) {
                    if (this.interruptible) {
                        try {
                            this.semaphore.acquire();
                        }
                        catch (InterruptedException e) {
                            shouldRelease = false;
                            this.permitDenied(request, response);
                            if (shouldRelease) {
                                this.semaphore.release();
                            }
                            return;
                        }
                    } else {
                        this.semaphore.acquireUninterruptibly();
                    }
                } else if (!this.semaphore.tryAcquire()) {
                    shouldRelease = false;
                    this.permitDenied(request, response);
                    return;
                }
                this.getNext().invoke(request, response);
            }
            finally {
                if (shouldRelease) {
                    this.semaphore.release();
                }
            }
        } else {
            this.getNext().invoke(request, response);
        }
    }

    public boolean controlConcurrency(Request request, Response response) {
        return true;
    }

    public void permitDenied(Request request, Response response) throws IOException, ServletException {
    }
}

