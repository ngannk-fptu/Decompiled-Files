/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.parameter.multivalued;

import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractor;
import com.sun.jersey.spi.StringReader;

abstract class AbstractStringReaderExtractor
implements MultivaluedParameterExtractor {
    protected final StringReader sr;
    protected final String parameter;
    protected final String defaultStringValue;

    public AbstractStringReaderExtractor(StringReader sr, String parameter, String defaultStringValue) {
        StringReader.ValidateDefaultValue validate;
        this.sr = sr;
        this.parameter = parameter;
        this.defaultStringValue = defaultStringValue;
        if (defaultStringValue != null && ((validate = sr.getClass().getAnnotation(StringReader.ValidateDefaultValue.class)) == null || validate.value())) {
            sr.fromString(defaultStringValue);
        }
    }

    @Override
    public String getName() {
        return this.parameter;
    }

    @Override
    public String getDefaultStringValue() {
        return this.defaultStringValue;
    }
}

