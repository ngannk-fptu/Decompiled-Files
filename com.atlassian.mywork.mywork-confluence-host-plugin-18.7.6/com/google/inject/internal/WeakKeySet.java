/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.Key;
import com.google.inject.internal.Errors;
import com.google.inject.internal.util.$Maps;
import com.google.inject.internal.util.$Sets;
import com.google.inject.internal.util.$SourceProvider;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class WeakKeySet {
    private Map<String, Set<Object>> backingSet;

    WeakKeySet() {
    }

    public void add(Key<?> key, Object source) {
        String k;
        Set<Object> sources;
        if (this.backingSet == null) {
            this.backingSet = $Maps.newHashMap();
        }
        if (source instanceof Class || source == $SourceProvider.UNKNOWN_SOURCE) {
            source = null;
        }
        if ((sources = this.backingSet.get(k = key.toString())) == null) {
            sources = $Sets.newLinkedHashSet();
            this.backingSet.put(k, sources);
        }
        sources.add(Errors.convert(source));
    }

    public boolean contains(Key<?> key) {
        return this.backingSet != null && this.backingSet.containsKey(key.toString());
    }

    public Set<Object> getSources(Key<?> key) {
        return this.backingSet.get(key.toString());
    }
}

