/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.Header
 *  org.apache.hc.core5.http.HttpMessage
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.MultiValueMap
 */
package org.springframework.http.client.reactive;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

class HttpComponentsHeadersAdapter
implements MultiValueMap<String, String> {
    private final HttpMessage message;

    HttpComponentsHeadersAdapter(HttpMessage message) {
        this.message = message;
    }

    @Nullable
    public String getFirst(String key) {
        Header header = this.message.getFirstHeader(key);
        return header != null ? header.getValue() : null;
    }

    public void add(String key, @Nullable String value) {
        this.message.addHeader(key, (Object)value);
    }

    public void addAll(String key, List<? extends String> values) {
        values.forEach(value -> this.add(key, (String)value));
    }

    public void addAll(MultiValueMap<String, String> values) {
        values.forEach(this::addAll);
    }

    public void set(String key, @Nullable String value) {
        this.message.setHeader(key, (Object)value);
    }

    public void setAll(Map<String, String> values) {
        values.forEach(this::set);
    }

    public Map<String, String> toSingleValueMap() {
        LinkedHashMap map = CollectionUtils.newLinkedHashMap((int)this.size());
        this.message.headerIterator().forEachRemaining(h -> map.putIfAbsent(h.getName(), h.getValue()));
        return map;
    }

    public int size() {
        return this.message.getHeaders().length;
    }

    public boolean isEmpty() {
        return this.message.getHeaders().length == 0;
    }

    public boolean containsKey(Object key) {
        return key instanceof String && this.message.containsHeader((String)key);
    }

    public boolean containsValue(Object value) {
        return value instanceof String && Arrays.stream(this.message.getHeaders()).anyMatch(h -> h.getValue().equals(value));
    }

    @Nullable
    public List<String> get(Object key) {
        ArrayList<String> values = null;
        if (this.containsKey(key)) {
            Header[] headers = this.message.getHeaders((String)key);
            values = new ArrayList<String>(headers.length);
            for (Header header : headers) {
                values.add(header.getValue());
            }
        }
        return values;
    }

    @Nullable
    public List<String> put(String key, List<String> values) {
        Object oldValues = this.remove(key);
        values.forEach(value -> this.add(key, (String)value));
        return oldValues;
    }

    @Nullable
    public List<String> remove(Object key) {
        if (key instanceof String) {
            Object oldValues = this.get(key);
            this.message.removeHeaders((String)key);
            return oldValues;
        }
        return null;
    }

    public void putAll(Map<? extends String, ? extends List<String>> map) {
        map.forEach(this::put);
    }

    public void clear() {
        this.message.setHeaders(new Header[0]);
    }

    public Set<String> keySet() {
        LinkedHashSet<String> keys = new LinkedHashSet<String>(this.size());
        for (Header header : this.message.getHeaders()) {
            keys.add(header.getName());
        }
        return keys;
    }

    public Collection<List<String>> values() {
        ArrayList<List<String>> values = new ArrayList<List<String>>(this.size());
        for (Header header : this.message.getHeaders()) {
            values.add((List<String>)this.get(header.getName()));
        }
        return values;
    }

    public Set<Map.Entry<String, List<String>>> entrySet() {
        return new AbstractSet<Map.Entry<String, List<String>>>(){

            @Override
            public Iterator<Map.Entry<String, List<String>>> iterator() {
                return new EntryIterator();
            }

            @Override
            public int size() {
                return HttpComponentsHeadersAdapter.this.size();
            }
        };
    }

    public String toString() {
        return HttpHeaders.formatHeaders(this);
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
            List<String> values = HttpComponentsHeadersAdapter.this.get(this.key);
            return values != null ? values : Collections.emptyList();
        }

        @Override
        public List<String> setValue(List<String> value) {
            Object previousValues = this.getValue();
            HttpComponentsHeadersAdapter.this.put(this.key, value);
            return previousValues;
        }
    }

    private class EntryIterator
    implements Iterator<Map.Entry<String, List<String>>> {
        private final Iterator<Header> iterator;

        private EntryIterator() {
            this.iterator = HttpComponentsHeadersAdapter.this.message.headerIterator();
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public Map.Entry<String, List<String>> next() {
            return new HeaderEntry(this.iterator.next().getName());
        }
    }
}

