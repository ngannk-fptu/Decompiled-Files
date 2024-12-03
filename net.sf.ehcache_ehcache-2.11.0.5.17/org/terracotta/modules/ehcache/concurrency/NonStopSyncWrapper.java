/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.ToolkitFeatureType
 *  org.terracotta.toolkit.feature.NonStopFeature
 *  org.terracotta.toolkit.nonstop.NonStopConfiguration
 *  org.terracotta.toolkit.nonstop.NonStopException
 *  org.terracotta.toolkit.rejoin.RejoinException
 */
package org.terracotta.modules.ehcache.concurrency;

import net.sf.ehcache.concurrent.LockType;
import net.sf.ehcache.concurrent.Sync;
import net.sf.ehcache.constructs.nonstop.concurrency.InvalidLockStateAfterRejoinException;
import net.sf.ehcache.constructs.nonstop.concurrency.LockOperationTimedOutNonstopException;
import org.terracotta.modules.ehcache.ToolkitInstanceFactory;
import org.terracotta.modules.ehcache.store.ToolkitNonStopExceptionOnTimeoutConfiguration;
import org.terracotta.toolkit.ToolkitFeatureType;
import org.terracotta.toolkit.feature.NonStopFeature;
import org.terracotta.toolkit.nonstop.NonStopConfiguration;
import org.terracotta.toolkit.nonstop.NonStopException;
import org.terracotta.toolkit.rejoin.RejoinException;

public class NonStopSyncWrapper
implements Sync {
    private final Sync delegate;
    private final NonStopFeature nonStop;
    private final ToolkitNonStopExceptionOnTimeoutConfiguration toolkitNonStopConfiguration;

    public NonStopSyncWrapper(Sync delegate, ToolkitInstanceFactory toolkitInstanceFactory, ToolkitNonStopExceptionOnTimeoutConfiguration toolkitNonStopConfiguration) {
        this.delegate = delegate;
        this.nonStop = (NonStopFeature)toolkitInstanceFactory.getToolkit().getFeature(ToolkitFeatureType.NONSTOP);
        this.toolkitNonStopConfiguration = toolkitNonStopConfiguration;
    }

    @Override
    public void lock(LockType type) {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.delegate.lock(type);
        }
        catch (NonStopException e) {
            throw new LockOperationTimedOutNonstopException("Lock timed out");
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public void unlock(LockType type) {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.delegate.unlock(type);
        }
        catch (RejoinException e) {
            throw new InvalidLockStateAfterRejoinException(e);
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public boolean tryLock(LockType type, long msec) throws InterruptedException {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            boolean bl = this.delegate.tryLock(type, msec);
            return bl;
        }
        catch (NonStopException e) {
            throw new LockOperationTimedOutNonstopException("try lock timed out");
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public boolean isHeldByCurrentThread(LockType type) {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            boolean bl = this.delegate.isHeldByCurrentThread(type);
            return bl;
        }
        catch (NonStopException e) {
            throw new LockOperationTimedOutNonstopException("isHeldByCurrentThread timed out");
        }
        finally {
            this.nonStop.finish();
        }
    }
}

