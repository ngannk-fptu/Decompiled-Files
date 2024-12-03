/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.params;

import com.atlassian.confluence.macro.MacroExecutionContext;
import com.atlassian.confluence.macro.params.BaseParameter;
import com.atlassian.confluence.macro.params.ParameterException;
import java.util.List;

public class MaxResultsParameter
extends BaseParameter<Integer> {
    private static final String[] DEFAULT_PARAM_NAMES = new String[]{"max", "maxResults"};

    public MaxResultsParameter() {
        this(null);
    }

    public MaxResultsParameter(String defaultValue) {
        super(DEFAULT_PARAM_NAMES, defaultValue);
    }

    public MaxResultsParameter(List<String> names, String defaultValue) {
        super(names, defaultValue);
    }

    @Override
    protected Integer findObject(String paramValue, MacroExecutionContext ctx) throws ParameterException {
        if (paramValue != null) {
            Integer maxResults;
            try {
                maxResults = Integer.parseInt(paramValue);
            }
            catch (NumberFormatException nfe) {
                throw new ParameterException("'" + paramValue + "' cannot be parsed as a number");
            }
            return maxResults;
        }
        return null;
    }
}

