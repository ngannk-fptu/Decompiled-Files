/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.render;

import com.atlassian.confluence.ext.code.render.AbstractBooleanMappedParameter;
import com.atlassian.confluence.ext.code.render.InvalidValueException;
import java.util.Map;

public final class BooleanMappedParameter
extends AbstractBooleanMappedParameter {
    public BooleanMappedParameter(String name, String mappedName) {
        super(name, mappedName);
    }

    @Override
    public String getValue(Map<String, String> parameters) throws InvalidValueException {
        return this.internalGetValue(parameters);
    }
}

