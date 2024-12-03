/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.render;

import com.atlassian.confluence.ext.code.render.BooleanMappedParameter;
import com.atlassian.confluence.ext.code.render.IntegerMappedParameter;
import com.atlassian.confluence.ext.code.render.InvalidValueException;
import com.atlassian.confluence.ext.code.render.LanguageParameter;
import com.atlassian.confluence.ext.code.render.LineNumbersParameter;
import com.atlassian.confluence.ext.code.render.Parameter;
import com.atlassian.confluence.ext.code.render.ThemeParameter;
import java.util.HashMap;
import java.util.Map;

class ParameterMapper {
    private final Map<String, Parameter> paramMap = new HashMap<String, Parameter>();

    ParameterMapper() {
        this.addParameter(new LanguageParameter("lang", "brush"));
        this.addParameter(new BooleanMappedParameter("collapse", "collapse"));
        this.addParameter(new IntegerMappedParameter("firstline", "first-line"));
        this.addParameter(new LineNumbersParameter("linenumbers", "gutter"));
        this.addParameter(new ThemeParameter("theme", "theme"));
        this.addParameter(new BooleanMappedParameter("exportImage", "exportImage"));
    }

    private void addParameter(Parameter parameter) {
        this.paramMap.put(parameter.getMacroName(), parameter);
    }

    String getLanguage(Map<String, String> parameters) throws InvalidValueException {
        return this.paramMap.get("lang").getValue(parameters) == null ? this.paramMap.get("language").getValue(parameters) : this.paramMap.get("lang").getValue(parameters);
    }

    String getTheme(Map<String, String> parameters) throws InvalidValueException {
        return this.paramMap.get("theme").getValue(parameters);
    }

    Boolean getExportImage(Map<String, String> parameters) throws InvalidValueException {
        String value = this.paramMap.get("exportImage").getValue(parameters);
        return value != null ? Boolean.valueOf(value) : null;
    }

    Map<String, String> mapParameters(Map<String, String> parameters) throws InvalidValueException {
        HashMap<String, String> result = new HashMap<String, String>();
        for (Map.Entry<String, Parameter> e : this.paramMap.entrySet()) {
            Parameter param = e.getValue();
            String value = param.getValue(parameters);
            if (value == null) continue;
            result.put(param.getName(), value);
        }
        return result;
    }
}

