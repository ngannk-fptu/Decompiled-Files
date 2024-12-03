/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.lookup;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import org.apache.commons.text.lookup.BiStringLookup;

final class BiFunctionStringLookup<P, R>
implements BiStringLookup<P> {
    private final BiFunction<String, P, R> biFunction;

    static <U, T> BiFunctionStringLookup<U, T> on(BiFunction<String, U, T> biFunction) {
        return new BiFunctionStringLookup<U, T>(biFunction);
    }

    static <U, T> BiFunctionStringLookup<U, T> on(Map<String, T> map) {
        return BiFunctionStringLookup.on((String key, U u) -> map.get(key));
    }

    private BiFunctionStringLookup(BiFunction<String, P, R> biFunction) {
        this.biFunction = biFunction;
    }

    @Override
    public String lookup(String key) {
        return this.lookup(key, (P)null);
    }

    @Override
    public String lookup(String key, P object) {
        R obj;
        if (this.biFunction == null) {
            return null;
        }
        try {
            obj = this.biFunction.apply(key, object);
        }
        catch (IllegalArgumentException | NullPointerException | SecurityException e) {
            return null;
        }
        return Objects.toString(obj, null);
    }

    public String toString() {
        return super.toString() + " [function=" + this.biFunction + "]";
    }
}

