/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.interceptor.ParametersInterceptor
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.xwork.interceptors;

import com.atlassian.xwork.ParameterSafe;
import com.opensymphony.xwork2.interceptor.ParametersInterceptor;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SafeParametersInterceptor
extends ParametersInterceptor {
    public static final Logger LOG = LoggerFactory.getLogger(SafeParametersInterceptor.class);
    private static final Pattern NESTED_PARAM_PATTERN = Pattern.compile("\\.|\\[.*\\D+.*]");

    protected boolean isAcceptableParameter(String name, Object action) {
        return super.isAcceptableParameter(name, action) && SafeParametersInterceptor.isAllowedNestedParam(name, action);
    }

    public static boolean isAllowedNestedParam(String name, Object action) {
        BeanInfo beanInfo;
        if (!NESTED_PARAM_PATTERN.matcher(name).find()) {
            return true;
        }
        try {
            beanInfo = Introspector.getBeanInfo(action.getClass());
        }
        catch (IntrospectionException e) {
            LOG.warn("Error introspecting action parameter {} for action {}", new Object[]{name, action, e});
            return false;
        }
        String nestedParam = name.substring(0, StringUtils.indexOfAny((CharSequence)name, (String)".["));
        for (PropertyDescriptor desc : beanInfo.getPropertyDescriptors()) {
            if (!desc.getName().equals(nestedParam)) continue;
            if (SafeParametersInterceptor.isParamAnnotationAllowed(desc.getReadMethod())) {
                return true;
            }
            LOG.warn("Attempt to call unsafe property setter {} on {}", (Object)name, action);
            return false;
        }
        return false;
    }

    private static boolean isParamAnnotationAllowed(Method readMethod) {
        if (readMethod == null) {
            return false;
        }
        boolean isMethodAnnotated = readMethod.getAnnotation(ParameterSafe.class) != null;
        boolean isReturnTypeAnnotated = readMethod.getReturnType().getAnnotation(ParameterSafe.class) != null;
        return isMethodAnnotated || isReturnTypeAnnotated;
    }
}

