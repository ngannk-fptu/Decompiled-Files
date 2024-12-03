/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.undertow.util.HeaderMap
 *  io.undertow.util.HeaderValues
 *  io.undertow.util.HttpString
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.MultiValueMap
 */
package org.springframework.http.server.reactive;

import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.HttpString;
import java.util.AbstractSet;
import java.util.ArrayList;
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

class UndertowHeadersAdapter
implements MultiValueMap<String, String> {
    private final HeaderMap headers;

    UndertowHeadersAdapter(HeaderMap headers) {
        this.headers = headers;
    }

    public String getFirst(String key) {
        return this.headers.getFirst(key);
    }

    public void add(String key, @Nullable String value) {
        this.headers.add(HttpString.tryFromString((String)key), value);
    }

    public void addAll(String key, List<? extends String> values) {
        this.headers.addAll(HttpString.tryFromString((String)key), values);
    }

    public void addAll(MultiValueMap<String, String> values) {
        values.forEach((key, list) -> this.headers.addAll(HttpString.tryFromString((String)key), (Collection)list));
    }

    public void set(String key, @Nullable String value) {
        this.headers.put(HttpString.tryFromString((String)key), value);
    }

    public void setAll(Map<String, String> values) {
        values.forEach((key, list) -> this.headers.put(HttpString.tryFromString((String)key), list));
    }

    public Map<String, String> toSingleValueMap() {
        LinkedHashMap singleValueMap = CollectionUtils.newLinkedHashMap((int)this.headers.size());
        this.headers.forEach(values -> singleValueMap.put(values.getHeaderName().toString(), values.getFirst()));
        return singleValueMap;
    }

    public int size() {
        return this.headers.size();
    }

    public boolean isEmpty() {
        return this.headers.size() == 0;
    }

    public boolean containsKey(Object key) {
        return key instanceof String && this.headers.contains((String)key);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean containsValue(Object value) {
        if (!(value instanceof String)) return false;
        if (!this.headers.getHeaderNames().stream().map(arg_0 -> ((HeaderMap)this.headers).get(arg_0)).anyMatch(values -> values.contains(value))) return false;
        return true;
    }

    @Nullable
    public List<String> get(Object key) {
        if (key instanceof String) {
            return this.headers.get((String)key);
        }
        return null;
    }

    @Nullable
    public List<String> put(String key, List<String> value) {
        HeaderValues previousValues = this.headers.get(key);
        this.headers.putAll(HttpString.tryFromString((String)key), value);
        return previousValues;
    }

    @Nullable
    public List<String> remove(Object key) {
        Collection removed;
        if (key instanceof String && (removed = this.headers.remove((String)key)) != null) {
            return new ArrayList<String>(removed);
        }
        return null;
    }

    public void putAll(Map<? extends String, ? extends List<String>> map) {
        map.forEach((key, values) -> this.headers.putAll(HttpString.tryFromString((String)key), (Collection)values));
    }

    public void clear() {
        this.headers.clear();
    }

    public Set<String> keySet() {
        return new HeaderNames();
    }

    public Collection<List<String>> values() {
        return this.headers.getHeaderNames().stream().map(arg_0 -> ((HeaderMap)this.headers).get(arg_0)).collect(Collectors.toList());
    }

    public Set<Map.Entry<String, List<String>>> entrySet() {
        return new AbstractSet<Map.Entry<String, List<String>>>(){

            @Override
            public Iterator<Map.Entry<String, List<String>>> iterator() {
                return new EntryIterator();
            }

            @Override
            public int size() {
                return UndertowHeadersAdapter.this.headers.size();
            }
        };
    }

    public String toString() {
        return HttpHeaders.formatHeaders(this);
    }

    private final class HeaderNamesIterator
    implements Iterator<String> {
        private final Iterator<HttpString> iterator;
        @Nullable
        private String currentName;

        private HeaderNamesIterator(Iterator<HttpString> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public String next() {
            this.currentName = this.iterator.next().toString();
            return this.currentName;
        }

        @Override
        public void remove() {
            if (this.currentName == null) {
                throw new IllegalStateException("No current Header in iterator");
            }
            if (!UndertowHeadersAdapter.this.headers.contains(this.currentName)) {
                throw new IllegalStateException("Header not present: " + this.currentName);
            }
            UndertowHeadersAdapter.this.headers.remove(this.currentName);
        }
    }

    private class HeaderNames
    extends AbstractSet<String> {
        private HeaderNames() {
        }

        @Override
        public Iterator<String> iterator() {
            return new HeaderNamesIterator(UndertowHeadersAdapter.this.headers.getHeaderNames().iterator());
        }

        @Override
        public int size() {
            return UndertowHeadersAdapter.this.headers.getHeaderNames().size();
        }
    }

    private class HeaderEntry
    implements Map.Entry<String, List<String>> {
        private final HttpString key;

        HeaderEntry(HttpString key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return this.key.toString();
        }

        @Override
        public List<String> getValue() {
            return UndertowHeadersAdapter.this.headers.get(this.key);
        }

        @Override
        public List<String> setValue(List<String> value) {
            HeaderValues previousValues = UndertowHeadersAdapter.this.headers.get(this.key);
            UndertowHeadersAdapter.this.headers.putAll(this.key, value);
            return previousValues;
        }
    }

    private class EntryIterator
    implements Iterator<Map.Entry<String, List<String>>> {
        private Iterator<HttpString> names;

        private EntryIterator() {
            this.names = UndertowHeadersAdapter.this.headers.getHeaderNames().iterator();
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

