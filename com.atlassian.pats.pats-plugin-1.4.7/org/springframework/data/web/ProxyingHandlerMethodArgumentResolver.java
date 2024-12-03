/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.MutablePropertyValues
 *  org.springframework.beans.PropertyValues
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.ObjectFactory
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.util.ClassUtils
 *  org.springframework.web.bind.WebDataBinder
 *  org.springframework.web.bind.support.WebDataBinderFactory
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.method.annotation.ModelAttributeMethodProcessor
 */
package org.springframework.data.web;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.web.MapDataBinder;
import org.springframework.data.web.ProjectedPayload;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;

public class ProxyingHandlerMethodArgumentResolver
extends ModelAttributeMethodProcessor
implements BeanFactoryAware,
BeanClassLoaderAware {
    private static final List<String> IGNORED_PACKAGES = Arrays.asList("java", "org.springframework");
    private final SpelAwareProxyProjectionFactory proxyFactory = new SpelAwareProxyProjectionFactory();
    private final ObjectFactory<ConversionService> conversionService;

    public ProxyingHandlerMethodArgumentResolver(ObjectFactory<ConversionService> conversionService, boolean annotationNotRequired) {
        super(annotationNotRequired);
        this.conversionService = conversionService;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.proxyFactory.setBeanFactory(beanFactory);
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.proxyFactory.setBeanClassLoader(classLoader);
    }

    public boolean supportsParameter(MethodParameter parameter) {
        if (!super.supportsParameter(parameter)) {
            return false;
        }
        Class type = parameter.getParameterType();
        if (!type.isInterface()) {
            return false;
        }
        if (parameter.getParameterAnnotation(ProjectedPayload.class) != null) {
            return true;
        }
        if (AnnotatedElementUtils.findMergedAnnotation((AnnotatedElement)type, ProjectedPayload.class) != null) {
            return true;
        }
        String packageName = ClassUtils.getPackageName((Class)type);
        return !IGNORED_PACKAGES.stream().anyMatch(it -> packageName.startsWith((String)it));
    }

    protected Object createAttribute(String attributeName, MethodParameter parameter, WebDataBinderFactory binderFactory, NativeWebRequest request) throws Exception {
        MapDataBinder binder = new MapDataBinder(parameter.getParameterType(), (ConversionService)this.conversionService.getObject());
        binder.bind((PropertyValues)new MutablePropertyValues(request.getParameterMap()));
        return this.proxyFactory.createProjection(parameter.getParameterType(), binder.getTarget());
    }

    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
    }
}

