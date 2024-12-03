/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.parameter.multivalued;

import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractor;
import javax.ws.rs.core.MultivaluedMap;

final class StringExtractor
implements MultivaluedParameterExtractor {
    final String parameter;
    final String defaultValue;

    public StringExtractor(String parameter) {
        this(parameter, null);
    }

    public StringExtractor(String parameter, String defaultValue) {
        this.parameter = parameter;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getName() {
        return this.parameter;
    }

    @Override
    public String getDefaultStringValue() {
        return this.defaultValue;
    }

    @Override
    public Object extract(MultivaluedMap<String, String> parameters) {
        String value = parameters.getFirst(this.parameter);
        return value != null ? value : this.defaultValue;
    }
}

