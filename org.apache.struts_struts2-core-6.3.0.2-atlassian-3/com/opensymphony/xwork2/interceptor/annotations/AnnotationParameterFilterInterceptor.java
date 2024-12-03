/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.interceptor.annotations;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.annotations.Allowed;
import com.opensymphony.xwork2.interceptor.annotations.BlockByDefault;
import com.opensymphony.xwork2.interceptor.annotations.Blocked;
import com.opensymphony.xwork2.util.AnnotationUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import org.apache.struts2.dispatcher.HttpParameters;

public class AnnotationParameterFilterInterceptor
extends AbstractInterceptor {
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        HttpParameters parameters = invocation.getInvocationContext().getParameters();
        Object model = invocation.getStack().peek();
        if (model == action) {
            model = null;
        }
        boolean blockByDefault = action.getClass().isAnnotationPresent(BlockByDefault.class);
        ArrayList<Field> annotatedFields = new ArrayList<Field>();
        if (blockByDefault) {
            AnnotationUtils.addAllFields(Allowed.class, action.getClass(), annotatedFields);
            if (model != null) {
                AnnotationUtils.addAllFields(Allowed.class, model.getClass(), annotatedFields);
            }
            for (String paramName : parameters.keySet()) {
                boolean allowed = false;
                for (Field field : annotatedFields) {
                    if (!field.getName().equals(paramName)) continue;
                    allowed = true;
                    break;
                }
                if (allowed) continue;
                parameters = parameters.remove(paramName);
            }
        } else {
            AnnotationUtils.addAllFields(Blocked.class, action.getClass(), annotatedFields);
            if (model != null) {
                AnnotationUtils.addAllFields(Blocked.class, model.getClass(), annotatedFields);
            }
            for (String paramName : parameters.keySet()) {
                for (Field field : annotatedFields) {
                    if (!field.getName().equals(paramName)) continue;
                    parameters = parameters.remove(paramName);
                }
            }
        }
        invocation.getInvocationContext().withParameters(parameters);
        return invocation.invoke();
    }
}

