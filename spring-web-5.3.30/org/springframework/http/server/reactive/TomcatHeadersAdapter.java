/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.MessageBytes
 *  org.apache.tomcat.util.http.MimeHeaders
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.MultiValueMap
 */
package org.springframework.http.server.reactive;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.MimeHeaders;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

class TomcatHeadersAdapter
implements MultiValueMap<String, String> {
    private final MimeHeaders headers;

    TomcatHeadersAdapter(MimeHeaders headers) {
        this.headers = headers;
    }

    public String getFirst(String key) {
        return this.headers.getHeader(key);
    }

    public void add(String key, @Nullable String value) {
        this.headers.addValue(key).setString(value);
    }

    public void addAll(String key, List<? extends String> values) {
        values.forEach(value -> this.add(key, (String)value));
    }

    public void addAll(MultiValueMap<String, String> values) {
        values.forEach(this::addAll);
    }

    public void set(String key, @Nullable String value) {
        this.headers.setValue(key).setString(value);
    }

    public void setAll(Map<String, String> values) {
        values.forEach(this::set);
    }

    public Map<String, String> toSingleValueMap() {
        LinkedHashMap singleValueMap = CollectionUtils.newLinkedHashMap((int)this.headers.size());
        this.keySet().forEach(key -> singleValueMap.put(key, this.getFirst((String)key)));
        return singleValueMap;
    }

    public int size() {
        Enumeration names = this.headers.names();
        int size = 0;
        while (names.hasMoreElements()) {
            ++size;
            names.nextElement();
        }
        return size;
    }

    public boolean isEmpty() {
        return this.headers.size() == 0;
    }

    public boolean containsKey(Object key) {
        if (key instanceof String) {
            return this.headers.findHeader((String)key, 0) != -1;
        }
        return false;
    }

    public boolean containsValue(Object value) {
        if (value instanceof String) {
            MessageBytes needle = MessageBytes.newInstance();
            needle.setString((String)value);
            for (int i = 0; i < this.headers.size(); ++i) {
                if (!this.headers.getValue(i).equals(needle)) continue;
                return true;
            }
        }
        return false;
    }

    @Nullable
    public List<String> get(Object key) {
        if (this.containsKey(key)) {
            return Collections.list(this.headers.values((String)key));
        }
        return null;
    }

    @Nullable
    public List<String> put(String key, List<String> value) {
        Object previousValues = this.get(key);
        this.headers.removeHeader(key);
        value.forEach(v -> this.headers.addValue(key).setString(v));
        return previousValues;
    }

    @Nullable
    public List<String> remove(Object key) {
        if (key instanceof String) {
            Object previousValues = this.get(key);
            this.headers.removeHeader((String)key);
            return previousValues;
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
        return this.keySet().stream().map(object -> this.get(object)).collect(Collectors.toList());
    }

    public Set<Map.Entry<String, List<String>>> entrySet() {
        return new AbstractSet<Map.Entry<String, List<String>>>(){

            @Override
            public Iterator<Map.Entry<String, List<String>>> iterator() {
                return new EntryIterator();
            }

            @Override
            public int size() {
                return TomcatHeadersAdapter.this.headers.size();
            }
        };
    }

    public String toString() {
        return HttpHeaders.formatHeaders(this);
    }

    private final class HeaderNamesIterator
    implements Iterator<String> {
        private final Enumeration<String> enumeration;
        @Nullable
        private String currentName;

        private HeaderNamesIterator(Enumeration<String> enumeration) {
            this.enumeration = enumeration;
        }

        @Override
        public boolean hasNext() {
            return this.enumeration.hasMoreElements();
        }

        @Override
        public String next() {
            this.currentName = this.enumeration.nextElement();
            return this.currentName;
        }

        @Override
        public void remove() {
            if (this.currentName == null) {
                throw new IllegalStateException("No current Header in iterator");
            }
            int index = TomcatHeadersAdapter.this.headers.findHeader(this.currentName, 0);
            if (index == -1) {
                throw new IllegalStateException("Header not present: " + this.currentName);
            }
            TomcatHeadersAdapter.this.headers.removeHeader(index);
        }
    }

    private class HeaderNames
    extends AbstractSet<String> {
        private HeaderNames() {
        }

        @Override
        public Iterator<String> iterator() {
            return new HeaderNamesIterator(TomcatHeadersAdapter.this.headers.names());
        }

        @Override
        public int size() {
            Enumeration names = TomcatHeadersAdapter.this.headers.names();
            int size = 0;
            while (names.hasMoreElements()) {
                names.nextElement();
                ++size;
            }
            return size;
        }
    }

    private final class HeaderEntry
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
        @Nullable
        public List<String> getValue() {
            return TomcatHeadersAdapter.this.get(this.key);
        }

        @Override
        @Nullable
        public List<String> setValue(List<String> value) {
            Object previous = this.getValue();
            TomcatHeadersAdapter.this.headers.removeHeader(this.key);
            TomcatHeadersAdapter.this.addAll(this.key, value);
            return previous;
        }
    }

    private class EntryIterator
    implements Iterator<Map.Entry<String, List<String>>> {
        private Enumeration<String> names;

        private EntryIterator() {
            this.names = TomcatHeadersAdapter.this.headers.names();
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

