/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.atlassian.oauth;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.jcip.annotations.Immutable;

@Immutable
public class Request {
    public static final String OAUTH_SESSION_HANDLE = "oauth_session_handle";
    public static final String OAUTH_EXPIRES_IN = "oauth_expires_in";
    public static final String OAUTH_AUTHORIZATION_EXPIRES_IN = "oauth_authorization_expires_in";
    private final HttpMethod method;
    private final URI uri;
    private final Iterable<Parameter> parameters;
    private Map<String, Iterable<String>> parameterMap;

    public Request(HttpMethod method, URI uri, Iterable<Parameter> parameters) {
        this.method = Objects.requireNonNull(method, "method");
        this.uri = Objects.requireNonNull(uri, "uri");
        this.parameters = this.copy(parameters);
    }

    private <T> Iterable<T> copy(Iterable<T> elements) {
        ArrayList<T> copy = new ArrayList<T>();
        for (T e : elements) {
            copy.add(e);
        }
        return Collections.unmodifiableList(copy);
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public URI getUri() {
        return this.uri;
    }

    public Iterable<Parameter> getParameters() {
        return this.parameters;
    }

    public String getParameter(String parameterName) {
        Iterable<String> values = this.getParameterMap().get(parameterName);
        if (values == null) {
            return null;
        }
        Iterator<String> it = values.iterator();
        if (!it.hasNext()) {
            return null;
        }
        return it.next();
    }

    private Map<String, Iterable<String>> getParameterMap() {
        if (this.parameterMap == null) {
            this.parameterMap = this.makeUnmodifiableMap(this.makeParameterMap());
        }
        return this.parameterMap;
    }

    private Map<String, List<String>> makeParameterMap() {
        HashMap<String, List<String>> map = new HashMap<String, List<String>>();
        if (this.parameters != null) {
            for (Parameter p : this.parameters) {
                String name = p.getName();
                LinkedList<String> values = (LinkedList<String>)map.get(name);
                if (values == null) {
                    values = new LinkedList<String>();
                    map.put(name, values);
                }
                values.add(p.getValue());
            }
        }
        return map;
    }

    private Map<String, Iterable<String>> makeUnmodifiableMap(Map<String, List<String>> map) {
        HashMap<String, List<String>> immutableMap = new HashMap<String, List<String>>();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            immutableMap.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
        }
        return Collections.unmodifiableMap(immutableMap);
    }

    @Immutable
    public static class Parameter {
        private final String name;
        private final String value;

        public Parameter(String name, String value) {
            this.name = Objects.requireNonNull(name, "name");
            this.value = value;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }

        public String toString() {
            return this.name + '=' + this.value;
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + this.name.hashCode();
            result = 31 * result + this.value.hashCode();
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || this.getClass() != obj.getClass()) {
                return false;
            }
            Parameter that = (Parameter)obj;
            return this.name.equals(that.name) && this.value.equals(that.value);
        }
    }

    public static enum HttpMethod {
        GET,
        POST,
        PUT,
        DELETE,
        OPTIONS,
        TRACE,
        HEAD;

    }
}

