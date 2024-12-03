/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.streams.api.common.uri;

import com.atlassian.streams.api.common.uri.Uri;
import com.atlassian.streams.api.common.uri.Uris;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

public final class UriBuilder {
    private static final Pattern QUERY_PATTERN = Pattern.compile("([^&=]+)=([^&=]*)");
    private String scheme;
    private String authority;
    private String path;
    private final ParamString query;
    private final ParamString fragment;

    public UriBuilder(Uri uri) {
        this.scheme = uri.getScheme();
        this.authority = uri.getAuthority();
        this.path = uri.getPath();
        this.query = new ParamString(uri.getQuery());
        this.fragment = new ParamString(uri.getFragment());
    }

    public UriBuilder(HttpServletRequest req) {
        this.scheme = req.getScheme().toLowerCase();
        int serverPort = req.getServerPort();
        this.authority = req.getServerName() + (serverPort == 80 && "http".equals(this.scheme) || serverPort == 443 && "https".equals(this.scheme) || serverPort <= 0 ? "" : ":" + serverPort);
        this.path = req.getRequestURI();
        this.query = new ParamString(req.getQueryString());
        this.fragment = new ParamString();
    }

    public UriBuilder() {
        this.query = new ParamString();
        this.fragment = new ParamString();
    }

    public Uri toUri() {
        return new Uri(this);
    }

    public String getScheme() {
        return this.scheme;
    }

    public UriBuilder setScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public String getAuthority() {
        return this.authority;
    }

    public UriBuilder setAuthority(String authority) {
        this.authority = authority;
        return this;
    }

    public String getPath() {
        return this.path;
    }

    public UriBuilder setPath(String path) {
        this.path = path;
        return this;
    }

    public String getQuery() {
        return this.query.getString();
    }

    public UriBuilder setQuery(String str) {
        this.query.setString(str);
        return this;
    }

    public UriBuilder addQueryParameter(String name, String value) {
        this.query.add(name, value);
        return this;
    }

    public UriBuilder addQueryParameters(Map<String, String> parameters) {
        this.query.addAll(parameters);
        return this;
    }

    public UriBuilder putQueryParameter(String name, String ... values) {
        this.query.put(name, values);
        return this;
    }

    public UriBuilder putQueryParameter(String name, Iterable<String> values) {
        this.query.put(name, values);
        return this;
    }

    public UriBuilder removeQueryParameter(String name) {
        this.query.remove(name);
        return this;
    }

    public Map<String, List<String>> getQueryParameters() {
        return this.query.getParams();
    }

    public List<String> getQueryParameters(String name) {
        return this.query.getParams(name);
    }

    public String getQueryParameter(String name) {
        return this.query.get(name);
    }

    public String getFragment() {
        return this.fragment.getString();
    }

    public UriBuilder setFragment(String str) {
        this.fragment.setString(str);
        return this;
    }

    public UriBuilder addFragmentParameter(String name, String value) {
        this.fragment.add(name, value);
        return this;
    }

    public UriBuilder addFragmentParameters(Map<String, String> parameters) {
        this.fragment.addAll(parameters);
        return this;
    }

    public UriBuilder putFragmentParameter(String name, String ... values) {
        this.fragment.put(name, values);
        return this;
    }

    public UriBuilder putFragmentParameter(String name, Iterable<String> values) {
        this.fragment.put(name, values);
        return this;
    }

    public UriBuilder removeFragmentParameter(String name) {
        this.fragment.remove(name);
        return this;
    }

    public Map<String, List<String>> getFragmentParameters() {
        return this.fragment.getParams();
    }

    public List<String> getFragmentParameters(String name) {
        return this.fragment.getParams(name);
    }

    public String getFragmentParameter(String name) {
        return this.fragment.get(name);
    }

    public static String joinParameters(Map<String, List<String>> query) {
        if (query.isEmpty()) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        boolean firstDone = false;
        for (Map.Entry<String, List<String>> entry : query.entrySet()) {
            String name = Uris.encode(entry.getKey());
            for (String value : entry.getValue()) {
                if (firstDone) {
                    buf.append('&');
                }
                firstDone = true;
                buf.append(name).append('=').append(Uris.encode(value));
            }
        }
        return buf.toString();
    }

    public static Map<String, List<String>> splitParameters(String query) {
        if (query == null) {
            return Collections.emptyMap();
        }
        LinkedHashMap params = Maps.newLinkedHashMap();
        Matcher paramMatcher = QUERY_PATTERN.matcher(query);
        while (paramMatcher.find()) {
            String name = Uris.decode(paramMatcher.group(1));
            String value = Uris.decode(paramMatcher.group(2));
            List values = (List)params.get(name);
            if (values == null) {
                values = Lists.newArrayList();
                params.put(name, values);
            }
            values.add(value);
        }
        return Collections.unmodifiableMap(params);
    }

    public String toString() {
        return this.toUri().toString();
    }

    public int hashCode() {
        return this.toUri().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof UriBuilder)) {
            return false;
        }
        return this.toString().equals(obj.toString());
    }

    private static final class ParamString {
        private final Map<String, List<String>> params = Maps.newLinkedHashMap();
        private String str;

        private ParamString() {
        }

        private ParamString(String str) {
            this();
            this.setString(str);
        }

        public String getString() {
            if (this.str == null) {
                this.str = UriBuilder.joinParameters(this.params);
            }
            return this.str;
        }

        public void setString(String str) {
            this.params.clear();
            this.params.putAll(UriBuilder.splitParameters(str));
            this.str = str;
        }

        public void add(String name, String value) {
            this.str = null;
            ArrayList values = this.params.get(name);
            if (values == null) {
                values = Lists.newArrayList();
                this.params.put(name, values);
            }
            values.add(value);
        }

        public void addAll(Map<String, String> parameters) {
            this.str = null;
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                this.add(entry.getKey(), entry.getValue());
            }
        }

        public void put(String name, String ... values) {
            this.str = null;
            this.params.put(name, Lists.newArrayList((Object[])values));
        }

        public void put(String name, Iterable<String> values) {
            this.str = null;
            this.params.put(name, Lists.newArrayList(values));
        }

        public void remove(String name) {
            this.str = null;
            this.params.remove(name);
        }

        public Map<String, List<String>> getParams() {
            return this.params;
        }

        public List<String> getParams(String name) {
            return this.params.get(name);
        }

        public String get(String name) {
            Collection values = this.params.get(name);
            if (values == null || values.isEmpty()) {
                return null;
            }
            return (String)values.iterator().next();
        }
    }
}

