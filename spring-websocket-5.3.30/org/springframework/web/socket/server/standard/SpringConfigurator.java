/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.server.ServerEndpointConfig$Configurator
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.stereotype.Component
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.web.context.ContextLoader
 *  org.springframework.web.context.WebApplicationContext
 */
package org.springframework.web.socket.server.standard;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.server.ServerEndpointConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

public class SpringConfigurator
extends ServerEndpointConfig.Configurator {
    private static final String NO_VALUE = ObjectUtils.identityToString((Object)new Object());
    private static final Log logger = LogFactory.getLog(SpringConfigurator.class);
    private static final Map<String, Map<Class<?>, String>> cache = new ConcurrentHashMap();

    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
        if (wac == null) {
            String message = "Failed to find the root WebApplicationContext. Was ContextLoaderListener not used?";
            logger.error((Object)message);
            throw new IllegalStateException(message);
        }
        String beanName = ClassUtils.getShortNameAsProperty(endpointClass);
        if (wac.containsBean(beanName)) {
            Object endpoint = wac.getBean(beanName, endpointClass);
            if (logger.isTraceEnabled()) {
                logger.trace((Object)("Using @ServerEndpoint singleton " + endpoint));
            }
            return (T)endpoint;
        }
        Component ann = (Component)AnnotationUtils.findAnnotation(endpointClass, Component.class);
        if (ann != null && wac.containsBean(ann.value())) {
            Object endpoint = wac.getBean(ann.value(), endpointClass);
            if (logger.isTraceEnabled()) {
                logger.trace((Object)("Using @ServerEndpoint singleton " + endpoint));
            }
            return (T)endpoint;
        }
        beanName = this.getBeanNameByType(wac, endpointClass);
        if (beanName != null) {
            return (T)wac.getBean(beanName);
        }
        if (logger.isTraceEnabled()) {
            logger.trace((Object)("Creating new @ServerEndpoint instance of type " + endpointClass));
        }
        return (T)wac.getAutowireCapableBeanFactory().createBean(endpointClass);
    }

    @Nullable
    private String getBeanNameByType(WebApplicationContext wac, Class<?> endpointClass) {
        String beanName;
        String wacId = wac.getId();
        Map<Class<?>, String> beanNamesByType = cache.get(wacId);
        if (beanNamesByType == null) {
            beanNamesByType = new ConcurrentHashMap();
            cache.put(wacId, beanNamesByType);
        }
        if (!beanNamesByType.containsKey(endpointClass)) {
            Object[] names = wac.getBeanNamesForType(endpointClass);
            if (names.length == 1) {
                beanNamesByType.put(endpointClass, names[0]);
            } else {
                beanNamesByType.put(endpointClass, NO_VALUE);
                if (names.length > 1) {
                    throw new IllegalStateException("Found multiple @ServerEndpoint's of type [" + endpointClass.getName() + "]: bean names " + Arrays.toString(names));
                }
            }
        }
        return NO_VALUE.equals(beanName = beanNamesByType.get(endpointClass)) ? null : beanName;
    }
}

