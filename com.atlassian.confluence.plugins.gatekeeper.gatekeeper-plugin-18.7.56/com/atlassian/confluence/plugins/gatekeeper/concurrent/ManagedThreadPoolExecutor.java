/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gatekeeper.concurrent;

import com.atlassian.confluence.plugins.gatekeeper.concurrent.ManagedFutureTask;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagedThreadPoolExecutor<C extends Callable<T>, T>
extends ThreadPoolExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ManagedThreadPoolExecutor.class);
    private static final String ID_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int ID_CHARS_LENGTH = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".length();
    private static final int ID_LENGTH = 32;
    private static final long DEFAULT_TASK_EXPIRE_TIME_MS = 300000L;
    private static final long DEFAULT_TASK_CLEANUP_INTERVAL_MS = 60000L;
    private final SecureRandom random = new SecureRandom();
    private long taskExpireTime = 300000L;
    private long taskCleanupInterval = 60000L;
    private long lastCleanupTime;
    private ConcurrentHashMap<String, C> callableMap = new ConcurrentHashMap(1);
    private ConcurrentHashMap<String, ManagedFutureTask<T>> futureMap = new ConcurrentHashMap(1);
    private ConcurrentHashMap<String, Long> expireMap = new ConcurrentHashMap(1);

    public ManagedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, int threadKeepAliveTimeSeconds) {
        super(corePoolSize, maximumPoolSize, threadKeepAliveTimeSeconds, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        this.lastCleanupTime = System.currentTimeMillis();
    }

    public String queue(C task) {
        String id = this.generateId();
        ManagedFutureTask futureTask = new ManagedFutureTask(id, task);
        logger.debug("Submitted task: {}", futureTask);
        super.execute(futureTask);
        this.callableMap.put(id, task);
        this.futureMap.put(id, futureTask);
        return id;
    }

    public C getTask(String id) {
        return (C)(id != null ? (Callable)this.callableMap.get(id) : null);
    }

    public FutureTask<T> getFuture(String id) {
        return id != null ? (FutureTask)this.futureMap.get(id) : null;
    }

    public void remove(String id) {
        this.expireMap.remove(id);
        this.callableMap.remove(id);
        this.futureMap.remove(id);
    }

    private String generateId() {
        String result = null;
        boolean generate = true;
        StringBuilder sb = new StringBuilder(0);
        while (generate) {
            sb.setLength(0);
            for (int i = 0; i < 32; ++i) {
                sb.append(ID_CHARS.charAt(this.random.nextInt(ID_CHARS_LENGTH)));
            }
            result = sb.toString();
            generate = this.futureMap.containsKey(result);
        }
        return result;
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        String id = ((ManagedFutureTask)r).getId();
        this.expireMap.put(id, System.currentTimeMillis() + this.taskExpireTime);
    }

    public void cleanup() {
        long time = System.currentTimeMillis();
        if (time < this.lastCleanupTime + this.taskCleanupInterval) {
            return;
        }
        this.lastCleanupTime = time;
        this.expireMap.entrySet().stream().filter(entry -> entry.getValue() != null && time > (Long)entry.getValue()).map(Map.Entry::getKey).collect(Collectors.toList()).forEach(this::remove);
    }

    public long getTaskExpireTime() {
        return this.taskExpireTime;
    }

    public void setTaskExpireTime(long taskExpireTimeSeconds) {
        this.taskExpireTime = taskExpireTimeSeconds * 1000L;
    }

    public long getTaskCleanupInterval() {
        return this.taskCleanupInterval;
    }

    public void setTaskCleanupInterval(long taskCleanupIntervalSeconds) {
        this.taskCleanupInterval = taskCleanupIntervalSeconds * 1000L;
    }
}

