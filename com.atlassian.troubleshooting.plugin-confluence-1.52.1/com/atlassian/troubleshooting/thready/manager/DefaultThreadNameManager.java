/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.MapMaker
 */
package com.atlassian.troubleshooting.thready.manager;

import com.atlassian.troubleshooting.thready.manager.ThreadAttributes;
import com.atlassian.troubleshooting.thready.manager.ThreadNameManager;
import com.google.common.collect.MapMaker;
import java.util.Map;

public class DefaultThreadNameManager
implements ThreadNameManager {
    private final Map<Thread, ThreadAttributes> threadAttributes = new MapMaker().weakKeys().makeMap();

    @Override
    public boolean isUnchanged() {
        ThreadAttributes attributes = this.threadAttributes.get(Thread.currentThread());
        return attributes == null || attributes.isEmpty();
    }

    @Override
    public void setThreadName() {
        Thread.currentThread().setName(this.getThreadAttributes().getThreadName());
    }

    @Override
    public void addThreadAttribute(String key, String value) {
        this.getThreadAttributes().add(key, value);
    }

    @Override
    public void putThreadAttribute(String key, String value) {
        this.getThreadAttributes().put(key, value);
    }

    @Override
    public void clearThreadAttributes() {
        this.getThreadAttributes().clearAll();
    }

    private ThreadAttributes getThreadAttributes() {
        return this.threadAttributes.computeIfAbsent(Thread.currentThread(), id -> new ThreadAttributes(Thread.currentThread().getName()));
    }
}

