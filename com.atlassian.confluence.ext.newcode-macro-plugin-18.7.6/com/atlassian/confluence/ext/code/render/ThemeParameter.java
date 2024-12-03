/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.render;

import com.atlassian.confluence.ext.code.render.MappedParameter;
import java.util.Map;

public final class ThemeParameter
extends MappedParameter {
    public ThemeParameter(String name, String mappedName) {
        super(name, mappedName);
    }

    @Override
    public String getValue(Map<String, String> parameters) {
        String theme = parameters.get("theme");
        if (theme == null) {
            theme = "Confluence";
        }
        return theme;
    }
}

