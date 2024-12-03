/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.lifecycle;

import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.upm.lifecycle.UpmLifecycleManager;
import com.atlassian.upm.lifecycle.UpmProductDataStartupComponent;
import com.atlassian.upm.lifecycle.UpmUntenantedStartupComponent;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpmLifecycleManagerImpl
implements UpmLifecycleManager,
LifecycleAware {
    private static final Logger logger = LoggerFactory.getLogger(UpmLifecycleManagerImpl.class);
    private final List<UpmProductDataStartupComponent> productDataStartupComponents;
    private final List<UpmUntenantedStartupComponent> untenantedStartupComponents;
    private final AtomicBoolean gotOnStart = new AtomicBoolean(false);

    public UpmLifecycleManagerImpl(List<UpmProductDataStartupComponent> productDataStartupComponents, List<UpmUntenantedStartupComponent> untenantedStartupComponents) {
        this.productDataStartupComponents = Collections.unmodifiableList(productDataStartupComponents);
        this.untenantedStartupComponents = Collections.unmodifiableList(untenantedStartupComponents);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void ensureStarted() {
        boolean newlyStarted;
        UpmLifecycleManagerImpl upmLifecycleManagerImpl = this;
        synchronized (upmLifecycleManagerImpl) {
            newlyStarted = this.gotOnStart.compareAndSet(false, true);
        }
        if (newlyStarted) {
            this.triggerUntenantedStartupComponents();
            this.triggerProductDataStartupComponents();
        }
    }

    public void onStart() {
        logger.debug("received LifecycleAware.onStart");
        this.ensureStarted();
    }

    public void onStop() {
        logger.debug("received LifecycleAware.onStop");
    }

    private void triggerUntenantedStartupComponents() {
        logger.debug("triggering UpmUntenantedStartupComponents");
        for (UpmUntenantedStartupComponent c : this.untenantedStartupComponents) {
            logger.debug("triggering " + c.getClass());
            c.onStartupWithoutProductData();
        }
    }

    private void triggerProductDataStartupComponents() {
        logger.debug("triggering UpmProductDataStartupComponents");
        for (UpmProductDataStartupComponent c : this.productDataStartupComponents) {
            logger.debug("triggering " + c.getClass());
            c.onStartupWithProductData();
        }
    }
}

