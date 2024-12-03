/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import java.util.Map;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.interceptor.parameter.ParametersInterceptor;

public class ActionMappingParametersInterceptor
extends ParametersInterceptor {
    @Override
    protected HttpParameters retrieveParameters(ActionContext ac) {
        ActionMapping mapping = ac.getActionMapping();
        if (mapping != null) {
            return HttpParameters.create(mapping.getParams()).buildNoNestedWrapping();
        }
        return HttpParameters.create().buildNoNestedWrapping();
    }

    @Override
    protected void addParametersToContext(ActionContext ac, Map<String, ?> newParams) {
        HttpParameters previousParams = ac.getParameters();
        HttpParameters.Builder combinedParams = HttpParameters.create().withParent(previousParams).withExtraParams(newParams);
        ac.withParameters(combinedParams.buildNoNestedWrapping());
    }
}

