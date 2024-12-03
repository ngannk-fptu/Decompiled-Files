/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.util;

import java.util.LinkedHashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class InternCache
extends LinkedHashMap<String, String> {
    private static final int MAX_ENTRIES = 192;
    public static final InternCache instance = new InternCache();

    private InternCache() {
        super(192, 0.8f, true);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
        return this.size() > 192;
    }

    public synchronized String intern(String input) {
        String result = (String)this.get(input);
        if (result == null) {
            result = input.intern();
            this.put(result, result);
        }
        return result;
    }
}

