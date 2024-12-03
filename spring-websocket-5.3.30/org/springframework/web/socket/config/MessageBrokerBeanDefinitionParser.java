/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.MutablePropertyValues
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.ConstructorArgumentValues
 *  org.springframework.beans.factory.config.CustomScopeConfigurer
 *  org.springframework.beans.factory.config.RuntimeBeanReference
 *  org.springframework.beans.factory.parsing.BeanComponentDefinition
 *  org.springframework.beans.factory.parsing.ComponentDefinition
 *  org.springframework.beans.factory.parsing.CompositeComponentDefinition
 *  org.springframework.beans.factory.support.GenericBeanDefinition
 *  org.springframework.beans.factory.support.ManagedList
 *  org.springframework.beans.factory.support.ManagedMap
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean
 *  org.springframework.lang.Nullable
 *  org.springframework.messaging.converter.ByteArrayMessageConverter
 *  org.springframework.messaging.converter.CompositeMessageConverter
 *  org.springframework.messaging.converter.DefaultContentTypeResolver
 *  org.springframework.messaging.converter.GsonMessageConverter
 *  org.springframework.messaging.converter.JsonbMessageConverter
 *  org.springframework.messaging.converter.MappingJackson2MessageConverter
 *  org.springframework.messaging.converter.StringMessageConverter
 *  org.springframework.messaging.simp.SimpMessagingTemplate
 *  org.springframework.messaging.simp.SimpSessionScope
 *  org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler
 *  org.springframework.messaging.simp.stomp.StompBrokerRelayMessageHandler
 *  org.springframework.messaging.simp.user.DefaultUserDestinationResolver
 *  org.springframework.messaging.simp.user.MultiServerUserRegistry
 *  org.springframework.messaging.simp.user.UserDestinationMessageHandler
 *  org.springframework.messaging.simp.user.UserRegistryMessageHandler
 *  org.springframework.messaging.support.ExecutorSubscribableChannel
 *  org.springframework.messaging.support.ImmutableMessageChannelInterceptor
 *  org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.MimeTypeUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.util.xml.DomUtils
 */
package org.springframework.web.socket.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.GsonMessageConverter;
import org.springframework.messaging.converter.JsonbMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.SimpSessionScope;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.messaging.simp.stomp.StompBrokerRelayMessageHandler;
import org.springframework.messaging.simp.user.DefaultUserDestinationResolver;
import org.springframework.messaging.simp.user.MultiServerUserRegistry;
import org.springframework.messaging.simp.user.UserDestinationMessageHandler;
import org.springframework.messaging.simp.user.UserRegistryMessageHandler;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.messaging.support.ImmutableMessageChannelInterceptor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;
import org.springframework.web.socket.config.WebSocketNamespaceUtils;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;
import org.springframework.web.socket.messaging.WebSocketAnnotationMethodMessageHandler;
import org.springframework.web.socket.server.support.OriginHandshakeInterceptor;
import org.springframework.web.socket.server.support.WebSocketHandlerMapping;
import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;
import org.springframework.web.socket.sockjs.support.SockJsHttpRequestHandler;
import org.w3c.dom.Element;

class MessageBrokerBeanDefinitionParser
implements BeanDefinitionParser {
    public static final String WEB_SOCKET_HANDLER_BEAN_NAME = "subProtocolWebSocketHandler";
    public static final String SCHEDULER_BEAN_NAME = "messageBrokerScheduler";
    public static final String SOCKJS_SCHEDULER_BEAN_NAME = "messageBrokerSockJsScheduler";
    public static final String MESSAGING_TEMPLATE_BEAN_NAME = "brokerMessagingTemplate";
    public static final String MESSAGE_CONVERTER_BEAN_NAME = "brokerMessageConverter";
    private static final int DEFAULT_MAPPING_ORDER = 1;
    private static final boolean jackson2Present;
    private static final boolean gsonPresent;
    private static final boolean jsonbPresent;
    private static final boolean javaxValidationPresent;

    MessageBrokerBeanDefinitionParser() {
    }

    public BeanDefinition parse(Element element, ParserContext context) {
        Object source = context.extractSource((Object)element);
        CompositeComponentDefinition compDefinition = new CompositeComponentDefinition(element.getTagName(), source);
        context.pushContainingComponent(compDefinition);
        Element channelElem = DomUtils.getChildElementByTagName((Element)element, (String)"client-inbound-channel");
        RuntimeBeanReference inChannel = this.getMessageChannel("clientInboundChannel", channelElem, context, source);
        channelElem = DomUtils.getChildElementByTagName((Element)element, (String)"client-outbound-channel");
        RuntimeBeanReference outChannel = this.getMessageChannel("clientOutboundChannel", channelElem, context, source);
        channelElem = DomUtils.getChildElementByTagName((Element)element, (String)"broker-channel");
        RuntimeBeanReference brokerChannel = this.getMessageChannel("brokerChannel", channelElem, context, source);
        RuntimeBeanReference userRegistry = this.registerUserRegistry(element, context, source);
        RuntimeBeanReference userDestHandler = this.registerUserDestHandler(element, userRegistry, inChannel, brokerChannel, context, source);
        RuntimeBeanReference converter = this.registerMessageConverter(element, context, source);
        RuntimeBeanReference template = this.registerMessagingTemplate(element, brokerChannel, converter, context, source);
        this.registerAnnotationMethodMessageHandler(element, inChannel, outChannel, converter, template, context, source);
        RootBeanDefinition broker = this.registerMessageBroker(element, inChannel, outChannel, brokerChannel, userDestHandler, template, userRegistry, context, source);
        ManagedMap<String, Object> urlMap = this.registerHandlerMapping(element, context, source);
        RuntimeBeanReference stompHandler = this.registerStompHandler(element, inChannel, outChannel, context, source);
        for (Element endpointElem : DomUtils.getChildElementsByTagName((Element)element, (String)"stomp-endpoint")) {
            RuntimeBeanReference requestHandler = this.registerRequestHandler(endpointElem, stompHandler, context, source);
            String pathAttribute = endpointElem.getAttribute("path");
            Assert.hasText((String)pathAttribute, (String)"Invalid <stomp-endpoint> (no path mapping)");
            for (String path : StringUtils.tokenizeToStringArray((String)pathAttribute, (String)",")) {
                path = path.trim();
                Assert.hasText((String)path, () -> "Invalid <stomp-endpoint> path attribute: " + pathAttribute);
                if (DomUtils.getChildElementByTagName((Element)endpointElem, (String)"sockjs") != null) {
                    path = path.endsWith("/") ? path + "**" : path + "/**";
                }
                urlMap.put((Object)path, (Object)requestHandler);
            }
        }
        Map<String, SimpSessionScope> scopeMap = Collections.singletonMap("websocket", new SimpSessionScope());
        RootBeanDefinition scopeConfigurer = new RootBeanDefinition(CustomScopeConfigurer.class);
        scopeConfigurer.getPropertyValues().add("scopes", scopeMap);
        MessageBrokerBeanDefinitionParser.registerBeanDefByName("webSocketScopeConfigurer", scopeConfigurer, context, source);
        this.registerWebSocketMessageBrokerStats(broker, inChannel, outChannel, context, source);
        context.popAndRegisterContainingComponent();
        return null;
    }

    private RuntimeBeanReference registerUserRegistry(Element element, ParserContext context, @Nullable Object source) {
        boolean multiServer;
        Element relayElement = DomUtils.getChildElementByTagName((Element)element, (String)"stomp-broker-relay");
        boolean bl = multiServer = relayElement != null && relayElement.hasAttribute("user-registry-broadcast");
        if (multiServer) {
            RootBeanDefinition localRegistryBeanDef = new RootBeanDefinition(DefaultSimpUserRegistry.class);
            RootBeanDefinition beanDef = new RootBeanDefinition(MultiServerUserRegistry.class);
            beanDef.getConstructorArgumentValues().addIndexedArgumentValue(0, (Object)localRegistryBeanDef);
            String beanName = MessageBrokerBeanDefinitionParser.registerBeanDef(beanDef, context, source);
            return new RuntimeBeanReference(beanName);
        }
        RootBeanDefinition beanDef = new RootBeanDefinition(DefaultSimpUserRegistry.class);
        String beanName = MessageBrokerBeanDefinitionParser.registerBeanDef(beanDef, context, source);
        return new RuntimeBeanReference(beanName);
    }

    private ManagedMap<String, Object> registerHandlerMapping(Element element, ParserContext context, @Nullable Object source) {
        RootBeanDefinition handlerMappingDef = new RootBeanDefinition(WebSocketHandlerMapping.class);
        String orderAttribute = element.getAttribute("order");
        int order = orderAttribute.isEmpty() ? 1 : Integer.parseInt(orderAttribute);
        handlerMappingDef.getPropertyValues().add("order", (Object)order);
        String pathHelper = element.getAttribute("path-helper");
        if (StringUtils.hasText((String)pathHelper)) {
            handlerMappingDef.getPropertyValues().add("urlPathHelper", (Object)new RuntimeBeanReference(pathHelper));
        }
        ManagedMap urlMap = new ManagedMap();
        urlMap.setSource(source);
        handlerMappingDef.getPropertyValues().add("urlMap", (Object)urlMap);
        MessageBrokerBeanDefinitionParser.registerBeanDef(handlerMappingDef, context, source);
        return urlMap;
    }

    private RuntimeBeanReference getMessageChannel(String name, @Nullable Element element, ParserContext context, @Nullable Object source) {
        RootBeanDefinition executor;
        if (element == null) {
            executor = this.getDefaultExecutorBeanDefinition(name);
        } else {
            Element executorElem = DomUtils.getChildElementByTagName((Element)element, (String)"executor");
            if (executorElem == null) {
                executor = this.getDefaultExecutorBeanDefinition(name);
            } else {
                executor = new RootBeanDefinition(ThreadPoolTaskExecutor.class);
                if (executorElem.hasAttribute("core-pool-size")) {
                    executor.getPropertyValues().add("corePoolSize", (Object)executorElem.getAttribute("core-pool-size"));
                }
                if (executorElem.hasAttribute("max-pool-size")) {
                    executor.getPropertyValues().add("maxPoolSize", (Object)executorElem.getAttribute("max-pool-size"));
                }
                if (executorElem.hasAttribute("keep-alive-seconds")) {
                    executor.getPropertyValues().add("keepAliveSeconds", (Object)executorElem.getAttribute("keep-alive-seconds"));
                }
                if (executorElem.hasAttribute("queue-capacity")) {
                    executor.getPropertyValues().add("queueCapacity", (Object)executorElem.getAttribute("queue-capacity"));
                }
            }
        }
        ConstructorArgumentValues cargs = new ConstructorArgumentValues();
        if (executor != null) {
            executor.getPropertyValues().add("threadNamePrefix", (Object)(name + "-"));
            String executorName = name + "Executor";
            MessageBrokerBeanDefinitionParser.registerBeanDefByName(executorName, executor, context, source);
            cargs.addIndexedArgumentValue(0, (Object)new RuntimeBeanReference(executorName));
        }
        RootBeanDefinition channelDef = new RootBeanDefinition(ExecutorSubscribableChannel.class, cargs, null);
        ManagedList interceptors = new ManagedList();
        if (element != null) {
            Element interceptorsElement = DomUtils.getChildElementByTagName((Element)element, (String)"interceptors");
            interceptors.addAll(WebSocketNamespaceUtils.parseBeanSubElements(interceptorsElement, context));
        }
        interceptors.add((Object)new ImmutableMessageChannelInterceptor());
        channelDef.getPropertyValues().add("interceptors", (Object)interceptors);
        MessageBrokerBeanDefinitionParser.registerBeanDefByName(name, channelDef, context, source);
        return new RuntimeBeanReference(name);
    }

    @Nullable
    private RootBeanDefinition getDefaultExecutorBeanDefinition(String channelName) {
        if (channelName.equals("brokerChannel")) {
            return null;
        }
        RootBeanDefinition executorDef = new RootBeanDefinition(ThreadPoolTaskExecutor.class);
        executorDef.getPropertyValues().add("corePoolSize", (Object)(Runtime.getRuntime().availableProcessors() * 2));
        executorDef.getPropertyValues().add("maxPoolSize", (Object)Integer.MAX_VALUE);
        executorDef.getPropertyValues().add("queueCapacity", (Object)Integer.MAX_VALUE);
        executorDef.getPropertyValues().add("allowCoreThreadTimeOut", (Object)true);
        return executorDef;
    }

    private RuntimeBeanReference registerStompHandler(Element element, RuntimeBeanReference inChannel, RuntimeBeanReference outChannel, ParserContext context, @Nullable Object source) {
        RootBeanDefinition stompHandlerDef = new RootBeanDefinition(StompSubProtocolHandler.class);
        MessageBrokerBeanDefinitionParser.registerBeanDef(stompHandlerDef, context, source);
        Element errorHandlerElem = DomUtils.getChildElementByTagName((Element)element, (String)"stomp-error-handler");
        if (errorHandlerElem != null) {
            RuntimeBeanReference errorHandlerRef = new RuntimeBeanReference(errorHandlerElem.getAttribute("ref"));
            stompHandlerDef.getPropertyValues().add("errorHandler", (Object)errorHandlerRef);
        }
        ConstructorArgumentValues cargs = new ConstructorArgumentValues();
        cargs.addIndexedArgumentValue(0, (Object)inChannel);
        cargs.addIndexedArgumentValue(1, (Object)outChannel);
        RootBeanDefinition handlerDef = new RootBeanDefinition(SubProtocolWebSocketHandler.class, cargs, null);
        handlerDef.getPropertyValues().addPropertyValue("protocolHandlers", (Object)stompHandlerDef);
        MessageBrokerBeanDefinitionParser.registerBeanDefByName(WEB_SOCKET_HANDLER_BEAN_NAME, handlerDef, context, source);
        RuntimeBeanReference result = new RuntimeBeanReference(WEB_SOCKET_HANDLER_BEAN_NAME);
        Element transportElem = DomUtils.getChildElementByTagName((Element)element, (String)"transport");
        if (transportElem != null) {
            Element factoriesElement;
            if (transportElem.hasAttribute("message-size")) {
                stompHandlerDef.getPropertyValues().add("messageSizeLimit", (Object)transportElem.getAttribute("message-size"));
            }
            if (transportElem.hasAttribute("send-timeout")) {
                handlerDef.getPropertyValues().add("sendTimeLimit", (Object)transportElem.getAttribute("send-timeout"));
            }
            if (transportElem.hasAttribute("send-buffer-size")) {
                handlerDef.getPropertyValues().add("sendBufferSizeLimit", (Object)transportElem.getAttribute("send-buffer-size"));
            }
            if (transportElem.hasAttribute("time-to-first-message")) {
                handlerDef.getPropertyValues().add("timeToFirstMessage", (Object)transportElem.getAttribute("time-to-first-message"));
            }
            if ((factoriesElement = DomUtils.getChildElementByTagName((Element)transportElem, (String)"decorator-factories")) != null) {
                ManagedList<Object> factories = this.extractBeanSubElements(factoriesElement, context);
                RootBeanDefinition factoryBean = new RootBeanDefinition(DecoratingFactoryBean.class);
                factoryBean.getConstructorArgumentValues().addIndexedArgumentValue(0, (Object)result);
                factoryBean.getConstructorArgumentValues().addIndexedArgumentValue(1, factories);
                result = new RuntimeBeanReference(MessageBrokerBeanDefinitionParser.registerBeanDef(factoryBean, context, source));
            }
        }
        return result;
    }

    private RuntimeBeanReference registerRequestHandler(Element element, RuntimeBeanReference subProtoHandler, ParserContext ctx, @Nullable Object source) {
        RootBeanDefinition beanDef;
        RuntimeBeanReference sockJsService = WebSocketNamespaceUtils.registerSockJsService(element, SCHEDULER_BEAN_NAME, ctx, source);
        if (sockJsService != null) {
            ConstructorArgumentValues cargs = new ConstructorArgumentValues();
            cargs.addIndexedArgumentValue(0, (Object)sockJsService);
            cargs.addIndexedArgumentValue(1, (Object)subProtoHandler);
            beanDef = new RootBeanDefinition(SockJsHttpRequestHandler.class, cargs, null);
            ctx.getRegistry().registerAlias(SCHEDULER_BEAN_NAME, SOCKJS_SCHEDULER_BEAN_NAME);
        } else {
            RuntimeBeanReference handler = WebSocketNamespaceUtils.registerHandshakeHandler(element, ctx, source);
            Element interceptElem = DomUtils.getChildElementByTagName((Element)element, (String)"handshake-interceptors");
            ManagedList<Object> interceptors = WebSocketNamespaceUtils.parseBeanSubElements(interceptElem, ctx);
            String allowedOrigins = element.getAttribute("allowed-origins");
            List<String> origins = Arrays.asList(StringUtils.tokenizeToStringArray((String)allowedOrigins, (String)","));
            String allowedOriginPatterns = element.getAttribute("allowed-origin-patterns");
            List<String> originPatterns = Arrays.asList(StringUtils.tokenizeToStringArray((String)allowedOriginPatterns, (String)","));
            OriginHandshakeInterceptor interceptor = new OriginHandshakeInterceptor(origins);
            if (!ObjectUtils.isEmpty(originPatterns)) {
                interceptor.setAllowedOriginPatterns(originPatterns);
            }
            interceptors.add((Object)interceptor);
            ConstructorArgumentValues cargs = new ConstructorArgumentValues();
            cargs.addIndexedArgumentValue(0, (Object)subProtoHandler);
            cargs.addIndexedArgumentValue(1, (Object)handler);
            beanDef = new RootBeanDefinition(WebSocketHttpRequestHandler.class, cargs, null);
            beanDef.getPropertyValues().add("handshakeInterceptors", interceptors);
        }
        return new RuntimeBeanReference(MessageBrokerBeanDefinitionParser.registerBeanDef(beanDef, ctx, source));
    }

    private RootBeanDefinition registerMessageBroker(Element brokerElement, RuntimeBeanReference inChannel, RuntimeBeanReference outChannel, RuntimeBeanReference brokerChannel, Object userDestHandler, RuntimeBeanReference brokerTemplate, RuntimeBeanReference userRegistry, ParserContext context, @Nullable Object source) {
        RootBeanDefinition brokerDef;
        String prefix;
        Element simpleBrokerElem = DomUtils.getChildElementByTagName((Element)brokerElement, (String)"simple-broker");
        Element brokerRelayElem = DomUtils.getChildElementByTagName((Element)brokerElement, (String)"stomp-broker-relay");
        ConstructorArgumentValues cargs = new ConstructorArgumentValues();
        cargs.addIndexedArgumentValue(0, (Object)inChannel);
        cargs.addIndexedArgumentValue(1, (Object)outChannel);
        cargs.addIndexedArgumentValue(2, (Object)brokerChannel);
        if (simpleBrokerElem != null) {
            prefix = simpleBrokerElem.getAttribute("prefix");
            cargs.addIndexedArgumentValue(3, Arrays.asList(StringUtils.tokenizeToStringArray((String)prefix, (String)",")));
            brokerDef = new RootBeanDefinition(SimpleBrokerMessageHandler.class, cargs, null);
            if (brokerElement.hasAttribute("path-matcher")) {
                String pathMatcherRef = brokerElement.getAttribute("path-matcher");
                brokerDef.getPropertyValues().add("pathMatcher", (Object)new RuntimeBeanReference(pathMatcherRef));
            }
            if (simpleBrokerElem.hasAttribute("scheduler")) {
                String scheduler = simpleBrokerElem.getAttribute("scheduler");
                brokerDef.getPropertyValues().add("taskScheduler", (Object)new RuntimeBeanReference(scheduler));
            }
            if (simpleBrokerElem.hasAttribute("heartbeat")) {
                String heartbeatValue = simpleBrokerElem.getAttribute("heartbeat");
                brokerDef.getPropertyValues().add("heartbeatValue", (Object)heartbeatValue);
            }
            if (simpleBrokerElem.hasAttribute("selector-header")) {
                String headerName = simpleBrokerElem.getAttribute("selector-header");
                brokerDef.getPropertyValues().add("selectorHeaderName", (Object)headerName);
            }
        } else if (brokerRelayElem != null) {
            String destination;
            prefix = brokerRelayElem.getAttribute("prefix");
            cargs.addIndexedArgumentValue(3, Arrays.asList(StringUtils.tokenizeToStringArray((String)prefix, (String)",")));
            MutablePropertyValues values = new MutablePropertyValues();
            if (brokerRelayElem.hasAttribute("relay-host")) {
                values.add("relayHost", (Object)brokerRelayElem.getAttribute("relay-host"));
            }
            if (brokerRelayElem.hasAttribute("relay-port")) {
                values.add("relayPort", (Object)brokerRelayElem.getAttribute("relay-port"));
            }
            if (brokerRelayElem.hasAttribute("client-login")) {
                values.add("clientLogin", (Object)brokerRelayElem.getAttribute("client-login"));
            }
            if (brokerRelayElem.hasAttribute("client-passcode")) {
                values.add("clientPasscode", (Object)brokerRelayElem.getAttribute("client-passcode"));
            }
            if (brokerRelayElem.hasAttribute("system-login")) {
                values.add("systemLogin", (Object)brokerRelayElem.getAttribute("system-login"));
            }
            if (brokerRelayElem.hasAttribute("system-passcode")) {
                values.add("systemPasscode", (Object)brokerRelayElem.getAttribute("system-passcode"));
            }
            if (brokerRelayElem.hasAttribute("heartbeat-send-interval")) {
                values.add("systemHeartbeatSendInterval", (Object)brokerRelayElem.getAttribute("heartbeat-send-interval"));
            }
            if (brokerRelayElem.hasAttribute("heartbeat-receive-interval")) {
                values.add("systemHeartbeatReceiveInterval", (Object)brokerRelayElem.getAttribute("heartbeat-receive-interval"));
            }
            if (brokerRelayElem.hasAttribute("virtual-host")) {
                values.add("virtualHost", (Object)brokerRelayElem.getAttribute("virtual-host"));
            }
            ManagedMap map = new ManagedMap();
            map.setSource(source);
            if (brokerRelayElem.hasAttribute("user-destination-broadcast")) {
                destination = brokerRelayElem.getAttribute("user-destination-broadcast");
                map.put((Object)destination, userDestHandler);
            }
            if (brokerRelayElem.hasAttribute("user-registry-broadcast")) {
                destination = brokerRelayElem.getAttribute("user-registry-broadcast");
                map.put((Object)destination, (Object)this.registerUserRegistryMessageHandler(userRegistry, brokerTemplate, destination, context, source));
            }
            if (!map.isEmpty()) {
                values.add("systemSubscriptions", (Object)map);
            }
            Class<StompBrokerRelayMessageHandler> handlerType = StompBrokerRelayMessageHandler.class;
            brokerDef = new RootBeanDefinition(handlerType, cargs, values);
        } else {
            throw new IllegalStateException("Neither <simple-broker> nor <stomp-broker-relay> elements found.");
        }
        if (brokerElement.hasAttribute("preserve-publish-order")) {
            String preservePublishOrder = brokerElement.getAttribute("preserve-publish-order");
            brokerDef.getPropertyValues().add("preservePublishOrder", (Object)preservePublishOrder);
        }
        MessageBrokerBeanDefinitionParser.registerBeanDef(brokerDef, context, source);
        return brokerDef;
    }

    private RuntimeBeanReference registerUserRegistryMessageHandler(RuntimeBeanReference userRegistry, RuntimeBeanReference brokerTemplate, String destination, ParserContext context, @Nullable Object source) {
        RuntimeBeanReference scheduler = WebSocketNamespaceUtils.registerScheduler(SCHEDULER_BEAN_NAME, context, source);
        RootBeanDefinition beanDef = new RootBeanDefinition(UserRegistryMessageHandler.class);
        beanDef.getConstructorArgumentValues().addIndexedArgumentValue(0, (Object)userRegistry);
        beanDef.getConstructorArgumentValues().addIndexedArgumentValue(1, (Object)brokerTemplate);
        beanDef.getConstructorArgumentValues().addIndexedArgumentValue(2, (Object)destination);
        beanDef.getConstructorArgumentValues().addIndexedArgumentValue(3, (Object)scheduler);
        String beanName = MessageBrokerBeanDefinitionParser.registerBeanDef(beanDef, context, source);
        return new RuntimeBeanReference(beanName);
    }

    private RuntimeBeanReference registerMessageConverter(Element element, ParserContext context, @Nullable Object source) {
        Element convertersElement = DomUtils.getChildElementByTagName((Element)element, (String)"message-converters");
        ManagedList converters = new ManagedList();
        if (convertersElement != null) {
            converters.setSource(source);
            for (Element beanElement : DomUtils.getChildElementsByTagName((Element)convertersElement, (String[])new String[]{"bean", "ref"})) {
                Object object = context.getDelegate().parsePropertySubElement(beanElement, null);
                converters.add(object);
            }
        }
        if (convertersElement == null || Boolean.parseBoolean(convertersElement.getAttribute("register-defaults"))) {
            converters.setSource(source);
            converters.add((Object)new RootBeanDefinition(StringMessageConverter.class));
            converters.add((Object)new RootBeanDefinition(ByteArrayMessageConverter.class));
            if (jackson2Present) {
                RootBeanDefinition jacksonConverterDef = new RootBeanDefinition(MappingJackson2MessageConverter.class);
                RootBeanDefinition resolverDef = new RootBeanDefinition(DefaultContentTypeResolver.class);
                resolverDef.getPropertyValues().add("defaultMimeType", (Object)MimeTypeUtils.APPLICATION_JSON);
                jacksonConverterDef.getPropertyValues().add("contentTypeResolver", (Object)resolverDef);
                GenericBeanDefinition jacksonFactoryDef = new GenericBeanDefinition();
                jacksonFactoryDef.setBeanClass(Jackson2ObjectMapperFactoryBean.class);
                jacksonFactoryDef.setRole(2);
                jacksonFactoryDef.setSource(source);
                jacksonConverterDef.getPropertyValues().add("objectMapper", (Object)jacksonFactoryDef);
                converters.add((Object)jacksonConverterDef);
            } else if (gsonPresent) {
                converters.add((Object)new RootBeanDefinition(GsonMessageConverter.class));
            } else if (jsonbPresent) {
                converters.add((Object)new RootBeanDefinition(JsonbMessageConverter.class));
            }
        }
        ConstructorArgumentValues cargs = new ConstructorArgumentValues();
        cargs.addIndexedArgumentValue(0, (Object)converters);
        RootBeanDefinition messageConverterDef = new RootBeanDefinition(CompositeMessageConverter.class, cargs, null);
        String name = MESSAGE_CONVERTER_BEAN_NAME;
        MessageBrokerBeanDefinitionParser.registerBeanDefByName(name, messageConverterDef, context, source);
        return new RuntimeBeanReference(name);
    }

    private RuntimeBeanReference registerMessagingTemplate(Element element, RuntimeBeanReference brokerChannel, RuntimeBeanReference messageConverter, ParserContext context, @Nullable Object source) {
        ConstructorArgumentValues cargs = new ConstructorArgumentValues();
        cargs.addIndexedArgumentValue(0, (Object)brokerChannel);
        RootBeanDefinition beanDef = new RootBeanDefinition(SimpMessagingTemplate.class, cargs, null);
        if (element.hasAttribute("user-destination-prefix")) {
            beanDef.getPropertyValues().add("userDestinationPrefix", (Object)element.getAttribute("user-destination-prefix"));
        }
        beanDef.getPropertyValues().add("messageConverter", (Object)messageConverter);
        String name = MESSAGING_TEMPLATE_BEAN_NAME;
        MessageBrokerBeanDefinitionParser.registerBeanDefByName(name, beanDef, context, source);
        return new RuntimeBeanReference(name);
    }

    private void registerAnnotationMethodMessageHandler(Element messageBrokerElement, RuntimeBeanReference inChannel, RuntimeBeanReference outChannel, RuntimeBeanReference converter, RuntimeBeanReference messagingTemplate, ParserContext context, @Nullable Object source) {
        Element handlersElement;
        Element resolversElement;
        RuntimeBeanReference validatorRef;
        ConstructorArgumentValues cargs = new ConstructorArgumentValues();
        cargs.addIndexedArgumentValue(0, (Object)inChannel);
        cargs.addIndexedArgumentValue(1, (Object)outChannel);
        cargs.addIndexedArgumentValue(2, (Object)messagingTemplate);
        MutablePropertyValues values = new MutablePropertyValues();
        String prefixAttribute = messageBrokerElement.getAttribute("application-destination-prefix");
        values.add("destinationPrefixes", Arrays.asList(StringUtils.tokenizeToStringArray((String)prefixAttribute, (String)",")));
        values.add("messageConverter", (Object)converter);
        RootBeanDefinition beanDef = new RootBeanDefinition(WebSocketAnnotationMethodMessageHandler.class, cargs, values);
        if (messageBrokerElement.hasAttribute("path-matcher")) {
            String pathMatcherRef = messageBrokerElement.getAttribute("path-matcher");
            beanDef.getPropertyValues().add("pathMatcher", (Object)new RuntimeBeanReference(pathMatcherRef));
        }
        if ((validatorRef = this.getValidator(messageBrokerElement, source, context)) != null) {
            beanDef.getPropertyValues().add("validator", (Object)validatorRef);
        }
        if ((resolversElement = DomUtils.getChildElementByTagName((Element)messageBrokerElement, (String)"argument-resolvers")) != null) {
            values.add("customArgumentResolvers", this.extractBeanSubElements(resolversElement, context));
        }
        if ((handlersElement = DomUtils.getChildElementByTagName((Element)messageBrokerElement, (String)"return-value-handlers")) != null) {
            values.add("customReturnValueHandlers", this.extractBeanSubElements(handlersElement, context));
        }
        MessageBrokerBeanDefinitionParser.registerBeanDef(beanDef, context, source);
    }

    @Nullable
    private RuntimeBeanReference getValidator(Element messageBrokerElement, @Nullable Object source, ParserContext context) {
        if (messageBrokerElement.hasAttribute("validator")) {
            return new RuntimeBeanReference(messageBrokerElement.getAttribute("validator"));
        }
        if (javaxValidationPresent) {
            RootBeanDefinition validatorDef = new RootBeanDefinition("org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean");
            validatorDef.setSource(source);
            validatorDef.setRole(2);
            String validatorName = context.getReaderContext().registerWithGeneratedName((BeanDefinition)validatorDef);
            context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)validatorDef, validatorName));
            return new RuntimeBeanReference(validatorName);
        }
        return null;
    }

    private ManagedList<Object> extractBeanSubElements(Element parentElement, ParserContext context) {
        ManagedList list = new ManagedList();
        list.setSource(context.extractSource((Object)parentElement));
        for (Element beanElement : DomUtils.getChildElementsByTagName((Element)parentElement, (String[])new String[]{"bean", "ref"})) {
            Object object = context.getDelegate().parsePropertySubElement(beanElement, null);
            list.add(object);
        }
        return list;
    }

    private RuntimeBeanReference registerUserDestResolver(Element brokerElem, RuntimeBeanReference userRegistry, ParserContext context, @Nullable Object source) {
        RootBeanDefinition beanDef = new RootBeanDefinition(DefaultUserDestinationResolver.class);
        beanDef.getConstructorArgumentValues().addIndexedArgumentValue(0, (Object)userRegistry);
        if (brokerElem.hasAttribute("user-destination-prefix")) {
            beanDef.getPropertyValues().add("userDestinationPrefix", (Object)brokerElem.getAttribute("user-destination-prefix"));
        }
        if (brokerElem.hasAttribute("path-matcher")) {
            String pathMatcherRef = brokerElem.getAttribute("path-matcher");
            beanDef.getPropertyValues().add("pathMatcher", (Object)new RuntimeBeanReference(pathMatcherRef));
        }
        return new RuntimeBeanReference(MessageBrokerBeanDefinitionParser.registerBeanDef(beanDef, context, source));
    }

    private RuntimeBeanReference registerUserDestHandler(Element brokerElem, RuntimeBeanReference userRegistry, RuntimeBeanReference inChannel, RuntimeBeanReference brokerChannel, ParserContext context, @Nullable Object source) {
        RuntimeBeanReference userDestResolver = this.registerUserDestResolver(brokerElem, userRegistry, context, source);
        RootBeanDefinition beanDef = new RootBeanDefinition(UserDestinationMessageHandler.class);
        beanDef.getConstructorArgumentValues().addIndexedArgumentValue(0, (Object)inChannel);
        beanDef.getConstructorArgumentValues().addIndexedArgumentValue(1, (Object)brokerChannel);
        beanDef.getConstructorArgumentValues().addIndexedArgumentValue(2, (Object)userDestResolver);
        Element relayElement = DomUtils.getChildElementByTagName((Element)brokerElem, (String)"stomp-broker-relay");
        if (relayElement != null && relayElement.hasAttribute("user-destination-broadcast")) {
            String destination = relayElement.getAttribute("user-destination-broadcast");
            beanDef.getPropertyValues().add("broadcastDestination", (Object)destination);
        }
        String beanName = MessageBrokerBeanDefinitionParser.registerBeanDef(beanDef, context, source);
        return new RuntimeBeanReference(beanName);
    }

    private void registerWebSocketMessageBrokerStats(RootBeanDefinition broker, RuntimeBeanReference inChannel, RuntimeBeanReference outChannel, ParserContext context, @Nullable Object source) {
        RootBeanDefinition beanDef = new RootBeanDefinition(WebSocketMessageBrokerStats.class);
        RuntimeBeanReference webSocketHandler = new RuntimeBeanReference(WEB_SOCKET_HANDLER_BEAN_NAME);
        beanDef.getPropertyValues().add(WEB_SOCKET_HANDLER_BEAN_NAME, (Object)webSocketHandler);
        if (StompBrokerRelayMessageHandler.class == broker.getBeanClass()) {
            beanDef.getPropertyValues().add("stompBrokerRelay", (Object)broker);
        }
        String name = inChannel.getBeanName() + "Executor";
        if (context.getRegistry().containsBeanDefinition(name)) {
            beanDef.getPropertyValues().add("inboundChannelExecutor", (Object)context.getRegistry().getBeanDefinition(name));
        }
        name = outChannel.getBeanName() + "Executor";
        if (context.getRegistry().containsBeanDefinition(name)) {
            beanDef.getPropertyValues().add("outboundChannelExecutor", (Object)context.getRegistry().getBeanDefinition(name));
        }
        RuntimeBeanReference scheduler = WebSocketNamespaceUtils.registerScheduler(SCHEDULER_BEAN_NAME, context, source);
        beanDef.getPropertyValues().add("sockJsTaskScheduler", (Object)scheduler);
        MessageBrokerBeanDefinitionParser.registerBeanDefByName("webSocketMessageBrokerStats", beanDef, context, source);
    }

    private static String registerBeanDef(RootBeanDefinition beanDef, ParserContext context, @Nullable Object source) {
        String name = context.getReaderContext().generateBeanName((BeanDefinition)beanDef);
        MessageBrokerBeanDefinitionParser.registerBeanDefByName(name, beanDef, context, source);
        return name;
    }

    private static void registerBeanDefByName(String name, RootBeanDefinition beanDef, ParserContext context, @Nullable Object source) {
        beanDef.setSource(source);
        beanDef.setRole(2);
        context.getRegistry().registerBeanDefinition(name, (BeanDefinition)beanDef);
        context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)beanDef, name));
    }

    static {
        ClassLoader classLoader = MessageBrokerBeanDefinitionParser.class.getClassLoader();
        jackson2Present = ClassUtils.isPresent((String)"com.fasterxml.jackson.databind.ObjectMapper", (ClassLoader)classLoader) && ClassUtils.isPresent((String)"com.fasterxml.jackson.core.JsonGenerator", (ClassLoader)classLoader);
        gsonPresent = ClassUtils.isPresent((String)"com.google.gson.Gson", (ClassLoader)classLoader);
        jsonbPresent = ClassUtils.isPresent((String)"javax.json.bind.Jsonb", (ClassLoader)classLoader);
        javaxValidationPresent = ClassUtils.isPresent((String)"javax.validation.Validator", (ClassLoader)classLoader);
    }

    private static final class DecoratingFactoryBean
    implements FactoryBean<WebSocketHandler> {
        private final WebSocketHandler handler;
        private final List<WebSocketHandlerDecoratorFactory> factories;

        public DecoratingFactoryBean(WebSocketHandler handler, List<WebSocketHandlerDecoratorFactory> factories) {
            this.handler = handler;
            this.factories = factories;
        }

        public WebSocketHandler getObject() {
            WebSocketHandler result = this.handler;
            for (WebSocketHandlerDecoratorFactory factory : this.factories) {
                result = factory.decorate(result);
            }
            return result;
        }

        public Class<?> getObjectType() {
            return WebSocketHandler.class;
        }

        public boolean isSingleton() {
            return true;
        }
    }
}

