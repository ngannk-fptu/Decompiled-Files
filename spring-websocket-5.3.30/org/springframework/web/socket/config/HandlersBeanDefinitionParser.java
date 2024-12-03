/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.ConstructorArgumentValues
 *  org.springframework.beans.factory.config.RuntimeBeanReference
 *  org.springframework.beans.factory.parsing.BeanComponentDefinition
 *  org.springframework.beans.factory.parsing.ComponentDefinition
 *  org.springframework.beans.factory.parsing.CompositeComponentDefinition
 *  org.springframework.beans.factory.support.ManagedList
 *  org.springframework.beans.factory.support.ManagedMap
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.util.xml.DomUtils
 */
package org.springframework.web.socket.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.socket.config.WebSocketNamespaceUtils;
import org.springframework.web.socket.server.support.OriginHandshakeInterceptor;
import org.springframework.web.socket.server.support.WebSocketHandlerMapping;
import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;
import org.springframework.web.socket.sockjs.support.SockJsHttpRequestHandler;
import org.w3c.dom.Element;

class HandlersBeanDefinitionParser
implements BeanDefinitionParser {
    private static final String SOCK_JS_SCHEDULER_NAME = "SockJsScheduler";
    private static final int DEFAULT_MAPPING_ORDER = 1;

    HandlersBeanDefinitionParser() {
    }

    @Nullable
    public BeanDefinition parse(Element element, ParserContext context) {
        HandlerMappingStrategy strategy;
        Object source = context.extractSource((Object)element);
        CompositeComponentDefinition compDefinition = new CompositeComponentDefinition(element.getTagName(), source);
        context.pushContainingComponent(compDefinition);
        String orderAttribute = element.getAttribute("order");
        int order = orderAttribute.isEmpty() ? 1 : Integer.parseInt(orderAttribute);
        RootBeanDefinition handlerMappingDef = new RootBeanDefinition(WebSocketHandlerMapping.class);
        handlerMappingDef.setSource(source);
        handlerMappingDef.setRole(2);
        handlerMappingDef.getPropertyValues().add("order", (Object)order);
        String handlerMappingName = context.getReaderContext().registerWithGeneratedName((BeanDefinition)handlerMappingDef);
        RuntimeBeanReference sockJsService = WebSocketNamespaceUtils.registerSockJsService(element, SOCK_JS_SCHEDULER_NAME, context, source);
        if (sockJsService != null) {
            strategy = new SockJsHandlerMappingStrategy(sockJsService);
        } else {
            RuntimeBeanReference handler = WebSocketNamespaceUtils.registerHandshakeHandler(element, context, source);
            Element interceptElem = DomUtils.getChildElementByTagName((Element)element, (String)"handshake-interceptors");
            ManagedList<Object> interceptors = WebSocketNamespaceUtils.parseBeanSubElements(interceptElem, context);
            String allowedOrigins = element.getAttribute("allowed-origins");
            List<String> origins = Arrays.asList(StringUtils.tokenizeToStringArray((String)allowedOrigins, (String)","));
            String allowedOriginPatterns = element.getAttribute("allowed-origin-patterns");
            List<String> originPatterns = Arrays.asList(StringUtils.tokenizeToStringArray((String)allowedOriginPatterns, (String)","));
            OriginHandshakeInterceptor interceptor = new OriginHandshakeInterceptor(origins);
            if (!ObjectUtils.isEmpty(originPatterns)) {
                interceptor.setAllowedOriginPatterns(originPatterns);
            }
            interceptors.add((Object)interceptor);
            strategy = new WebSocketHandlerMappingStrategy(handler, interceptors);
        }
        ManagedMap urlMap = new ManagedMap();
        urlMap.setSource(source);
        for (Element mappingElement : DomUtils.getChildElementsByTagName((Element)element, (String)"mapping")) {
            strategy.addMapping(mappingElement, (ManagedMap<String, Object>)urlMap, context);
        }
        handlerMappingDef.getPropertyValues().add("urlMap", (Object)urlMap);
        context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)handlerMappingDef, handlerMappingName));
        context.popAndRegisterContainingComponent();
        return null;
    }

    private static class SockJsHandlerMappingStrategy
    implements HandlerMappingStrategy {
        private final RuntimeBeanReference sockJsService;

        public SockJsHandlerMappingStrategy(RuntimeBeanReference sockJsService) {
            this.sockJsService = sockJsService;
        }

        @Override
        public void addMapping(Element element, ManagedMap<String, Object> urlMap, ParserContext context) {
            String pathAttribute = element.getAttribute("path");
            String[] mappings = StringUtils.tokenizeToStringArray((String)pathAttribute, (String)",");
            RuntimeBeanReference handlerReference = new RuntimeBeanReference(element.getAttribute("handler"));
            ConstructorArgumentValues cargs = new ConstructorArgumentValues();
            cargs.addIndexedArgumentValue(0, (Object)this.sockJsService, "SockJsService");
            cargs.addIndexedArgumentValue(1, (Object)handlerReference, "WebSocketHandler");
            RootBeanDefinition requestHandlerDef = new RootBeanDefinition(SockJsHttpRequestHandler.class, cargs, null);
            requestHandlerDef.setSource(context.extractSource((Object)element));
            requestHandlerDef.setRole(2);
            String requestHandlerName = context.getReaderContext().registerWithGeneratedName((BeanDefinition)requestHandlerDef);
            RuntimeBeanReference requestHandlerRef = new RuntimeBeanReference(requestHandlerName);
            for (String mapping : mappings) {
                String pathPattern = mapping.endsWith("/") ? mapping + "**" : mapping + "/**";
                urlMap.put((Object)pathPattern, (Object)requestHandlerRef);
            }
        }
    }

    private static class WebSocketHandlerMappingStrategy
    implements HandlerMappingStrategy {
        private final RuntimeBeanReference handshakeHandlerReference;
        private final ManagedList<?> interceptorsList;

        public WebSocketHandlerMappingStrategy(RuntimeBeanReference handshakeHandler, ManagedList<?> interceptors) {
            this.handshakeHandlerReference = handshakeHandler;
            this.interceptorsList = interceptors;
        }

        @Override
        public void addMapping(Element element, ManagedMap<String, Object> urlMap, ParserContext context) {
            String pathAttribute = element.getAttribute("path");
            String[] mappings = StringUtils.tokenizeToStringArray((String)pathAttribute, (String)",");
            RuntimeBeanReference handlerReference = new RuntimeBeanReference(element.getAttribute("handler"));
            ConstructorArgumentValues cargs = new ConstructorArgumentValues();
            cargs.addIndexedArgumentValue(0, (Object)handlerReference);
            cargs.addIndexedArgumentValue(1, (Object)this.handshakeHandlerReference);
            RootBeanDefinition requestHandlerDef = new RootBeanDefinition(WebSocketHttpRequestHandler.class, cargs, null);
            requestHandlerDef.setSource(context.extractSource((Object)element));
            requestHandlerDef.setRole(2);
            requestHandlerDef.getPropertyValues().add("handshakeInterceptors", this.interceptorsList);
            String requestHandlerName = context.getReaderContext().registerWithGeneratedName((BeanDefinition)requestHandlerDef);
            RuntimeBeanReference requestHandlerRef = new RuntimeBeanReference(requestHandlerName);
            for (String mapping : mappings) {
                urlMap.put((Object)mapping, (Object)requestHandlerRef);
            }
        }
    }

    private static interface HandlerMappingStrategy {
        public void addMapping(Element var1, ManagedMap<String, Object> var2, ParserContext var3);
    }
}

