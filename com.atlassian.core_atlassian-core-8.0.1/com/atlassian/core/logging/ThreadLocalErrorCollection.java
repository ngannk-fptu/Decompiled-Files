/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.spi.LoggingEvent
 */
package com.atlassian.core.logging;

import com.atlassian.core.logging.DatedLoggingEvent;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.spi.LoggingEvent;

@Deprecated
public class ThreadLocalErrorCollection {
    public static final int DEFAULT_LIMIT = 100;
    private static final ThreadLocal threadLocalCollection = new ThreadLocal(){

        protected Object initialValue() {
            return new LinkedList();
        }
    };
    private static final ThreadLocal threadLocalEnabled = new ThreadLocal();
    private static int limit = 100;

    public static void add(long timeInMillis, LoggingEvent e) {
        if (!ThreadLocalErrorCollection.isEnabled()) {
            return;
        }
        List loggingEvents = ThreadLocalErrorCollection.getList();
        loggingEvents.add(new DatedLoggingEvent(timeInMillis, e));
        while (loggingEvents.size() > limit) {
            loggingEvents.remove(0);
        }
    }

    public static void clear() {
        threadLocalCollection.remove();
    }

    public static List getList() {
        List list = (List)threadLocalCollection.get();
        return list;
    }

    public static boolean isEmpty() {
        return ThreadLocalErrorCollection.getList().isEmpty();
    }

    public static int getLimit() {
        return limit;
    }

    public static void setLimit(int limit) {
        ThreadLocalErrorCollection.limit = limit;
    }

    public static boolean isEnabled() {
        Boolean enabledState = (Boolean)threadLocalEnabled.get();
        return Boolean.TRUE == enabledState;
    }

    public static void enable() {
        threadLocalEnabled.set(Boolean.TRUE);
    }

    public static void disable() {
        threadLocalEnabled.remove();
    }
}

