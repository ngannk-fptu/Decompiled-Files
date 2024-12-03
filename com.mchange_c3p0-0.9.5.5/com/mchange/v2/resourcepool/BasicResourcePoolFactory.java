/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.async.AsynchronousRunner
 *  com.mchange.v2.async.CarefulRunnableQueue
 *  com.mchange.v2.async.RunnableQueue
 *  com.mchange.v2.async.ThreadPoolAsynchronousRunner
 */
package com.mchange.v2.resourcepool;

import com.mchange.v2.async.AsynchronousRunner;
import com.mchange.v2.async.CarefulRunnableQueue;
import com.mchange.v2.async.RunnableQueue;
import com.mchange.v2.async.ThreadPoolAsynchronousRunner;
import com.mchange.v2.resourcepool.BasicResourcePool;
import com.mchange.v2.resourcepool.ResourcePool;
import com.mchange.v2.resourcepool.ResourcePoolException;
import com.mchange.v2.resourcepool.ResourcePoolFactory;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;

public class BasicResourcePoolFactory
extends ResourcePoolFactory {
    int start = -1;
    int min = 1;
    int max = 12;
    int inc = 3;
    int retry_attempts = -1;
    int retry_delay = 1000;
    long idle_resource_test_period = -1L;
    long max_age = -1L;
    long max_idle_time = -1L;
    long excess_max_idle_time = -1L;
    long destroy_overdue_resc_time = -1L;
    long expiration_enforcement_delay = -1L;
    boolean break_on_acquisition_failure = true;
    boolean debug_store_checkout_stacktrace = false;
    boolean force_synchronous_checkins = false;
    AsynchronousRunner taskRunner;
    boolean taskRunner_is_external;
    RunnableQueue asyncEventQueue;
    boolean asyncEventQueue_is_external;
    Timer timer;
    boolean timer_is_external;
    int default_num_task_threads;
    Set liveChildren;

    public static BasicResourcePoolFactory createNoEventSupportInstance(int num_task_threads) {
        return BasicResourcePoolFactory.createNoEventSupportInstance(null, null, num_task_threads);
    }

    public static BasicResourcePoolFactory createNoEventSupportInstance(AsynchronousRunner taskRunner, Timer timer) {
        return BasicResourcePoolFactory.createNoEventSupportInstance(taskRunner, timer, 3);
    }

    private static BasicResourcePoolFactory createNoEventSupportInstance(AsynchronousRunner taskRunner, Timer timer, int default_num_task_threads) {
        return new BasicResourcePoolFactory(taskRunner, timer, default_num_task_threads, true);
    }

    BasicResourcePoolFactory() {
        this(null, null, null);
    }

    BasicResourcePoolFactory(AsynchronousRunner taskRunner, RunnableQueue asyncEventQueue, Timer timer) {
        this(taskRunner, asyncEventQueue, timer, 3);
    }

    BasicResourcePoolFactory(int num_task_threads) {
        this(null, null, null, num_task_threads);
    }

    BasicResourcePoolFactory(AsynchronousRunner taskRunner, Timer timer, int default_num_task_threads, boolean no_event_support) {
        this(taskRunner, null, timer, default_num_task_threads);
        if (no_event_support) {
            this.asyncEventQueue_is_external = true;
        }
    }

    BasicResourcePoolFactory(AsynchronousRunner taskRunner, RunnableQueue asyncEventQueue, Timer timer, int default_num_task_threads) {
        this.taskRunner = taskRunner;
        this.taskRunner_is_external = taskRunner != null;
        this.asyncEventQueue = asyncEventQueue;
        this.asyncEventQueue_is_external = asyncEventQueue != null;
        this.timer = timer;
        this.timer_is_external = timer != null;
        this.default_num_task_threads = default_num_task_threads;
    }

    private void createThreadResources() {
        if (!this.taskRunner_is_external) {
            this.taskRunner = new ThreadPoolAsynchronousRunner(this.default_num_task_threads, true);
        }
        if (!this.asyncEventQueue_is_external) {
            this.asyncEventQueue = new CarefulRunnableQueue(true, false);
        }
        if (!this.timer_is_external) {
            this.timer = new Timer(true);
        }
        this.liveChildren = new HashSet();
    }

    private void destroyThreadResources() {
        if (!this.taskRunner_is_external) {
            this.taskRunner.close();
            this.taskRunner = null;
        }
        if (!this.asyncEventQueue_is_external) {
            this.asyncEventQueue.close();
            this.asyncEventQueue = null;
        }
        if (!this.timer_is_external) {
            this.timer.cancel();
            this.timer = null;
        }
        this.liveChildren = null;
    }

    synchronized void markBroken(BasicResourcePool pool) {
        if (this.liveChildren != null) {
            this.liveChildren.remove(pool);
            if (this.liveChildren.isEmpty()) {
                this.destroyThreadResources();
            }
        }
    }

    @Override
    public synchronized void setStart(int start) throws ResourcePoolException {
        this.start = start;
    }

    @Override
    public synchronized int getStart() throws ResourcePoolException {
        return this.start;
    }

    @Override
    public synchronized void setMin(int min) throws ResourcePoolException {
        this.min = min;
    }

    @Override
    public synchronized int getMin() throws ResourcePoolException {
        return this.min;
    }

    @Override
    public synchronized void setMax(int max) throws ResourcePoolException {
        this.max = max;
    }

    @Override
    public synchronized int getMax() throws ResourcePoolException {
        return this.max;
    }

    @Override
    public synchronized void setIncrement(int inc) throws ResourcePoolException {
        this.inc = inc;
    }

    @Override
    public synchronized int getIncrement() throws ResourcePoolException {
        return this.inc;
    }

    @Override
    public synchronized void setAcquisitionRetryAttempts(int retry_attempts) throws ResourcePoolException {
        this.retry_attempts = retry_attempts;
    }

    @Override
    public synchronized int getAcquisitionRetryAttempts() throws ResourcePoolException {
        return this.retry_attempts;
    }

    @Override
    public synchronized void setAcquisitionRetryDelay(int retry_delay) throws ResourcePoolException {
        this.retry_delay = retry_delay;
    }

    @Override
    public synchronized int getAcquisitionRetryDelay() throws ResourcePoolException {
        return this.retry_delay;
    }

    @Override
    public synchronized void setIdleResourceTestPeriod(long test_period) {
        this.idle_resource_test_period = test_period;
    }

    @Override
    public synchronized long getIdleResourceTestPeriod() {
        return this.idle_resource_test_period;
    }

    @Override
    public synchronized void setResourceMaxAge(long max_age) throws ResourcePoolException {
        this.max_age = max_age;
    }

    @Override
    public synchronized long getResourceMaxAge() throws ResourcePoolException {
        return this.max_age;
    }

    @Override
    public synchronized void setResourceMaxIdleTime(long millis) throws ResourcePoolException {
        this.max_idle_time = millis;
    }

    @Override
    public synchronized long getResourceMaxIdleTime() throws ResourcePoolException {
        return this.max_idle_time;
    }

    @Override
    public synchronized void setExcessResourceMaxIdleTime(long millis) throws ResourcePoolException {
        this.excess_max_idle_time = millis;
    }

    @Override
    public synchronized long getExcessResourceMaxIdleTime() throws ResourcePoolException {
        return this.excess_max_idle_time;
    }

    @Override
    public synchronized long getDestroyOverdueResourceTime() throws ResourcePoolException {
        return this.destroy_overdue_resc_time;
    }

    @Override
    public synchronized void setDestroyOverdueResourceTime(long millis) throws ResourcePoolException {
        this.destroy_overdue_resc_time = millis;
    }

    @Override
    public synchronized void setExpirationEnforcementDelay(long expiration_enforcement_delay) throws ResourcePoolException {
        this.expiration_enforcement_delay = expiration_enforcement_delay;
    }

    @Override
    public synchronized long getExpirationEnforcementDelay() throws ResourcePoolException {
        return this.expiration_enforcement_delay;
    }

    @Override
    public synchronized void setBreakOnAcquisitionFailure(boolean break_on_acquisition_failure) throws ResourcePoolException {
        this.break_on_acquisition_failure = break_on_acquisition_failure;
    }

    @Override
    public synchronized boolean getBreakOnAcquisitionFailure() throws ResourcePoolException {
        return this.break_on_acquisition_failure;
    }

    @Override
    public synchronized void setDebugStoreCheckoutStackTrace(boolean debug_store_checkout_stacktrace) throws ResourcePoolException {
        this.debug_store_checkout_stacktrace = debug_store_checkout_stacktrace;
    }

    @Override
    public synchronized boolean getDebugStoreCheckoutStackTrace() throws ResourcePoolException {
        return this.debug_store_checkout_stacktrace;
    }

    @Override
    public synchronized void setForceSynchronousCheckins(boolean force_synchronous_checkins) throws ResourcePoolException {
        this.force_synchronous_checkins = force_synchronous_checkins;
    }

    @Override
    public synchronized boolean getForceSynchronousCheckins() throws ResourcePoolException {
        return this.force_synchronous_checkins;
    }

    @Override
    public synchronized ResourcePool createPool(ResourcePool.Manager mgr) throws ResourcePoolException {
        if (this.liveChildren == null) {
            this.createThreadResources();
        }
        BasicResourcePool child = new BasicResourcePool(mgr, this.start, this.min, this.max, this.inc, this.retry_attempts, this.retry_delay, this.idle_resource_test_period, this.max_age, this.max_idle_time, this.excess_max_idle_time, this.destroy_overdue_resc_time, this.expiration_enforcement_delay, this.break_on_acquisition_failure, this.debug_store_checkout_stacktrace, this.force_synchronous_checkins, this.taskRunner, this.asyncEventQueue, this.timer, this);
        this.liveChildren.add(child);
        return child;
    }
}

