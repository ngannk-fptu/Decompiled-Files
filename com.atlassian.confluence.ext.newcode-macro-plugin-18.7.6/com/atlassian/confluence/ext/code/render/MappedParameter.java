/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.render;

import com.atlassian.confluence.ext.code.render.InvalidValueException;
import com.atlassian.confluence.ext.code.render.Parameter;
import java.util.Map;

class MappedParameter
implements Parameter {
    private String name;
    private String mappedName;

    MappedParameter(String name, String mappedName) {
        this.name = name;
        this.mappedName = mappedName;
    }

    @Override
    public final String getName() {
        return this.mappedName;
    }

    @Override
    public final String getMacroName() {
        return this.name;
    }

    @Override
    public String getValue(Map<String, String> parameters) throws InvalidValueException {
        return parameters.get(this.name);
    }
}

