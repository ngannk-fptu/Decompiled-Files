/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.codec;

import io.netty.handler.codec.Headers;
import io.netty.util.internal.ObjectUtil;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class HeadersUtils {
    private HeadersUtils() {
    }

    public static <K, V> List<String> getAllAsString(Headers<K, V, ?> headers, K name) {
        final List<V> allNames = headers.getAll(name);
        return new AbstractList<String>(){

            @Override
            public String get(int index) {
                Object value = allNames.get(index);
                return value != null ? value.toString() : null;
            }

            @Override
            public int size() {
                return allNames.size();
            }
        };
    }

    public static <K, V> String getAsString(Headers<K, V, ?> headers, K name) {
        V orig = headers.get(name);
        return orig != null ? orig.toString() : null;
    }

    public static Iterator<Map.Entry<String, String>> iteratorAsString(Iterable<Map.Entry<CharSequence, CharSequence>> headers) {
        return new StringEntryIterator(headers.iterator());
    }

    public static <K, V> String toString(Class<?> headersClass, Iterator<Map.Entry<K, V>> headersIt, int size) {
        String simpleName = headersClass.getSimpleName();
        if (size == 0) {
            return simpleName + "[]";
        }
        StringBuilder sb = new StringBuilder(simpleName.length() + 2 + size * 20).append(simpleName).append('[');
        while (headersIt.hasNext()) {
            Map.Entry<K, V> header = headersIt.next();
            sb.append(header.getKey()).append(": ").append(header.getValue()).append(", ");
        }
        sb.setLength(sb.length() - 2);
        return sb.append(']').toString();
    }

    public static Set<String> namesAsString(Headers<CharSequence, CharSequence, ?> headers) {
        return new DelegatingNameSet(headers);
    }

    private static final class DelegatingNameSet
    extends AbstractCollection<String>
    implements Set<String> {
        private final Headers<CharSequence, CharSequence, ?> headers;

        DelegatingNameSet(Headers<CharSequence, CharSequence, ?> headers) {
            this.headers = (Headers)ObjectUtil.checkNotNull(headers, (String)"headers");
        }

        @Override
        public int size() {
            return this.headers.names().size();
        }

        @Override
        public boolean isEmpty() {
            return this.headers.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return this.headers.contains(o.toString());
        }

        @Override
        public Iterator<String> iterator() {
            return new StringIterator<CharSequence>(this.headers.names().iterator());
        }
    }

    private static final class StringIterator<T>
    implements Iterator<String> {
        private final Iterator<T> iter;

        StringIterator(Iterator<T> iter) {
            this.iter = iter;
        }

        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }

        @Override
        public String next() {
            T next = this.iter.next();
            return next != null ? next.toString() : null;
        }

        @Override
        public void remove() {
            this.iter.remove();
        }
    }

    private static final class StringEntry
    implements Map.Entry<String, String> {
        private final Map.Entry<CharSequence, CharSequence> entry;
        private String name;
        private String value;

        StringEntry(Map.Entry<CharSequence, CharSequence> entry) {
            this.entry = entry;
        }

        @Override
        public String getKey() {
            if (this.name == null) {
                this.name = this.entry.getKey().toString();
            }
            return this.name;
        }

        @Override
        public String getValue() {
            if (this.value == null && this.entry.getValue() != null) {
                this.value = this.entry.getValue().toString();
            }
            return this.value;
        }

        @Override
        public String setValue(String value) {
            String old = this.getValue();
            this.entry.setValue(value);
            return old;
        }

        public String toString() {
            return this.entry.toString();
        }
    }

    private static final class StringEntryIterator
    implements Iterator<Map.Entry<String, String>> {
        private final Iterator<Map.Entry<CharSequence, CharSequence>> iter;

        StringEntryIterator(Iterator<Map.Entry<CharSequence, CharSequence>> iter) {
            this.iter = iter;
        }

        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }

        @Override
        public Map.Entry<String, String> next() {
            return new StringEntry(this.iter.next());
        }

        @Override
        public void remove() {
            this.iter.remove();
        }
    }
}

