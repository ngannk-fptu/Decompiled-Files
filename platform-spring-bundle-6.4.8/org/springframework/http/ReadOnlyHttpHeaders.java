/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

class ReadOnlyHttpHeaders
extends HttpHeaders {
    private static final long serialVersionUID = -8578554704772377436L;
    @Nullable
    private MediaType cachedContentType;
    @Nullable
    private List<MediaType> cachedAccept;

    ReadOnlyHttpHeaders(MultiValueMap<String, String> headers) {
        super(headers);
    }

    @Override
    public MediaType getContentType() {
        MediaType contentType;
        if (this.cachedContentType != null) {
            return this.cachedContentType;
        }
        this.cachedContentType = contentType = super.getContentType();
        return contentType;
    }

    @Override
    public List<MediaType> getAccept() {
        if (this.cachedAccept != null) {
            return this.cachedAccept;
        }
        List<MediaType> accept = super.getAccept();
        this.cachedAccept = accept;
        return accept;
    }

    @Override
    public void clearContentHeaders() {
    }

    @Override
    public List<String> get(Object key) {
        List values = (List)this.headers.get(key);
        return values != null ? Collections.unmodifiableList(values) : null;
    }

    @Override
    public void add(String headerName, @Nullable String headerValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addAll(String key, List<? extends String> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addAll(MultiValueMap<String, String> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(String headerName, @Nullable String headerValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAll(Map<String, String> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String> toSingleValueMap() {
        return Collections.unmodifiableMap(this.headers.toSingleValueMap());
    }

    @Override
    public Set<String> keySet() {
        return Collections.unmodifiableSet(this.headers.keySet());
    }

    @Override
    public List<String> put(String key, List<String> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ? extends List<String>> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<List<String>> values() {
        return Collections.unmodifiableCollection(this.headers.values());
    }

    @Override
    public Set<Map.Entry<String, List<String>>> entrySet() {
        return this.headers.entrySet().stream().map(AbstractMap.SimpleImmutableEntry::new).collect(Collectors.collectingAndThen(Collectors.toCollection(LinkedHashSet::new), Collections::unmodifiableSet));
    }
}

