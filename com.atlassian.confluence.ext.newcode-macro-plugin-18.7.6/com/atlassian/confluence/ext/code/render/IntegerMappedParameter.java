/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.render;

import com.atlassian.confluence.ext.code.render.InvalidValueException;
import com.atlassian.confluence.ext.code.render.MappedParameter;
import java.util.Map;

public final class IntegerMappedParameter
extends MappedParameter {
    public IntegerMappedParameter(String name, String mappedName) {
        super(name, mappedName);
    }

    @Override
    public String getValue(Map<String, String> parameters) throws InvalidValueException {
        String retval = super.getValue(parameters);
        try {
            if (retval != null) {
                Integer.parseInt(retval);
            }
        }
        catch (NumberFormatException e) {
            throw new InvalidValueException(this.getMacroName());
        }
        return retval;
    }
}

