/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.util.List;
import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

public class MatrixVariableMapMethodArgumentResolver
implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        MatrixVariable matrixVariable = parameter.getParameterAnnotation(MatrixVariable.class);
        return matrixVariable != null && Map.class.isAssignableFrom(parameter.getParameterType()) && !StringUtils.hasText(matrixVariable.name());
    }

    @Override
    @Nullable
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest request, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        Map matrixVariables = (Map)request.getAttribute(HandlerMapping.MATRIX_VARIABLES_ATTRIBUTE, 0);
        MultiValueMap<String, String> map = this.mapMatrixVariables(parameter, matrixVariables);
        return this.isSingleValueMap(parameter) ? map.toSingleValueMap() : map;
    }

    private MultiValueMap<String, String> mapMatrixVariables(MethodParameter parameter, @Nullable Map<String, MultiValueMap<String, String>> matrixVariables) {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        if (CollectionUtils.isEmpty(matrixVariables)) {
            return map;
        }
        MatrixVariable ann = parameter.getParameterAnnotation(MatrixVariable.class);
        Assert.state(ann != null, "No MatrixVariable annotation");
        String pathVariable = ann.pathVar();
        if (!pathVariable.equals("\n\t\t\n\t\t\n\ue000\ue001\ue002\n\t\t\t\t\n")) {
            MultiValueMap<String, String> mapForPathVariable = matrixVariables.get(pathVariable);
            if (mapForPathVariable == null) {
                return map;
            }
            map.putAll((Map<String, String>)mapForPathVariable);
        } else {
            for (MultiValueMap<String, String> vars : matrixVariables.values()) {
                vars.forEach((name, values) -> {
                    for (String value : values) {
                        map.add((String)name, value);
                    }
                });
            }
        }
        return map;
    }

    private boolean isSingleValueMap(MethodParameter parameter) {
        ResolvableType[] genericTypes;
        if (!MultiValueMap.class.isAssignableFrom(parameter.getParameterType()) && (genericTypes = ResolvableType.forMethodParameter(parameter).getGenerics()).length == 2) {
            return !List.class.isAssignableFrom(genericTypes[1].toClass());
        }
        return false;
    }
}

