/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.async.AsynchronousRunner
 *  com.mchange.v2.async.Queuable
 *  com.mchange.v2.async.RunnableQueue
 */
package com.mchange.v2.resourcepool;

import com.mchange.v2.async.AsynchronousRunner;
import com.mchange.v2.async.Queuable;
import com.mchange.v2.async.RunnableQueue;
import com.mchange.v2.resourcepool.BasicResourcePoolFactory;
import com.mchange.v2.resourcepool.ResourcePool;
import com.mchange.v2.resourcepool.ResourcePoolException;
import java.util.Timer;

public abstract class ResourcePoolFactory {
    static final ResourcePoolFactory SHARED_INSTANCE = new BasicResourcePoolFactory();
    static final int DEFAULT_NUM_TASK_THREADS = 3;

    public static ResourcePoolFactory getSharedInstance() throws ResourcePoolException {
        return SHARED_INSTANCE;
    }

    public static ResourcePoolFactory createInstance() {
        return new BasicResourcePoolFactory();
    }

    public static ResourcePoolFactory createInstance(int num_task_threads) {
        return new BasicResourcePoolFactory(num_task_threads);
    }

    public static ResourcePoolFactory createInstance(AsynchronousRunner taskRunner, RunnableQueue asyncEventQueue, Timer cullTimer) {
        return new BasicResourcePoolFactory(taskRunner, asyncEventQueue, cullTimer);
    }

    public static ResourcePoolFactory createInstance(Queuable taskRunnerEventQueue, Timer cullTimer) {
        return ResourcePoolFactory.createInstance((AsynchronousRunner)taskRunnerEventQueue, taskRunnerEventQueue == null ? null : taskRunnerEventQueue.asRunnableQueue(), cullTimer);
    }

    public abstract void setMin(int var1) throws ResourcePoolException;

    public abstract int getMin() throws ResourcePoolException;

    public abstract void setMax(int var1) throws ResourcePoolException;

    public abstract int getStart() throws ResourcePoolException;

    public abstract void setStart(int var1) throws ResourcePoolException;

    public abstract int getMax() throws ResourcePoolException;

    public abstract void setIncrement(int var1) throws ResourcePoolException;

    public abstract int getIncrement() throws ResourcePoolException;

    public abstract void setAcquisitionRetryAttempts(int var1) throws ResourcePoolException;

    public abstract int getAcquisitionRetryAttempts() throws ResourcePoolException;

    public abstract void setAcquisitionRetryDelay(int var1) throws ResourcePoolException;

    public abstract int getAcquisitionRetryDelay() throws ResourcePoolException;

    public abstract void setIdleResourceTestPeriod(long var1) throws ResourcePoolException;

    public abstract long getIdleResourceTestPeriod() throws ResourcePoolException;

    public abstract void setResourceMaxAge(long var1) throws ResourcePoolException;

    public abstract long getResourceMaxAge() throws ResourcePoolException;

    public abstract void setResourceMaxIdleTime(long var1) throws ResourcePoolException;

    public abstract long getResourceMaxIdleTime() throws ResourcePoolException;

    public abstract void setExcessResourceMaxIdleTime(long var1) throws ResourcePoolException;

    public abstract long getExcessResourceMaxIdleTime() throws ResourcePoolException;

    public abstract long getDestroyOverdueResourceTime() throws ResourcePoolException;

    public abstract void setDestroyOverdueResourceTime(long var1) throws ResourcePoolException;

    public abstract void setExpirationEnforcementDelay(long var1) throws ResourcePoolException;

    public abstract long getExpirationEnforcementDelay() throws ResourcePoolException;

    public abstract void setBreakOnAcquisitionFailure(boolean var1) throws ResourcePoolException;

    public abstract boolean getBreakOnAcquisitionFailure() throws ResourcePoolException;

    public abstract void setDebugStoreCheckoutStackTrace(boolean var1) throws ResourcePoolException;

    public abstract boolean getDebugStoreCheckoutStackTrace() throws ResourcePoolException;

    public abstract void setForceSynchronousCheckins(boolean var1) throws ResourcePoolException;

    public abstract boolean getForceSynchronousCheckins() throws ResourcePoolException;

    public abstract ResourcePool createPool(ResourcePool.Manager var1) throws ResourcePoolException;
}

