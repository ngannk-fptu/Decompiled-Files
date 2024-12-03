/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.dispatcher;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.struts2.dispatcher.Parameter;

public class HttpParameters
implements Map<String, Parameter> {
    private final Map<String, Parameter> parameters = new TreeMap<String, Parameter>(String.CASE_INSENSITIVE_ORDER);

    private HttpParameters(Map<String, Parameter> parameters) {
        this.parameters.putAll(parameters);
    }

    public static Builder create(Map requestParameterMap) {
        return new Builder(requestParameterMap);
    }

    public static Builder create() {
        return new Builder(new TreeMap(String.CASE_INSENSITIVE_ORDER));
    }

    public HttpParameters remove(Set<String> paramsToRemove) {
        for (String paramName : paramsToRemove) {
            this.parameters.remove(paramName);
        }
        return this;
    }

    public HttpParameters remove(final String paramToRemove) {
        return this.remove((Set<String>)new HashSet<String>(){
            {
                this.add(paramToRemove);
            }
        });
    }

    public boolean contains(String name) {
        return this.parameters.containsKey(name);
    }

    @Deprecated
    private Map<String, String[]> toMap() {
        HashMap<String, String[]> result = new HashMap<String, String[]>(this.parameters.size());
        for (Map.Entry<String, Parameter> entry : this.parameters.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getMultipleValues());
        }
        return result;
    }

    public HttpParameters appendAll(Map<String, Parameter> newParams) {
        this.parameters.putAll(newParams);
        return this;
    }

    @Override
    public int size() {
        return this.parameters.size();
    }

    @Override
    public boolean isEmpty() {
        return this.parameters.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.parameters.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.parameters.containsValue(value);
    }

    @Override
    public Parameter get(Object key) {
        if (key == null) {
            return new Parameter.Empty("null");
        }
        Parameter val = this.parameters.get(key.toString());
        return val != null ? val : new Parameter.Empty(key.toString());
    }

    @Override
    public Parameter put(String key, Parameter value) {
        throw new IllegalAccessError("HttpParameters are immutable, you cannot put value directly!");
    }

    @Override
    public Parameter remove(Object key) {
        throw new IllegalAccessError("HttpParameters are immutable, you cannot remove object directly!");
    }

    @Override
    public void putAll(Map<? extends String, ? extends Parameter> m) {
        throw new IllegalAccessError("HttpParameters are immutable, you cannot put values directly!");
    }

    @Override
    public void clear() {
        throw new IllegalAccessError("HttpParameters are immutable, you cannot clear values directly!");
    }

    @Override
    public Set<String> keySet() {
        return Collections.unmodifiableSet(new TreeSet<String>(this.parameters.keySet()));
    }

    @Override
    public Collection<Parameter> values() {
        return Collections.unmodifiableCollection(this.parameters.values());
    }

    @Override
    public Set<Map.Entry<String, Parameter>> entrySet() {
        return Collections.unmodifiableSet(this.parameters.entrySet());
    }

    public String toString() {
        return this.parameters.toString();
    }

    public static class Builder {
        private Map<String, Object> requestParameterMap = new HashMap<String, Object>();
        private HttpParameters parent;

        protected Builder(Map<String, ?> requestParameterMap) {
            this.requestParameterMap.putAll(requestParameterMap);
        }

        public Builder withParent(HttpParameters parentParams) {
            if (parentParams != null) {
                this.parent = parentParams;
            }
            return this;
        }

        public Builder withExtraParams(Map<String, ?> params) {
            if (params != null) {
                this.requestParameterMap.putAll(params);
            }
            return this;
        }

        public Builder withComparator(Comparator<String> orderedComparator) {
            this.requestParameterMap = new TreeMap<String, Object>(orderedComparator);
            return this;
        }

        public HttpParameters build() {
            HashMap<String, Parameter> parameters = this.parent == null ? new HashMap<String, Parameter>() : new HashMap(this.parent.parameters);
            for (Map.Entry<String, Object> entry : this.requestParameterMap.entrySet()) {
                String name = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof Parameter) {
                    parameters.put(name, (Parameter)value);
                    continue;
                }
                parameters.put(name, new Parameter.Request(name, value));
            }
            return new HttpParameters(parameters);
        }

        @Deprecated
        public HttpParameters buildNoNestedWrapping() {
            HashMap<String, Parameter> parameters = this.parent == null ? new HashMap<String, Parameter>() : new HashMap(this.parent.parameters);
            for (Map.Entry<String, Object> entry : this.requestParameterMap.entrySet()) {
                String name = entry.getKey();
                Object value = entry.getValue();
                Parameter parameterValue = value instanceof Parameter ? (Parameter)value : new Parameter.Request(name, value);
                parameters.put(name, parameterValue);
            }
            return new HttpParameters(parameters);
        }
    }
}

