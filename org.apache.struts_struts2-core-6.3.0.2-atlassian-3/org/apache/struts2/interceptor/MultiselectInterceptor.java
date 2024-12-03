/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import java.util.HashMap;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;

public class MultiselectInterceptor
extends AbstractInterceptor {
    private static final long serialVersionUID = 1L;

    @Override
    public String intercept(ActionInvocation ai) throws Exception {
        HttpParameters parameters = ai.getInvocationContext().getParameters();
        HashMap<String, Parameter> newParams = new HashMap<String, Parameter>();
        for (String name : parameters.keySet()) {
            if (!name.startsWith("__multiselect_")) continue;
            String key = name.substring("__multiselect_".length());
            if (!parameters.contains(key)) {
                newParams.put(key, new Parameter.Request(key, new String[0]));
            }
            parameters = parameters.remove(name);
        }
        ai.getInvocationContext().getParameters().appendAll(newParams);
        return ai.invoke();
    }
}

