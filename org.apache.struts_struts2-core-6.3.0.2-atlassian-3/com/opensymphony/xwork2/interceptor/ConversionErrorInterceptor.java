/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.text.StringEscapeUtils
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.conversion.impl.ConversionData;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.util.ValueStack;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringEscapeUtils;

public class ConversionErrorInterceptor
extends MethodFilterInterceptor {
    public static final String ORIGINAL_PROPERTY_OVERRIDE = "original.property.override";

    protected Object getOverrideExpr(ActionInvocation invocation, Object value) {
        return this.escape(value);
    }

    protected String escape(Object value) {
        return "\"" + StringEscapeUtils.escapeJava((String)String.valueOf(value)) + "\"";
    }

    @Override
    public String doIntercept(ActionInvocation invocation) throws Exception {
        ActionContext invocationContext = invocation.getInvocationContext();
        Map<String, ConversionData> conversionErrors = invocationContext.getConversionErrors();
        ValueStack stack = invocationContext.getValueStack();
        HashMap<String, Object> fakie = null;
        for (Map.Entry<String, ConversionData> entry : conversionErrors.entrySet()) {
            ConversionData conversionData;
            String propertyName = entry.getKey();
            if (!this.shouldAddError(propertyName, (conversionData = entry.getValue()).getValue())) continue;
            String message = XWorkConverter.getConversionErrorMessage(propertyName, conversionData.getToClass(), stack);
            Object action = invocation.getAction();
            if (action instanceof ValidationAware) {
                ValidationAware va = (ValidationAware)action;
                va.addFieldError(propertyName, message);
            }
            if (fakie == null) {
                fakie = new HashMap<String, Object>();
            }
            fakie.put(propertyName, this.getOverrideExpr(invocation, conversionData.getValue()));
        }
        if (fakie != null) {
            stack.getContext().put(ORIGINAL_PROPERTY_OVERRIDE, fakie);
            invocation.addPreResultListener(new PreResultListener(){

                @Override
                public void beforeResult(ActionInvocation invocation, String resultCode) {
                    Map fakie = (Map)invocation.getInvocationContext().get(ConversionErrorInterceptor.ORIGINAL_PROPERTY_OVERRIDE);
                    if (fakie != null) {
                        invocation.getStack().setExprOverrides(fakie);
                    }
                }
            });
        }
        return invocation.invoke();
    }

    protected boolean shouldAddError(String propertyName, Object value) {
        return true;
    }
}

