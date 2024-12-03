/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.TextParseUtil;
import java.util.Collections;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.action.NoParameters;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;

public class ParameterRemoverInterceptor
extends AbstractInterceptor {
    private static final Logger LOG = LogManager.getLogger(ParameterRemoverInterceptor.class);
    private Set<String> paramNames = Collections.emptySet();
    private Set<String> paramValues = Collections.emptySet();

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ActionContext ac;
        HttpParameters parameters;
        if (!(invocation.getAction() instanceof NoParameters) && null != this.paramNames && (parameters = (ac = invocation.getInvocationContext()).getParameters()) != null) {
            for (String removeName : this.paramNames) {
                try {
                    Parameter parameter = parameters.get(removeName);
                    if (!parameter.isDefined() || !this.paramValues.contains(parameter.getValue())) continue;
                    parameters.remove(removeName);
                }
                catch (Exception e) {
                    LOG.error("Failed to convert parameter to string", (Throwable)e);
                }
            }
        }
        return invocation.invoke();
    }

    public void setParamNames(String paramNames) {
        this.paramNames = TextParseUtil.commaDelimitedStringToSet(paramNames);
    }

    public void setParamValues(String paramValues) {
        this.paramValues = TextParseUtil.commaDelimitedStringToSet(paramValues);
    }
}

