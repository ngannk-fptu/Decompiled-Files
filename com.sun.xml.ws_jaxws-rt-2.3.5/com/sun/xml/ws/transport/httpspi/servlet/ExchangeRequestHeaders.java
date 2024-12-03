/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.sun.xml.ws.transport.httpspi.servlet;

import com.sun.xml.ws.transport.httpspi.servlet.Headers;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

class ExchangeRequestHeaders
extends Headers {
    private final HttpServletRequest request;
    private boolean useMap = false;

    ExchangeRequestHeaders(HttpServletRequest request) {
        this.request = request;
    }

    private void convertToMap() {
        if (!this.useMap) {
            Enumeration e = this.request.getHeaderNames();
            while (e.hasMoreElements()) {
                String name = (String)e.nextElement();
                Enumeration ev = this.request.getHeaders(name);
                while (ev.hasMoreElements()) {
                    String value = (String)ev.nextElement();
                    super.add(name, value);
                }
            }
            this.useMap = true;
        }
    }

    @Override
    public int size() {
        this.convertToMap();
        return super.size();
    }

    @Override
    public boolean isEmpty() {
        this.convertToMap();
        return super.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof String)) {
            return false;
        }
        return this.useMap ? super.containsKey(key) : this.request.getHeader((String)key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        this.convertToMap();
        return super.containsValue(value);
    }

    @Override
    public List<String> get(Object key) {
        this.convertToMap();
        return super.get(key);
    }

    @Override
    public String getFirst(String key) {
        return this.useMap ? super.getFirst(key) : this.request.getHeader(key);
    }

    @Override
    public List<String> put(String key, List<String> value) {
        this.convertToMap();
        return super.put(key, value);
    }

    @Override
    public void add(String key, String value) {
        this.convertToMap();
        super.add(key, value);
    }

    @Override
    public void set(String key, String value) {
        this.convertToMap();
        super.set(key, value);
    }

    @Override
    public List<String> remove(Object key) {
        this.convertToMap();
        return super.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends List<String>> t) {
        this.convertToMap();
        super.putAll(t);
    }

    @Override
    public void clear() {
        this.convertToMap();
        super.clear();
    }

    @Override
    public Set<String> keySet() {
        this.convertToMap();
        return super.keySet();
    }

    @Override
    public Collection<List<String>> values() {
        this.convertToMap();
        return super.values();
    }

    @Override
    public Set<Map.Entry<String, List<String>>> entrySet() {
        this.convertToMap();
        return super.entrySet();
    }

    @Override
    public String toString() {
        this.convertToMap();
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

