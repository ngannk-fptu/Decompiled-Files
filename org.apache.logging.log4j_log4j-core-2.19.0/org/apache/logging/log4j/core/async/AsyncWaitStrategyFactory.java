/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.lmax.disruptor.WaitStrategy
 */
package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.WaitStrategy;

public interface AsyncWaitStrategyFactory {
    public WaitStrategy createWaitStrategy();
}

