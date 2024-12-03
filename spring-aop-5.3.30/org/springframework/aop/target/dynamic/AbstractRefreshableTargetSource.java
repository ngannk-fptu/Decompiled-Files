/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 */
package org.springframework.aop.target.dynamic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.TargetSource;
import org.springframework.aop.target.dynamic.Refreshable;
import org.springframework.lang.Nullable;

public abstract class AbstractRefreshableTargetSource
implements TargetSource,
Refreshable {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    protected Object targetObject;
    private long refreshCheckDelay = -1L;
    private long lastRefreshCheck = -1L;
    private long lastRefreshTime = -1L;
    private long refreshCount = 0L;

    public void setRefreshCheckDelay(long refreshCheckDelay) {
        this.refreshCheckDelay = refreshCheckDelay;
    }

    @Override
    public synchronized Class<?> getTargetClass() {
        if (this.targetObject == null) {
            this.refresh();
        }
        return this.targetObject.getClass();
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    @Nullable
    public final synchronized Object getTarget() {
        if (this.refreshCheckDelayElapsed() && this.requiresRefresh() || this.targetObject == null) {
            this.refresh();
        }
        return this.targetObject;
    }

    @Override
    public void releaseTarget(Object object) {
    }

    @Override
    public final synchronized void refresh() {
        this.logger.debug((Object)"Attempting to refresh target");
        this.targetObject = this.freshTarget();
        ++this.refreshCount;
        this.lastRefreshTime = System.currentTimeMillis();
        this.logger.debug((Object)"Target refreshed successfully");
    }

    @Override
    public synchronized long getRefreshCount() {
        return this.refreshCount;
    }

    @Override
    public synchronized long getLastRefreshTime() {
        return this.lastRefreshTime;
    }

    private boolean refreshCheckDelayElapsed() {
        if (this.refreshCheckDelay < 0L) {
            return false;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (this.lastRefreshCheck < 0L || currentTimeMillis - this.lastRefreshCheck > this.refreshCheckDelay) {
            this.lastRefreshCheck = currentTimeMillis;
            this.logger.debug((Object)"Refresh check delay elapsed - checking whether refresh is required");
            return true;
        }
        return false;
    }

    protected boolean requiresRefresh() {
        return true;
    }

    protected abstract Object freshTarget();
}

