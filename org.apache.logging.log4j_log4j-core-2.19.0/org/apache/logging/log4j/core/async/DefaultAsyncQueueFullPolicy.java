/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 */
package org.apache.logging.log4j.core.async;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.async.AsyncQueueFullPolicy;
import org.apache.logging.log4j.core.async.EventRoute;
import org.apache.logging.log4j.core.util.Log4jThread;

public class DefaultAsyncQueueFullPolicy
implements AsyncQueueFullPolicy {
    @Override
    public EventRoute getRoute(long backgroundThreadId, Level level) {
        Thread currentThread = Thread.currentThread();
        if (currentThread.getId() == backgroundThreadId || currentThread instanceof Log4jThread) {
            return EventRoute.SYNCHRONOUS;
        }
        return EventRoute.ENQUEUE;
    }
}

