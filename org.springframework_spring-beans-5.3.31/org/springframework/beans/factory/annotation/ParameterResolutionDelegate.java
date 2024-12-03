/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.core.annotation.SynthesizingMethodParameter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.beans.factory.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public final class ParameterResolutionDelegate {
    private static final AnnotatedElement EMPTY_ANNOTATED_ELEMENT = new AnnotatedElement(){

        @Override
        @Nullable
        public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
            return null;
        }

        @Override
        public Annotation[] getAnnotations() {
            return new Annotation[0];
        }

        @Override
        public Annotation[] getDeclaredAnnotations() {
            return new Annotation[0];
        }
    };

    private ParameterResolutionDelegate() {
    }

    public static boolean isAutowirable(Parameter parameter, int parameterIndex) {
        Assert.notNull((Object)parameter, (String)"Parameter must not be null");
        AnnotatedElement annotatedParameter = ParameterResolutionDelegate.getEffectiveAnnotatedParameter(parameter, parameterIndex);
        return AnnotatedElementUtils.hasAnnotation((AnnotatedElement)annotatedParameter, Autowired.class) || AnnotatedElementUtils.hasAnnotation((AnnotatedElement)annotatedParameter, Qualifier.class) || AnnotatedElementUtils.hasAnnotation((AnnotatedElement)annotatedParameter, Value.class);
    }

    @Nullable
    public static Object resolveDependency(Parameter parameter, int parameterIndex, Class<?> containingClass, AutowireCapableBeanFactory beanFactory) throws BeansException {
        Assert.notNull((Object)parameter, (String)"Parameter must not be null");
        Assert.notNull(containingClass, (String)"Containing class must not be null");
        Assert.notNull((Object)beanFactory, (String)"AutowireCapableBeanFactory must not be null");
        AnnotatedElement annotatedParameter = ParameterResolutionDelegate.getEffectiveAnnotatedParameter(parameter, parameterIndex);
        Autowired autowired = (Autowired)AnnotatedElementUtils.findMergedAnnotation((AnnotatedElement)annotatedParameter, Autowired.class);
        boolean required = autowired == null || autowired.required();
        SynthesizingMethodParameter methodParameter = SynthesizingMethodParameter.forExecutable((Executable)parameter.getDeclaringExecutable(), (int)parameterIndex);
        DependencyDescriptor descriptor = new DependencyDescriptor((MethodParameter)methodParameter, required);
        descriptor.setContainingClass(containingClass);
        return beanFactory.resolveDependency(descriptor, null);
    }

    private static AnnotatedElement getEffectiveAnnotatedParameter(Parameter parameter, int index) {
        Executable executable = parameter.getDeclaringExecutable();
        if (executable instanceof Constructor && ClassUtils.isInnerClass(executable.getDeclaringClass()) && executable.getParameterAnnotations().length == executable.getParameterCount() - 1) {
            return index == 0 ? EMPTY_ANNOTATED_ELEMENT : executable.getParameters()[index - 1];
        }
        return parameter;
    }
}

