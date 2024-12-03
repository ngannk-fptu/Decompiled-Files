/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.aop.target;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.TargetSource;
import org.springframework.lang.Nullable;

public abstract class AbstractLazyCreationTargetSource
implements TargetSource {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private Object lazyTarget;

    public synchronized boolean isInitialized() {
        return this.lazyTarget != null;
    }

    @Override
    @Nullable
    public synchronized Class<?> getTargetClass() {
        return this.lazyTarget != null ? this.lazyTarget.getClass() : null;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public synchronized Object getTarget() throws Exception {
        if (this.lazyTarget == null) {
            this.logger.debug((Object)"Initializing lazy target object");
            this.lazyTarget = this.createObject();
        }
        return this.lazyTarget;
    }

    @Override
    public void releaseTarget(Object target) throws Exception {
    }

    protected abstract Object createObject() throws Exception;
}

