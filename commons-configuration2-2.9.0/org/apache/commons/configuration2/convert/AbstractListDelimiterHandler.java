/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.convert.ValueTransformer;

public abstract class AbstractListDelimiterHandler
implements ListDelimiterHandler {
    static void flattenIterator(ListDelimiterHandler handler, Collection<Object> target, Iterator<?> iterator, int limit) {
        int size = target.size();
        while (size < limit && iterator.hasNext()) {
            target.addAll(handler.flatten(iterator.next(), limit - size));
            size = target.size();
        }
    }

    @Override
    public Object escape(Object value, ValueTransformer transformer) {
        return transformer.transformValue(value instanceof String ? this.escapeString((String)value) : value);
    }

    protected abstract String escapeString(String var1);

    private Collection<?> flatten(Object value) {
        return this.flatten(value, Integer.MAX_VALUE);
    }

    @Override
    public Iterable<?> parse(Object value) {
        return this.flatten(value);
    }

    @Override
    public Collection<String> split(String s, boolean trim) {
        return s == null ? new ArrayList(0) : this.splitString(s, trim);
    }

    protected abstract Collection<String> splitString(String var1, boolean var2);
}

