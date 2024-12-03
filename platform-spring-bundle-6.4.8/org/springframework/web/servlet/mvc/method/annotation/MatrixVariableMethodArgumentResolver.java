/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MissingMatrixVariableException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;
import org.springframework.web.servlet.HandlerMapping;

public class MatrixVariableMethodArgumentResolver
extends AbstractNamedValueMethodArgumentResolver {
    public MatrixVariableMethodArgumentResolver() {
        super(null);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (!parameter.hasParameterAnnotation(MatrixVariable.class)) {
            return false;
        }
        if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
            MatrixVariable matrixVariable = parameter.getParameterAnnotation(MatrixVariable.class);
            return matrixVariable != null && StringUtils.hasText(matrixVariable.name());
        }
        return true;
    }

    @Override
    protected AbstractNamedValueMethodArgumentResolver.NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        MatrixVariable ann = parameter.getParameterAnnotation(MatrixVariable.class);
        Assert.state(ann != null, "No MatrixVariable annotation");
        return new MatrixVariableNamedValueInfo(ann);
    }

    @Override
    @Nullable
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        Map pathParameters = (Map)request.getAttribute(HandlerMapping.MATRIX_VARIABLES_ATTRIBUTE, 0);
        if (CollectionUtils.isEmpty(pathParameters)) {
            return null;
        }
        MatrixVariable ann = parameter.getParameterAnnotation(MatrixVariable.class);
        Assert.state(ann != null, "No MatrixVariable annotation");
        String pathVar = ann.pathVar();
        List paramValues = null;
        if (!pathVar.equals("\n\t\t\n\t\t\n\ue000\ue001\ue002\n\t\t\t\t\n")) {
            if (pathParameters.containsKey(pathVar)) {
                paramValues = (List)((MultiValueMap)pathParameters.get(pathVar)).get(name);
            }
        } else {
            boolean found = false;
            paramValues = new ArrayList();
            for (MultiValueMap params : pathParameters.values()) {
                if (!params.containsKey(name)) continue;
                if (found) {
                    String paramType = parameter.getNestedParameterType().getName();
                    throw new ServletRequestBindingException("Found more than one match for URI path parameter '" + name + "' for parameter type [" + paramType + "]. Use 'pathVar' attribute to disambiguate.");
                }
                paramValues.addAll((Collection)params.get(name));
                found = true;
            }
        }
        if (CollectionUtils.isEmpty(paramValues)) {
            return null;
        }
        if (paramValues.size() == 1) {
            return paramValues.get(0);
        }
        return paramValues;
    }

    @Override
    protected void handleMissingValue(String name, MethodParameter parameter) throws ServletRequestBindingException {
        throw new MissingMatrixVariableException(name, parameter);
    }

    @Override
    protected void handleMissingValueAfterConversion(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        throw new MissingMatrixVariableException(name, parameter, true);
    }

    private static final class MatrixVariableNamedValueInfo
    extends AbstractNamedValueMethodArgumentResolver.NamedValueInfo {
        private MatrixVariableNamedValueInfo(MatrixVariable annotation) {
            super(annotation.name(), annotation.required(), annotation.defaultValue());
        }
    }
}

