/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.converter.json;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import org.springframework.lang.Nullable;

public class MappingJacksonValue {
    private Object value;
    @Nullable
    private Class<?> serializationView;
    @Nullable
    private FilterProvider filters;
    @Nullable
    private String jsonpFunction;

    public MappingJacksonValue(Object value) {
        this.value = value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    public void setSerializationView(@Nullable Class<?> serializationView) {
        this.serializationView = serializationView;
    }

    @Nullable
    public Class<?> getSerializationView() {
        return this.serializationView;
    }

    public void setFilters(@Nullable FilterProvider filters) {
        this.filters = filters;
    }

    @Nullable
    public FilterProvider getFilters() {
        return this.filters;
    }

    @Deprecated
    public void setJsonpFunction(@Nullable String functionName) {
        this.jsonpFunction = functionName;
    }

    @Deprecated
    @Nullable
    public String getJsonpFunction() {
        return this.jsonpFunction;
    }
}

