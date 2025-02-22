/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.ThreadContext
 *  org.slf4j.spi.MDCAdapter
 */
package org.apache.logging.slf4j;

import java.util.Map;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.spi.MDCAdapter;

public class Log4jMDCAdapter
implements MDCAdapter {
    public void put(String key, String val) {
        ThreadContext.put((String)key, (String)val);
    }

    public String get(String key) {
        return ThreadContext.get((String)key);
    }

    public void remove(String key) {
        ThreadContext.remove((String)key);
    }

    public void clear() {
        ThreadContext.clearMap();
    }

    public Map<String, String> getCopyOfContextMap() {
        return ThreadContext.getContext();
    }

    public void setContextMap(Map<String, String> map) {
        ThreadContext.clearMap();
        ThreadContext.putAll(map);
    }
}

