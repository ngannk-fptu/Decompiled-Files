/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.render;

import com.atlassian.confluence.ext.code.render.MappedParameter;
import java.util.Map;

public final class LanguageParameter
extends MappedParameter {
    private static final Object NONE_LANG = "none";
    private static final String PLAIN_LANG = "plain";
    private static final String AS_LANG = "actionscript";
    private static final String AS3_LANG = "actionscript3";

    public LanguageParameter(String name, String mappedName) {
        super(name, mappedName);
    }

    @Override
    public String getValue(Map<String, String> parameters) {
        String lang;
        String string = lang = parameters.get("lang") == null ? parameters.get("language") : parameters.get("lang");
        if (lang == null && (lang = parameters.get("0")) == null) {
            lang = "Java".toLowerCase();
        }
        if (NONE_LANG.equals(lang)) {
            lang = PLAIN_LANG;
        }
        if (AS_LANG.equals(lang)) {
            lang = AS3_LANG;
        }
        return lang.toLowerCase();
    }
}

