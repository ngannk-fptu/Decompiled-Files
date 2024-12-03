/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.slf4j;

import java.util.Map;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.spi.MDCAdapter;

public class Log4jMDCAdapter
implements MDCAdapter {
    @Override
    public void put(String key, String val) {
        ThreadContext.put(key, val);
    }

    @Override
    public String get(String key) {
        return ThreadContext.get(key);
    }

    @Override
    public void remove(String key) {
        ThreadContext.remove(key);
    }

    @Override
    public void clear() {
        ThreadContext.clearMap();
    }

    @Override
    public Map<String, String> getCopyOfContextMap() {
        return ThreadContext.getContext();
    }

    public void setContextMap(Map map) {
        ThreadContext.clearMap();
        ThreadContext.putAll(map);
    }
}

