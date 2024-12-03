/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.util.Collections;
import java.util.Map;
import javax.servlet.ServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.servlet.HandlerMapping;

public class ServletModelAttributeMethodProcessor
extends ModelAttributeMethodProcessor {
    public ServletModelAttributeMethodProcessor(boolean annotationNotRequired) {
        super(annotationNotRequired);
    }

    @Override
    protected final Object createAttribute(String attributeName, MethodParameter parameter, WebDataBinderFactory binderFactory, NativeWebRequest request) throws Exception {
        Object attribute;
        String value = this.getRequestValueForAttribute(attributeName, request);
        if (value != null && (attribute = this.createAttributeFromRequestValue(value, attributeName, parameter, binderFactory, request)) != null) {
            return attribute;
        }
        return super.createAttribute(attributeName, parameter, binderFactory, request);
    }

    @Nullable
    protected String getRequestValueForAttribute(String attributeName, NativeWebRequest request) {
        Map<String, String> variables = this.getUriTemplateVariables(request);
        String variableValue = variables.get(attributeName);
        if (StringUtils.hasText(variableValue)) {
            return variableValue;
        }
        String parameterValue = request.getParameter(attributeName);
        if (StringUtils.hasText(parameterValue)) {
            return parameterValue;
        }
        return null;
    }

    protected final Map<String, String> getUriTemplateVariables(NativeWebRequest request) {
        Map<String, String> variables = (Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, 0);
        return variables != null ? variables : Collections.emptyMap();
    }

    @Nullable
    protected Object createAttributeFromRequestValue(String sourceValue, String attributeName, MethodParameter parameter, WebDataBinderFactory binderFactory, NativeWebRequest request) throws Exception {
        TypeDescriptor target;
        TypeDescriptor source;
        WebDataBinder binder = binderFactory.createBinder(request, null, attributeName);
        ConversionService conversionService = binder.getConversionService();
        if (conversionService != null && conversionService.canConvert(source = TypeDescriptor.valueOf(String.class), target = new TypeDescriptor(parameter))) {
            return binder.convertIfNecessary((Object)sourceValue, parameter.getParameterType(), parameter);
        }
        return null;
    }

    @Override
    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
        ServletRequest servletRequest = request.getNativeRequest(ServletRequest.class);
        Assert.state(servletRequest != null, "No ServletRequest");
        ServletRequestDataBinder servletBinder = (ServletRequestDataBinder)binder;
        servletBinder.bind(servletRequest);
    }

    @Override
    @Nullable
    public Object resolveConstructorArgument(String paramName, Class<?> paramType, NativeWebRequest request) throws Exception {
        Object value = super.resolveConstructorArgument(paramName, paramType, request);
        if (value != null) {
            return value;
        }
        ServletRequest servletRequest = request.getNativeRequest(ServletRequest.class);
        if (servletRequest != null) {
            String attr = HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
            Map uriVars = (Map)servletRequest.getAttribute(attr);
            return uriVars.get(paramName);
        }
        return null;
    }
}

