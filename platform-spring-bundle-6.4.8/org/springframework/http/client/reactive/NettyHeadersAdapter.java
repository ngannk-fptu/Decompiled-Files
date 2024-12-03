/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.handler.codec.http.HttpHeaders
 */
package org.springframework.http.client.reactive;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

class NettyHeadersAdapter
implements MultiValueMap<String, String> {
    private final io.netty.handler.codec.http.HttpHeaders headers;

    NettyHeadersAdapter(io.netty.handler.codec.http.HttpHeaders headers) {
        this.headers = headers;
    }

    @Override
    @Nullable
    public String getFirst(String key) {
        return this.headers.get(key);
    }

    @Override
    public void add(String key, @Nullable String value) {
        if (value != null) {
            this.headers.add(key, (Object)value);
        }
    }

    @Override
    public void addAll(String key, List<? extends String> values) {
        this.headers.add(key, values);
    }

    @Override
    public void addAll(MultiValueMap<String, String> values) {
        values.forEach((arg_0, arg_1) -> ((io.netty.handler.codec.http.HttpHeaders)this.headers).add(arg_0, arg_1));
    }

    @Override
    public void set(String key, @Nullable String value) {
        if (value != null) {
            this.headers.set(key, (Object)value);
        }
    }

    @Override
    public void setAll(Map<String, String> values) {
        values.forEach((arg_0, arg_1) -> ((io.netty.handler.codec.http.HttpHeaders)this.headers).set(arg_0, arg_1));
    }

    @Override
    public Map<String, String> toSingleValueMap() {
        LinkedHashMap<String, String> singleValueMap = CollectionUtils.newLinkedHashMap(this.headers.size());
        this.headers.entries().forEach((? super T entry) -> {
            if (!singleValueMap.containsKey(entry.getKey())) {
                singleValueMap.put((String)entry.getKey(), (String)entry.getValue());
            }
        });
        return singleValueMap;
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
    public boolean containsKey(Object key) {
        return key instanceof String && this.headers.contains((String)key);
    }

    @Override
    public boolean containsValue(Object value) {
        return value instanceof String && this.headers.entries().stream().anyMatch(entry -> value.equals(entry.getValue()));
    }

    @Override
    @Nullable
    public List<String> get(Object key) {
        if (this.containsKey(key)) {
            return this.headers.getAll((String)key);
        }
        return null;
    }

    @Override
    @Nullable
    public List<String> put(String key, @Nullable List<String> value) {
        List previousValues = this.headers.getAll(key);
        this.headers.set(key, value);
        return previousValues;
    }

    @Override
    @Nullable
    public List<String> remove(Object key) {
        if (key instanceof String) {
            List previousValues = this.headers.getAll((String)key);
            this.headers.remove((String)key);
            return previousValues;
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends List<String>> map) {
        map.forEach((arg_0, arg_1) -> ((io.netty.handler.codec.http.HttpHeaders)this.headers).set(arg_0, arg_1));
    }

    @Override
    public void clear() {
        this.headers.clear();
    }

    @Override
    public Set<String> keySet() {
        return new HeaderNames();
    }

    @Override
    public Collection<List<String>> values() {
        return this.headers.names().stream().map(arg_0 -> ((io.netty.handler.codec.http.HttpHeaders)this.headers).getAll(arg_0)).collect(Collectors.toList());
    }

    @Override
    public Set<Map.Entry<String, List<String>>> entrySet() {
        return new AbstractSet<Map.Entry<String, List<String>>>(){

            @Override
            public Iterator<Map.Entry<String, List<String>>> iterator() {
                return new EntryIterator();
            }

            @Override
            public int size() {
                return NettyHeadersAdapter.this.headers.size();
            }
        };
    }

    public String toString() {
        return HttpHeaders.formatHeaders(this);
    }

    private final class HeaderNamesIterator
    implements Iterator<String> {
        private final Iterator<String> iterator;
        @Nullable
        private String currentName;

        private HeaderNamesIterator(Iterator<String> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public String next() {
            this.currentName = this.iterator.next();
            return this.currentName;
        }

        @Override
        public void remove() {
            if (this.currentName == null) {
                throw new IllegalStateException("No current Header in iterator");
            }
            if (!NettyHeadersAdapter.this.headers.contains(this.currentName)) {
                throw new IllegalStateException("Header not present: " + this.currentName);
            }
            NettyHeadersAdapter.this.headers.remove(this.currentName);
        }
    }

    private class HeaderNames
    extends AbstractSet<String> {
        private HeaderNames() {
        }

        @Override
        public Iterator<String> iterator() {
            return new HeaderNamesIterator(NettyHeadersAdapter.this.headers.names().iterator());
        }

        @Override
        public int size() {
            return NettyHeadersAdapter.this.headers.names().size();
        }
    }

    private class HeaderEntry
    implements Map.Entry<String, List<String>> {
        private final String key;

        HeaderEntry(String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public List<String> getValue() {
            return NettyHeadersAdapter.this.headers.getAll(this.key);
        }

        @Override
        public List<String> setValue(List<String> value) {
            List previousValues = NettyHeadersAdapter.this.headers.getAll(this.key);
            NettyHeadersAdapter.this.headers.set(this.key, value);
            return previousValues;
        }
    }

    private class EntryIterator
    implements Iterator<Map.Entry<String, List<String>>> {
        private Iterator<String> names;

        private EntryIterator() {
            this.names = NettyHeadersAdapter.this.headers.names().iterator();
        }

        @Override
        public boolean hasNext() {
            return this.names.hasNext();
        }

        @Override
        public Map.Entry<String, List<String>> next() {
            return new HeaderEntry(this.names.next());
        }
    }
}

