/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.convert;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.configuration2.convert.AbstractListDelimiterHandler;
import org.apache.commons.configuration2.convert.ValueTransformer;

public interface ListDelimiterHandler {
    public static final ValueTransformer NOOP_TRANSFORMER = value -> value;

    public Object escape(Object var1, ValueTransformer var2);

    public Object escapeList(List<?> var1, ValueTransformer var2);

    public Iterable<?> parse(Object var1);

    public Collection<String> split(String var1, boolean var2);

    default public Collection<?> flatten(Object value, int limit) {
        if (value instanceof String) {
            return this.split((String)value, true);
        }
        LinkedList<Object> result = new LinkedList<Object>();
        if (value instanceof Iterable) {
            AbstractListDelimiterHandler.flattenIterator(this, result, ((Iterable)value).iterator(), limit);
        } else if (value instanceof Iterator) {
            AbstractListDelimiterHandler.flattenIterator(this, result, (Iterator)value, limit);
        } else if (value != null) {
            if (value.getClass().isArray()) {
                int len = Array.getLength(value);
                int size = 0;
                for (int idx = 0; idx < len && size < limit; ++idx) {
                    result.addAll(this.flatten(Array.get(value, idx), limit - size));
                    size = result.size();
                }
            } else {
                result.add(value);
            }
        }
        return result;
    }
}

