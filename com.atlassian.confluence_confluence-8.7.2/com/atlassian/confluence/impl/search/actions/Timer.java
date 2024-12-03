/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.search.actions;

import java.io.Serializable;
import java.util.LinkedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Timer
implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(Timer.class);
    private LinkedHashMap map = new LinkedHashMap();

    public void start(String s) {
        if (this.map.containsKey(s)) {
            this.map.remove(s);
        }
        TimeEntry entry = new TimeEntry();
        entry.start = System.currentTimeMillis();
        this.map.put(s, entry);
    }

    public void stop(String s) {
        TimeEntry entry = (TimeEntry)this.map.get(s);
        if (entry != null) {
            entry.stop = System.currentTimeMillis();
            log.debug(s + " : ");
            log.debug(entry.stop - entry.start + " ms\n");
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("**********************\n");
        for (Object o : this.map.keySet()) {
            String key = (String)o;
            sb.append(key).append(" : ");
            TimeEntry entry = (TimeEntry)this.map.get(key);
            sb.append(entry.stop - entry.start).append(" ms\n");
        }
        sb.append("**********************");
        return sb.toString();
    }

    static class TimeEntry
    implements Serializable {
        long start;
        long stop;

        TimeEntry() {
        }
    }
}

