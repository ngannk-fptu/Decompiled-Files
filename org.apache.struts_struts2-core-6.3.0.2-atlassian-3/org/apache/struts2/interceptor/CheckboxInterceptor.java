/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;

public class CheckboxInterceptor
extends AbstractInterceptor {
    private static final long serialVersionUID = -586878104807229585L;
    private String uncheckedValue = Boolean.FALSE.toString();
    private static final Logger LOG = LogManager.getLogger(CheckboxInterceptor.class);

    @Override
    public String intercept(ActionInvocation ai) throws Exception {
        HttpParameters parameters = ai.getInvocationContext().getParameters();
        HashMap<String, Parameter> extraParams = new HashMap<String, Parameter>();
        HashSet<String> checkboxParameters = new HashSet<String>();
        for (Map.Entry<String, Parameter> parameter : parameters.entrySet()) {
            String name = parameter.getKey();
            if (!name.startsWith("__checkbox_")) continue;
            String checkboxName = name.substring("__checkbox_".length());
            Parameter value = parameter.getValue();
            checkboxParameters.add(name);
            if (value.isMultiple()) {
                LOG.debug("Bypassing automatic checkbox detection due to multiple checkboxes of the same name: {}", (Object)name);
                continue;
            }
            if (parameters.contains(checkboxName)) continue;
            extraParams.put(checkboxName, new Parameter.Request(checkboxName, this.uncheckedValue));
        }
        parameters.remove(checkboxParameters);
        ai.getInvocationContext().getParameters().appendAll(extraParams);
        return ai.invoke();
    }

    public void setUncheckedValue(String uncheckedValue) {
        this.uncheckedValue = uncheckedValue;
    }
}

