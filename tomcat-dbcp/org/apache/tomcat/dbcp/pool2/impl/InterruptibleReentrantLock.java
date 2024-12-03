/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.pool2.impl;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class InterruptibleReentrantLock
extends ReentrantLock {
    private static final long serialVersionUID = 1L;

    InterruptibleReentrantLock(boolean fairness) {
        super(fairness);
    }

    public void interruptWaiters(Condition condition) {
        this.getWaitingThreads(condition).forEach(Thread::interrupt);
    }
}

