/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.util;

import java.util.LinkedHashMap;
import java.util.Map;

public final class InternCache
extends LinkedHashMap<String, String> {
    private static final int DEFAULT_SIZE = 64;
    private static final int MAX_SIZE = 660;
    private static final InternCache sInstance = new InternCache();

    private InternCache() {
        super(64, 0.6666f, false);
    }

    public static InternCache getInstance() {
        return sInstance;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String intern(String input) {
        String result;
        InternCache internCache = this;
        synchronized (internCache) {
            result = (String)this.get(input);
        }
        if (result == null) {
            result = input.intern();
            internCache = this;
            synchronized (internCache) {
                this.put(result, result);
            }
        }
        return result;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
        return this.size() > 660;
    }
}

