/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.ThreadSafe
 */
package com.atlassian.logging.log4j.appender.fluentd;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class LoggingEventQueue<T> {
    private final long maxNumEvents;
    volatile LinkedList<T> loggingEventQueue = new LinkedList();

    public LoggingEventQueue(long maxNumEvents) {
        this.maxNumEvents = maxNumEvents;
    }

    public synchronized void enqueue(T loggingEvent) {
        this.loggingEventQueue.add(loggingEvent);
        while ((long)this.loggingEventQueue.size() > this.maxNumEvents) {
            this.dequeue();
        }
    }

    public synchronized List<T> retrieveLoggingEvents(int maximum) {
        LinkedList<T> events = new LinkedList<T>();
        if (maximum < 0 || maximum >= this.loggingEventQueue.size()) {
            LinkedList<T> currentQueue = this.loggingEventQueue;
            this.loggingEventQueue = new LinkedList();
            return currentQueue;
        }
        for (int remaining = maximum; remaining > 0; --remaining) {
            events.add(this.dequeue());
        }
        return events;
    }

    public synchronized T dequeue() {
        return this.loggingEventQueue.poll();
    }

    public int getSize() {
        return this.loggingEventQueue.size();
    }

    public boolean isFull() {
        return (long)this.getSize() >= this.maxNumEvents;
    }
}

