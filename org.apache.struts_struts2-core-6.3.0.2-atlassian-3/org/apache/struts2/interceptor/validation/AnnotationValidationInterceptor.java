/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.reflect.MethodUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor.validation;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.validator.ValidationInterceptor;
import java.lang.reflect.Method;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;

public class AnnotationValidationInterceptor
extends ValidationInterceptor {
    private static final Logger LOG = LogManager.getLogger(AnnotationValidationInterceptor.class);

    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        Method method;
        Object action = invocation.getAction();
        if (action != null && null != MethodUtils.getAnnotation((Method)(method = this.getActionMethod(action.getClass(), invocation.getProxy().getMethod())), SkipValidation.class, (boolean)true, (boolean)true)) {
            return invocation.invoke();
        }
        return super.doIntercept(invocation);
    }

    protected Method getActionMethod(Class<?> actionClass, String methodName) {
        try {
            return actionClass.getMethod(methodName, new Class[0]);
        }
        catch (NoSuchMethodException e) {
            throw new ConfigurationException("Wrong method was defined as an action method: " + methodName, e);
        }
    }
}

