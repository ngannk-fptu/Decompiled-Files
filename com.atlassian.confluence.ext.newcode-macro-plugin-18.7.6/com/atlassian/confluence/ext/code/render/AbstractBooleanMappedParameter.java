/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.render;

import com.atlassian.confluence.ext.code.render.InvalidValueException;
import com.atlassian.confluence.ext.code.render.MappedParameter;
import java.util.Map;

public abstract class AbstractBooleanMappedParameter
extends MappedParameter {
    public AbstractBooleanMappedParameter(String name, String mappedName) {
        super(name, mappedName);
    }

    protected final String internalGetValue(Map<String, String> parameters) throws InvalidValueException {
        String retval = super.getValue(parameters);
        if (null != retval && !"true".equals(retval) && !"false".equals(retval)) {
            throw new InvalidValueException(this.getMacroName());
        }
        return retval;
    }
}

