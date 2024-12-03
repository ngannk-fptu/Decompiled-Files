/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.config.Lookup;
import org.apache.hc.core5.util.TextUtils;

@Contract(threading=ThreadingBehavior.SAFE)
public final class Registry<I>
implements Lookup<I> {
    private final Map<String, I> map;

    Registry(Map<String, I> map) {
        this.map = new ConcurrentHashMap<String, I>(map);
    }

    @Override
    public I lookup(String key) {
        if (key == null) {
            return null;
        }
        return this.map.get(TextUtils.toLowerCase(key));
    }

    public String toString() {
        return this.map.toString();
    }
}

