/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.DefaultParameterNameDiscoverer
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.ParameterNameDiscoverer
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.data.repository.support.AnnotationAttribute;
import org.springframework.data.util.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

class MethodParameters {
    private final ParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
    private final List<MethodParameter> parameters;

    public MethodParameters(Method method) {
        this(method, Optional.empty());
    }

    public MethodParameters(Method method, Optional<AnnotationAttribute> namingAnnotation) {
        Assert.notNull((Object)method, (String)"Method must not be null!");
        this.parameters = new ArrayList<MethodParameter>();
        for (int i = 0; i < method.getParameterCount(); ++i) {
            AnnotationNamingMethodParameter parameter = new AnnotationNamingMethodParameter(method, i, namingAnnotation);
            parameter.initParameterNameDiscovery(this.discoverer);
            this.parameters.add(parameter);
        }
    }

    public List<MethodParameter> getParameters() {
        return this.parameters;
    }

    public Optional<MethodParameter> getParameter(String name) {
        Assert.hasText((String)name, (String)"Parameter name must not be null!");
        return this.getParameters().stream().filter(it -> name.equals(it.getParameterName())).findFirst();
    }

    public List<MethodParameter> getParametersOfType(Class<?> type) {
        Assert.notNull(type, (String)"Type must not be null!");
        return this.getParameters().stream().filter(it -> it.getParameterType().equals(type)).collect(Collectors.toList());
    }

    public List<MethodParameter> getParametersWith(Class<? extends Annotation> annotation) {
        Assert.notNull(annotation, (String)"Annotation must not be null!");
        return this.getParameters().stream().filter(it -> it.hasParameterAnnotation(annotation)).collect(Collectors.toList());
    }

    private static class AnnotationNamingMethodParameter
    extends MethodParameter {
        private final Optional<AnnotationAttribute> attribute;
        private final Lazy<String> name;

        public AnnotationNamingMethodParameter(Method method, int parameterIndex, Optional<AnnotationAttribute> attribute) {
            super(method, parameterIndex);
            this.attribute = attribute;
            this.name = Lazy.of(() -> this.attribute.flatMap(it -> it.getValueFrom(this).map(Object::toString)).orElseGet(() -> super.getParameterName()));
        }

        @Nullable
        public String getParameterName() {
            return this.name.orElse(null);
        }
    }
}

