/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 */
package com.sun.xml.ws.transport.httpspi.servlet;

import com.sun.xml.ws.transport.httpspi.servlet.Headers;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;

class ExchangeResponseHeaders
extends Headers {
    private final HttpServletResponse response;

    ExchangeResponseHeaders(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return super.containsValue(value);
    }

    @Override
    public List<String> get(Object key) {
        return super.get(key);
    }

    @Override
    public String getFirst(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> put(String key, List<String> value) {
        for (String val : value) {
            this.response.addHeader(key, val);
        }
        return super.put(key, value);
    }

    @Override
    public void add(String key, String value) {
        this.response.addHeader(key, value);
        super.add(key, value);
    }

    @Override
    public void set(String key, String value) {
        this.response.addHeader(key, value);
        super.set(key, value);
    }

    @Override
    public List<String> remove(Object key) {
        return super.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends List<String>> t) {
        super.putAll(t);
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public Set<String> keySet() {
        return super.keySet();
    }

    @Override
    public Collection<List<String>> values() {
        return super.values();
    }

    @Override
    public Set<Map.Entry<String, List<String>>> entrySet() {
        return super.entrySet();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

