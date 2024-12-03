/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations.internal;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.digester.annotations.FromAnnotationsRuleSet;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class RuleSetCache
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int cacheSize = 255;
    private final float loadFactor = 0.75f;
    private final int capacity;
    private final Map<Class<?>, FromAnnotationsRuleSet> data;

    public RuleSetCache() {
        this.getClass();
        this.capacity = (int)Math.ceil(255.0f / this.loadFactor) + 1;
        this.data = new LinkedHashMap<Class<?>, FromAnnotationsRuleSet>(this.capacity, 0.75f){
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<Class<?>, FromAnnotationsRuleSet> eldest) {
                return this.size() > 255;
            }
        };
    }

    public boolean containsKey(Class<?> key) {
        RuleSetCache.checkKey(key);
        return this.data.containsKey(key);
    }

    public FromAnnotationsRuleSet get(Class<?> key) {
        RuleSetCache.checkKey(key);
        return this.data.get(key);
    }

    public void put(Class<?> key, FromAnnotationsRuleSet value) {
        RuleSetCache.checkKey(key);
        this.data.put(key, value);
    }

    private static void checkKey(Class<?> key) {
        if (key == null) {
            throw new IllegalArgumentException("null keys not supported");
        }
    }
}

