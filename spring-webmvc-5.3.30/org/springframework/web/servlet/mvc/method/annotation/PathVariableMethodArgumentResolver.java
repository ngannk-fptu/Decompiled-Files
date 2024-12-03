/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 *  org.springframework.web.bind.MissingPathVariableException
 *  org.springframework.web.bind.ServletRequestBindingException
 *  org.springframework.web.bind.annotation.PathVariable
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
 *  org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver$NamedValueInfo
 *  org.springframework.web.method.support.ModelAndViewContainer
 *  org.springframework.web.method.support.UriComponentsContributor
 *  org.springframework.web.util.UriComponentsBuilder
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.util.HashMap;
import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.method.support.UriComponentsContributor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.util.UriComponentsBuilder;

public class PathVariableMethodArgumentResolver
extends AbstractNamedValueMethodArgumentResolver
implements UriComponentsContributor {
    private static final TypeDescriptor STRING_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(String.class);

    public boolean supportsParameter(MethodParameter parameter) {
        if (!parameter.hasParameterAnnotation(PathVariable.class)) {
            return false;
        }
        if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
            PathVariable pathVariable = (PathVariable)parameter.getParameterAnnotation(PathVariable.class);
            return pathVariable != null && StringUtils.hasText((String)pathVariable.value());
        }
        return true;
    }

    protected AbstractNamedValueMethodArgumentResolver.NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        PathVariable ann = (PathVariable)parameter.getParameterAnnotation(PathVariable.class);
        Assert.state((ann != null ? 1 : 0) != 0, (String)"No PathVariable annotation");
        return new PathVariableNamedValueInfo(ann);
    }

    @Nullable
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        Map uriTemplateVars = (Map)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, 0);
        return uriTemplateVars != null ? uriTemplateVars.get(name) : null;
    }

    protected void handleMissingValue(String name, MethodParameter parameter) throws ServletRequestBindingException {
        throw new MissingPathVariableException(name, parameter);
    }

    protected void handleMissingValueAfterConversion(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        throw new MissingPathVariableException(name, parameter, true);
    }

    protected void handleResolvedValue(@Nullable Object arg, String name, MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest request) {
        String key = View.PATH_VARIABLES;
        int scope = 0;
        HashMap<String, Object> pathVars = (HashMap<String, Object>)request.getAttribute(key, scope);
        if (pathVars == null) {
            pathVars = new HashMap<String, Object>();
            request.setAttribute(key, pathVars, scope);
        }
        pathVars.put(name, arg);
    }

    public void contributeMethodArgument(MethodParameter parameter, Object value, UriComponentsBuilder builder, Map<String, Object> uriVariables, ConversionService conversionService) {
        if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
            return;
        }
        PathVariable ann = (PathVariable)parameter.getParameterAnnotation(PathVariable.class);
        String name = ann != null && StringUtils.hasLength((String)ann.value()) ? ann.value() : parameter.getParameterName();
        String formatted = this.formatUriValue(conversionService, new TypeDescriptor(parameter.nestedIfOptional()), value);
        uriVariables.put(name, formatted);
    }

    @Nullable
    protected String formatUriValue(@Nullable ConversionService cs, @Nullable TypeDescriptor sourceType, Object value) {
        if (value instanceof String) {
            return (String)value;
        }
        if (cs != null) {
            return (String)cs.convert(value, sourceType, STRING_TYPE_DESCRIPTOR);
        }
        return value.toString();
    }

    private static class PathVariableNamedValueInfo
    extends AbstractNamedValueMethodArgumentResolver.NamedValueInfo {
        public PathVariableNamedValueInfo(PathVariable annotation) {
            super(annotation.name(), annotation.required(), "\n\t\t\n\t\t\n\ue000\ue001\ue002\n\t\t\t\t\n");
        }
    }
}

