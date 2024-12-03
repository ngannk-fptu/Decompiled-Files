/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpFields
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.MultiValueMap
 */
package org.springframework.http.server.reactive;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.jetty.http.HttpFields;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

class JettyHeadersAdapter
implements MultiValueMap<String, String> {
    private final HttpFields headers;

    JettyHeadersAdapter(HttpFields headers) {
        this.headers = headers;
    }

    public String getFirst(String key) {
        return this.headers.get(key);
    }

    public void add(String key, @Nullable String value) {
        this.headers.add(key, value);
    }

    public void addAll(String key, List<? extends String> values) {
        values.forEach(value -> this.add(key, (String)value));
    }

    public void addAll(MultiValueMap<String, String> values) {
        values.forEach(this::addAll);
    }

    public void set(String key, @Nullable String value) {
        this.headers.put(key, value);
    }

    public void setAll(Map<String, String> values) {
        values.forEach(this::set);
    }

    public Map<String, String> toSingleValueMap() {
        LinkedHashMap singleValueMap = CollectionUtils.newLinkedHashMap((int)this.headers.size());
        Iterator iterator = this.headers.iterator();
        iterator.forEachRemaining(field -> {
            if (!singleValueMap.containsKey(field.getName())) {
                singleValueMap.put(field.getName(), field.getValue());
            }
        });
        return singleValueMap;
    }

    public int size() {
        return this.headers.getFieldNamesCollection().size();
    }

    public boolean isEmpty() {
        return this.headers.size() == 0;
    }

    public boolean containsKey(Object key) {
        return key instanceof String && this.headers.containsKey((String)key);
    }

    public boolean containsValue(Object value) {
        return value instanceof String && this.headers.stream().anyMatch(field -> field.contains((String)value));
    }

    @Nullable
    public List<String> get(Object key) {
        if (this.containsKey(key)) {
            return this.headers.getValuesList((String)key);
        }
        return null;
    }

    @Nullable
    public List<String> put(String key, List<String> value) {
        Object oldValues = this.get(key);
        this.headers.put(key, value);
        return oldValues;
    }

    @Nullable
    public List<String> remove(Object key) {
        if (key instanceof String) {
            Object oldValues = this.get(key);
            this.headers.remove((String)key);
            return oldValues;
        }
        return null;
    }

    public void putAll(Map<? extends String, ? extends List<String>> map) {
        map.forEach(this::put);
    }

    public void clear() {
        this.headers.clear();
    }

    public Set<String> keySet() {
        return new HeaderNames();
    }

    public Collection<List<String>> values() {
        return this.headers.getFieldNamesCollection().stream().map(arg_0 -> ((HttpFields)this.headers).getValuesList(arg_0)).collect(Collectors.toList());
    }

    public Set<Map.Entry<String, List<String>>> entrySet() {
        return new AbstractSet<Map.Entry<String, List<String>>>(){

            @Override
            public Iterator<Map.Entry<String, List<String>>> iterator() {
                return new EntryIterator();
            }

            @Override
            public int size() {
                return JettyHeadersAdapter.this.headers.size();
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
            if (!JettyHeadersAdapter.this.headers.containsKey(this.currentName)) {
                throw new IllegalStateException("Header not present: " + this.currentName);
            }
            JettyHeadersAdapter.this.headers.remove(this.currentName);
        }
    }

    private class HeaderNames
    extends AbstractSet<String> {
        private HeaderNames() {
        }

        @Override
        public Iterator<String> iterator() {
            return new HeaderNamesIterator(JettyHeadersAdapter.this.headers.getFieldNamesCollection().iterator());
        }

        @Override
        public int size() {
            return JettyHeadersAdapter.this.headers.getFieldNamesCollection().size();
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
            return JettyHeadersAdapter.this.headers.getValuesList(this.key);
        }

        @Override
        public List<String> setValue(List<String> value) {
            List previousValues = JettyHeadersAdapter.this.headers.getValuesList(this.key);
            JettyHeadersAdapter.this.headers.put(this.key, value);
            return previousValues;
        }
    }

    private class EntryIterator
    implements Iterator<Map.Entry<String, List<String>>> {
        private final Enumeration<String> names;

        private EntryIterator() {
            this.names = JettyHeadersAdapter.this.headers.getFieldNames();
        }

        @Override
        public boolean hasNext() {
            return this.names.hasMoreElements();
        }

        @Override
        public Map.Entry<String, List<String>> next() {
            return new HeaderEntry(this.names.nextElement());
        }
    }
}

