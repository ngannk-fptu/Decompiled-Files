/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.jayway.jsonpath.spi.json.JacksonJsonProvider
 *  com.jayway.jsonpath.spi.json.JsonProvider
 *  com.jayway.jsonpath.spi.mapper.JacksonMappingProvider
 *  com.jayway.jsonpath.spi.mapper.MappingProvider
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.http.HttpInputMessage
 *  org.springframework.http.MediaType
 *  org.springframework.http.converter.HttpMessageNotReadableException
 *  org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ConcurrentReferenceHashMap
 */
package org.springframework.data.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.web.JsonProjectingMethodInterceptorFactory;
import org.springframework.data.web.ProjectedPayload;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

public class ProjectingJackson2HttpMessageConverter
extends MappingJackson2HttpMessageConverter
implements BeanClassLoaderAware,
BeanFactoryAware {
    private final SpelAwareProxyProjectionFactory projectionFactory;
    private final Map<Class<?>, Boolean> supportedTypesCache = new ConcurrentReferenceHashMap();

    public ProjectingJackson2HttpMessageConverter() {
        this.projectionFactory = ProjectingJackson2HttpMessageConverter.initProjectionFactory(this.getObjectMapper());
    }

    public ProjectingJackson2HttpMessageConverter(ObjectMapper mapper) {
        super(mapper);
        this.projectionFactory = ProjectingJackson2HttpMessageConverter.initProjectionFactory(mapper);
    }

    private static SpelAwareProxyProjectionFactory initProjectionFactory(ObjectMapper mapper) {
        Assert.notNull((Object)mapper, (String)"ObjectMapper must not be null!");
        SpelAwareProxyProjectionFactory projectionFactory = new SpelAwareProxyProjectionFactory();
        projectionFactory.registerMethodInvokerFactory(new JsonProjectingMethodInterceptorFactory((JsonProvider)new JacksonJsonProvider(mapper), (MappingProvider)new JacksonMappingProvider(mapper)));
        return projectionFactory;
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.projectionFactory.setBeanClassLoader(classLoader);
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.projectionFactory.setBeanFactory(beanFactory);
    }

    public boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType) {
        if (!this.canRead(mediaType)) {
            return false;
        }
        ResolvableType owner = contextClass == null ? null : ResolvableType.forClass(contextClass);
        Class rawType = ResolvableType.forType((Type)type, (ResolvableType)owner).resolve(Object.class);
        Boolean result = this.supportedTypesCache.get(rawType);
        if (result != null) {
            return result;
        }
        result = rawType.isInterface() && AnnotationUtils.findAnnotation((Class)rawType, ProjectedPayload.class) != null;
        this.supportedTypesCache.put(rawType, result);
        return result;
    }

    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        return false;
    }

    public Object read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return this.projectionFactory.createProjection(ResolvableType.forType((Type)type).resolve(Object.class), (Object)inputMessage.getBody());
    }
}

