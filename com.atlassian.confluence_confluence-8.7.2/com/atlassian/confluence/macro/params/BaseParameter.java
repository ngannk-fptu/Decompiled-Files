/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.params;

import com.atlassian.confluence.macro.MacroExecutionContext;
import com.atlassian.confluence.macro.params.Parameter;
import com.atlassian.confluence.macro.params.ParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class BaseParameter<T>
implements Parameter<T> {
    private List<String> paramNames;
    private String defaultValue;
    protected boolean shouldValidate;

    protected BaseParameter(String name, String defaultValue) {
        this(new String[]{name}, defaultValue);
    }

    protected BaseParameter(String[] names, String defaultValue) {
        this.paramNames = new ArrayList<String>(Arrays.asList(names));
        this.defaultValue = defaultValue;
    }

    protected BaseParameter(List<String> names, String defaultValue) {
        this.paramNames = new ArrayList<String>(names);
        this.defaultValue = defaultValue;
    }

    @Deprecated
    public final void addParameterAlias(String name) {
        this.paramNames.add(name);
    }

    @Deprecated
    public final void setParameterNames(String[] names) {
        this.paramNames = new ArrayList<String>(Arrays.asList(names));
    }

    @Deprecated
    public final void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public final String getDefaultValue() {
        return this.defaultValue;
    }

    protected final String getParameter(Map<String, String> params, List names, String defaultValue) {
        String value = null;
        Iterator i = names.iterator();
        while (i.hasNext() && value == null) {
            String param = (String)i.next();
            value = params.get(param);
        }
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public final String getParameterValue(Map<String, String> params) {
        return this.getParameter(params, this.paramNames, this.defaultValue);
    }

    @Override
    public final void setValidate(boolean shouldValidate) {
        this.shouldValidate = shouldValidate;
    }

    @Override
    public final T findValue(MacroExecutionContext ctx) throws ParameterException {
        String paramValue = this.getParameter(ctx.getParams(), this.paramNames, this.defaultValue);
        return this.findObject(paramValue, ctx);
    }

    protected abstract T findObject(String var1, MacroExecutionContext var2) throws ParameterException;
}

