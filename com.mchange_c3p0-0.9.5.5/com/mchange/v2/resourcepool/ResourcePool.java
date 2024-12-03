/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v1.util.ClosableResource
 */
package com.mchange.v2.resourcepool;

import com.mchange.v1.util.ClosableResource;
import com.mchange.v2.resourcepool.ResourcePoolException;
import com.mchange.v2.resourcepool.TimeoutException;

public interface ResourcePool
extends ClosableResource {
    public static final int KNOWN_AND_AVAILABLE = 0;
    public static final int KNOWN_AND_CHECKED_OUT = 1;
    public static final int UNKNOWN_OR_PURGED = -1;

    public Object checkoutResource() throws ResourcePoolException, InterruptedException;

    public Object checkoutResource(long var1) throws TimeoutException, ResourcePoolException, InterruptedException;

    public void checkinResource(Object var1) throws ResourcePoolException;

    public void checkinAll() throws ResourcePoolException;

    public int statusInPool(Object var1) throws ResourcePoolException;

    public void markBroken(Object var1) throws ResourcePoolException;

    public int getMinPoolSize() throws ResourcePoolException;

    public int getMaxPoolSize() throws ResourcePoolException;

    public int getPoolSize() throws ResourcePoolException;

    public void setPoolSize(int var1) throws ResourcePoolException;

    public int getAvailableCount() throws ResourcePoolException;

    public int getExcludedCount() throws ResourcePoolException;

    public int getAwaitingCheckinCount() throws ResourcePoolException;

    public int getAwaitingCheckinNotExcludedCount() throws ResourcePoolException;

    public long getEffectiveExpirationEnforcementDelay() throws ResourcePoolException;

    public long getStartTime() throws ResourcePoolException;

    public long getUpTime() throws ResourcePoolException;

    public long getNumFailedCheckins() throws ResourcePoolException;

    public long getNumFailedCheckouts() throws ResourcePoolException;

    public long getNumFailedIdleTests() throws ResourcePoolException;

    public int getNumCheckoutWaiters() throws ResourcePoolException;

    public Throwable getLastAcquisitionFailure() throws ResourcePoolException;

    public Throwable getLastCheckinFailure() throws ResourcePoolException;

    public Throwable getLastCheckoutFailure() throws ResourcePoolException;

    public Throwable getLastIdleCheckFailure() throws ResourcePoolException;

    public Throwable getLastResourceTestFailure() throws ResourcePoolException;

    public void resetPool() throws ResourcePoolException;

    public void close() throws ResourcePoolException;

    public void close(boolean var1) throws ResourcePoolException;

    public static interface Manager {
        public Object acquireResource() throws Exception;

        public void refurbishIdleResource(Object var1) throws Exception;

        public void refurbishResourceOnCheckout(Object var1) throws Exception;

        public void refurbishResourceOnCheckin(Object var1) throws Exception;

        public void destroyResource(Object var1, boolean var2) throws Exception;
    }
}

