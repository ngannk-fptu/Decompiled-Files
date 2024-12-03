/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.render;

import com.atlassian.confluence.ext.code.render.AbstractBooleanMappedParameter;
import com.atlassian.confluence.ext.code.render.InvalidValueException;
import java.util.Map;

public final class LineNumbersParameter
extends AbstractBooleanMappedParameter {
    public LineNumbersParameter(String name, String mappedName) {
        super(name, mappedName);
    }

    @Override
    public String getValue(Map<String, String> parameters) throws InvalidValueException {
        String value = this.internalGetValue(parameters);
        return value == null ? "false" : value;
    }
}

