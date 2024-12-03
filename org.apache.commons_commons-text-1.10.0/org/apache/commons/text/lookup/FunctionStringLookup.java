/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.lookup;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.apache.commons.text.lookup.AbstractStringLookup;
import org.apache.commons.text.lookup.StringLookupFactory;

final class FunctionStringLookup<V>
extends AbstractStringLookup {
    private final Function<String, V> function;

    static <R> FunctionStringLookup<R> on(Function<String, R> function) {
        return new FunctionStringLookup<R>(function);
    }

    static <V> FunctionStringLookup<V> on(Map<String, V> map) {
        return FunctionStringLookup.on(StringLookupFactory.toMap(map)::get);
    }

    private FunctionStringLookup(Function<String, V> function) {
        this.function = function;
    }

    @Override
    public String lookup(String key) {
        V obj;
        if (this.function == null) {
            return null;
        }
        try {
            obj = this.function.apply(key);
        }
        catch (IllegalArgumentException | NullPointerException | SecurityException e) {
            return null;
        }
        return Objects.toString(obj, null);
    }

    public String toString() {
        return super.toString() + " [function=" + this.function + "]";
    }
}

