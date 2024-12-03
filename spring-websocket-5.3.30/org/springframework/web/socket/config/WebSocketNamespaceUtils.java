/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.RuntimeBeanReference
 *  org.springframework.beans.factory.parsing.BeanComponentDefinition
 *  org.springframework.beans.factory.parsing.ComponentDefinition
 *  org.springframework.beans.factory.support.ManagedList
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.lang.Nullable
 *  org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
 *  org.springframework.util.StringUtils
 *  org.springframework.util.xml.DomUtils
 */
package org.springframework.web.socket.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.OriginHandshakeInterceptor;
import org.springframework.web.socket.sockjs.transport.TransportHandlingSockJsService;
import org.springframework.web.socket.sockjs.transport.handler.DefaultSockJsService;
import org.springframework.web.socket.sockjs.transport.handler.WebSocketTransportHandler;
import org.w3c.dom.Element;

abstract class WebSocketNamespaceUtils {
    WebSocketNamespaceUtils() {
    }

    public static RuntimeBeanReference registerHandshakeHandler(Element element, ParserContext context, @Nullable Object source) {
        RuntimeBeanReference handlerRef;
        Element handlerElem = DomUtils.getChildElementByTagName((Element)element, (String)"handshake-handler");
        if (handlerElem != null) {
            handlerRef = new RuntimeBeanReference(handlerElem.getAttribute("ref"));
        } else {
            RootBeanDefinition defaultHandlerDef = new RootBeanDefinition(DefaultHandshakeHandler.class);
            defaultHandlerDef.setSource(source);
            defaultHandlerDef.setRole(2);
            String handlerName = context.getReaderContext().registerWithGeneratedName((BeanDefinition)defaultHandlerDef);
            handlerRef = new RuntimeBeanReference(handlerName);
        }
        return handlerRef;
    }

    @Nullable
    public static RuntimeBeanReference registerSockJsService(Element element, String schedulerName, ParserContext context, @Nullable Object source) {
        Element sockJsElement = DomUtils.getChildElementByTagName((Element)element, (String)"sockjs");
        if (sockJsElement != null) {
            Element handshakeHandler = DomUtils.getChildElementByTagName((Element)element, (String)"handshake-handler");
            RootBeanDefinition sockJsServiceDef = new RootBeanDefinition(DefaultSockJsService.class);
            sockJsServiceDef.setSource(source);
            String customTaskSchedulerName = sockJsElement.getAttribute("scheduler");
            RuntimeBeanReference scheduler = !customTaskSchedulerName.isEmpty() ? new RuntimeBeanReference(customTaskSchedulerName) : WebSocketNamespaceUtils.registerScheduler(schedulerName, context, source);
            sockJsServiceDef.getConstructorArgumentValues().addIndexedArgumentValue(0, (Object)scheduler);
            Element transportHandlersElement = DomUtils.getChildElementByTagName((Element)sockJsElement, (String)"transport-handlers");
            if (transportHandlersElement != null) {
                String registerDefaults = transportHandlersElement.getAttribute("register-defaults");
                if (registerDefaults.equals("false")) {
                    sockJsServiceDef.setBeanClass(TransportHandlingSockJsService.class);
                }
                ManagedList<Object> transportHandlers = WebSocketNamespaceUtils.parseBeanSubElements(transportHandlersElement, context);
                sockJsServiceDef.getConstructorArgumentValues().addIndexedArgumentValue(1, transportHandlers);
            } else if (handshakeHandler != null) {
                RuntimeBeanReference handshakeHandlerRef = new RuntimeBeanReference(handshakeHandler.getAttribute("ref"));
                RootBeanDefinition transportHandler = new RootBeanDefinition(WebSocketTransportHandler.class);
                transportHandler.setSource(source);
                transportHandler.getConstructorArgumentValues().addIndexedArgumentValue(0, (Object)handshakeHandlerRef);
                sockJsServiceDef.getConstructorArgumentValues().addIndexedArgumentValue(1, (Object)transportHandler);
            }
            Element interceptElem = DomUtils.getChildElementByTagName((Element)element, (String)"handshake-interceptors");
            ManagedList<Object> interceptors = WebSocketNamespaceUtils.parseBeanSubElements(interceptElem, context);
            String allowedOrigins = element.getAttribute("allowed-origins");
            List<String> origins = Arrays.asList(StringUtils.tokenizeToStringArray((String)allowedOrigins, (String)","));
            sockJsServiceDef.getPropertyValues().add("allowedOrigins", origins);
            String allowedOriginPatterns = element.getAttribute("allowed-origin-patterns");
            List<String> originPatterns = Arrays.asList(StringUtils.tokenizeToStringArray((String)allowedOriginPatterns, (String)","));
            sockJsServiceDef.getPropertyValues().add("allowedOriginPatterns", originPatterns);
            RootBeanDefinition originHandshakeInterceptor = new RootBeanDefinition(OriginHandshakeInterceptor.class);
            originHandshakeInterceptor.getPropertyValues().add("allowedOrigins", origins);
            originHandshakeInterceptor.getPropertyValues().add("allowedOriginPatterns", originPatterns);
            interceptors.add((Object)originHandshakeInterceptor);
            sockJsServiceDef.getPropertyValues().add("handshakeInterceptors", interceptors);
            String attrValue = sockJsElement.getAttribute("name");
            if (!attrValue.isEmpty()) {
                sockJsServiceDef.getPropertyValues().add("name", (Object)attrValue);
            }
            if (!(attrValue = sockJsElement.getAttribute("websocket-enabled")).isEmpty()) {
                sockJsServiceDef.getPropertyValues().add("webSocketEnabled", (Object)Boolean.valueOf(attrValue));
            }
            if (!(attrValue = sockJsElement.getAttribute("session-cookie-needed")).isEmpty()) {
                sockJsServiceDef.getPropertyValues().add("sessionCookieNeeded", (Object)Boolean.valueOf(attrValue));
            }
            if (!(attrValue = sockJsElement.getAttribute("stream-bytes-limit")).isEmpty()) {
                sockJsServiceDef.getPropertyValues().add("streamBytesLimit", (Object)Integer.valueOf(attrValue));
            }
            if (!(attrValue = sockJsElement.getAttribute("disconnect-delay")).isEmpty()) {
                sockJsServiceDef.getPropertyValues().add("disconnectDelay", (Object)Long.valueOf(attrValue));
            }
            if (!(attrValue = sockJsElement.getAttribute("message-cache-size")).isEmpty()) {
                sockJsServiceDef.getPropertyValues().add("httpMessageCacheSize", (Object)Integer.valueOf(attrValue));
            }
            if (!(attrValue = sockJsElement.getAttribute("heartbeat-time")).isEmpty()) {
                sockJsServiceDef.getPropertyValues().add("heartbeatTime", (Object)Long.valueOf(attrValue));
            }
            if (!(attrValue = sockJsElement.getAttribute("client-library-url")).isEmpty()) {
                sockJsServiceDef.getPropertyValues().add("sockJsClientLibraryUrl", (Object)attrValue);
            }
            if (!(attrValue = sockJsElement.getAttribute("message-codec")).isEmpty()) {
                sockJsServiceDef.getPropertyValues().add("messageCodec", (Object)new RuntimeBeanReference(attrValue));
            }
            if (!(attrValue = sockJsElement.getAttribute("suppress-cors")).isEmpty()) {
                sockJsServiceDef.getPropertyValues().add("suppressCors", (Object)Boolean.valueOf(attrValue));
            }
            sockJsServiceDef.setRole(2);
            String sockJsServiceName = context.getReaderContext().registerWithGeneratedName((BeanDefinition)sockJsServiceDef);
            return new RuntimeBeanReference(sockJsServiceName);
        }
        return null;
    }

    public static RuntimeBeanReference registerScheduler(String schedulerName, ParserContext context, @Nullable Object source) {
        if (!context.getRegistry().containsBeanDefinition(schedulerName)) {
            RootBeanDefinition taskSchedulerDef = new RootBeanDefinition(ThreadPoolTaskScheduler.class);
            taskSchedulerDef.setSource(source);
            taskSchedulerDef.setRole(2);
            taskSchedulerDef.getPropertyValues().add("poolSize", (Object)Runtime.getRuntime().availableProcessors());
            taskSchedulerDef.getPropertyValues().add("threadNamePrefix", (Object)(schedulerName + "-"));
            taskSchedulerDef.getPropertyValues().add("removeOnCancelPolicy", (Object)true);
            context.getRegistry().registerBeanDefinition(schedulerName, (BeanDefinition)taskSchedulerDef);
            context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)taskSchedulerDef, schedulerName));
        }
        return new RuntimeBeanReference(schedulerName);
    }

    public static ManagedList<Object> parseBeanSubElements(@Nullable Element parentElement, ParserContext context) {
        ManagedList beans = new ManagedList();
        if (parentElement != null) {
            beans.setSource(context.extractSource((Object)parentElement));
            for (Element beanElement : DomUtils.getChildElementsByTagName((Element)parentElement, (String[])new String[]{"bean", "ref"})) {
                beans.add(context.getDelegate().parsePropertySubElement(beanElement, null));
            }
        }
        return beans;
    }
}

