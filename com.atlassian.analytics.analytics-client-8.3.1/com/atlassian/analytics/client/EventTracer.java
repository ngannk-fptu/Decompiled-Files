/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.analytics.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventTracer {
    private static final Logger log = LoggerFactory.getLogger(EventTracer.class);

    public void logEventSourceInfo(Object event) {
        int frame;
        StringBuilder callStack = new StringBuilder();
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int output = 0;
        for (frame = 0; frame < stackTrace.length && !stackTrace[frame].getMethodName().equals("publish"); ++frame) {
        }
        if (frame == stackTrace.length) {
            frame = 2;
        }
        while (frame < stackTrace.length && output < 10) {
            if (output > 0) {
                callStack.append(" ");
            }
            callStack.append(stackTrace[frame].toString());
            ++output;
            ++frame;
        }
        log.debug("Processing event {} from {}", event.getClass(), (Object)callStack);
    }
}

