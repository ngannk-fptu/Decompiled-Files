/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.context.ApplicationContext
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  reactor.blockhound.BlockHound$Builder
 *  reactor.blockhound.integration.BlockHoundIntegration
 */
package org.springframework.web.server.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.HttpHandlerDecoratorFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;
import org.springframework.web.server.adapter.HttpWebHandlerAdapter;
import org.springframework.web.server.handler.ExceptionHandlingWebHandler;
import org.springframework.web.server.handler.FilteringWebHandler;
import org.springframework.web.server.handler.WebHandlerDecorator;
import org.springframework.web.server.i18n.LocaleContextResolver;
import org.springframework.web.server.session.WebSessionManager;
import reactor.blockhound.BlockHound;
import reactor.blockhound.integration.BlockHoundIntegration;

public final class WebHttpHandlerBuilder {
    public static final String WEB_HANDLER_BEAN_NAME = "webHandler";
    public static final String WEB_SESSION_MANAGER_BEAN_NAME = "webSessionManager";
    public static final String SERVER_CODEC_CONFIGURER_BEAN_NAME = "serverCodecConfigurer";
    public static final String LOCALE_CONTEXT_RESOLVER_BEAN_NAME = "localeContextResolver";
    public static final String FORWARDED_HEADER_TRANSFORMER_BEAN_NAME = "forwardedHeaderTransformer";
    private final WebHandler webHandler;
    @Nullable
    private final ApplicationContext applicationContext;
    private final List<WebFilter> filters = new ArrayList<WebFilter>();
    private final List<WebExceptionHandler> exceptionHandlers = new ArrayList<WebExceptionHandler>();
    @Nullable
    private Function<HttpHandler, HttpHandler> httpHandlerDecorator;
    @Nullable
    private WebSessionManager sessionManager;
    @Nullable
    private ServerCodecConfigurer codecConfigurer;
    @Nullable
    private LocaleContextResolver localeContextResolver;
    @Nullable
    private ForwardedHeaderTransformer forwardedHeaderTransformer;

    private WebHttpHandlerBuilder(WebHandler webHandler, @Nullable ApplicationContext applicationContext) {
        Assert.notNull((Object)webHandler, (String)"WebHandler must not be null");
        this.webHandler = webHandler;
        this.applicationContext = applicationContext;
    }

    private WebHttpHandlerBuilder(WebHttpHandlerBuilder other) {
        this.webHandler = other.webHandler;
        this.applicationContext = other.applicationContext;
        this.filters.addAll(other.filters);
        this.exceptionHandlers.addAll(other.exceptionHandlers);
        this.sessionManager = other.sessionManager;
        this.codecConfigurer = other.codecConfigurer;
        this.localeContextResolver = other.localeContextResolver;
        this.forwardedHeaderTransformer = other.forwardedHeaderTransformer;
        this.httpHandlerDecorator = other.httpHandlerDecorator;
    }

    public static WebHttpHandlerBuilder webHandler(WebHandler webHandler) {
        return new WebHttpHandlerBuilder(webHandler, null);
    }

    public static WebHttpHandlerBuilder applicationContext(ApplicationContext context) {
        WebHttpHandlerBuilder builder = new WebHttpHandlerBuilder((WebHandler)context.getBean(WEB_HANDLER_BEAN_NAME, WebHandler.class), context);
        List webFilters = context.getBeanProvider(WebFilter.class).orderedStream().collect(Collectors.toList());
        builder.filters(filters -> filters.addAll(webFilters));
        List exceptionHandlers = context.getBeanProvider(WebExceptionHandler.class).orderedStream().collect(Collectors.toList());
        builder.exceptionHandlers(handlers -> handlers.addAll(exceptionHandlers));
        context.getBeanProvider(HttpHandlerDecoratorFactory.class).orderedStream().forEach(builder::httpHandlerDecorator);
        try {
            builder.sessionManager((WebSessionManager)context.getBean(WEB_SESSION_MANAGER_BEAN_NAME, WebSessionManager.class));
        }
        catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
            // empty catch block
        }
        try {
            builder.codecConfigurer((ServerCodecConfigurer)context.getBean(SERVER_CODEC_CONFIGURER_BEAN_NAME, ServerCodecConfigurer.class));
        }
        catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
            // empty catch block
        }
        try {
            builder.localeContextResolver((LocaleContextResolver)context.getBean(LOCALE_CONTEXT_RESOLVER_BEAN_NAME, LocaleContextResolver.class));
        }
        catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
            // empty catch block
        }
        try {
            builder.forwardedHeaderTransformer((ForwardedHeaderTransformer)context.getBean(FORWARDED_HEADER_TRANSFORMER_BEAN_NAME, ForwardedHeaderTransformer.class));
        }
        catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
            // empty catch block
        }
        return builder;
    }

    public WebHttpHandlerBuilder filter(WebFilter ... filters) {
        if (!ObjectUtils.isEmpty((Object[])filters)) {
            this.filters.addAll(Arrays.asList(filters));
            this.updateFilters();
        }
        return this;
    }

    public WebHttpHandlerBuilder filters(Consumer<List<WebFilter>> consumer) {
        consumer.accept(this.filters);
        this.updateFilters();
        return this;
    }

    private void updateFilters() {
        if (this.filters.isEmpty()) {
            return;
        }
        List filtersToUse = this.filters.stream().peek(filter -> {
            if (filter instanceof ForwardedHeaderTransformer && this.forwardedHeaderTransformer == null) {
                this.forwardedHeaderTransformer = (ForwardedHeaderTransformer)((Object)filter);
            }
        }).filter((? super T filter) -> !(filter instanceof ForwardedHeaderTransformer)).collect(Collectors.toList());
        this.filters.clear();
        this.filters.addAll(filtersToUse);
    }

    public WebHttpHandlerBuilder exceptionHandler(WebExceptionHandler ... handlers) {
        if (!ObjectUtils.isEmpty((Object[])handlers)) {
            this.exceptionHandlers.addAll(Arrays.asList(handlers));
        }
        return this;
    }

    public WebHttpHandlerBuilder exceptionHandlers(Consumer<List<WebExceptionHandler>> consumer) {
        consumer.accept(this.exceptionHandlers);
        return this;
    }

    public WebHttpHandlerBuilder sessionManager(WebSessionManager manager) {
        this.sessionManager = manager;
        return this;
    }

    public boolean hasSessionManager() {
        return this.sessionManager != null;
    }

    public WebHttpHandlerBuilder codecConfigurer(ServerCodecConfigurer codecConfigurer) {
        this.codecConfigurer = codecConfigurer;
        return this;
    }

    public boolean hasCodecConfigurer() {
        return this.codecConfigurer != null;
    }

    public WebHttpHandlerBuilder localeContextResolver(LocaleContextResolver localeContextResolver) {
        this.localeContextResolver = localeContextResolver;
        return this;
    }

    public boolean hasLocaleContextResolver() {
        return this.localeContextResolver != null;
    }

    public WebHttpHandlerBuilder forwardedHeaderTransformer(ForwardedHeaderTransformer transformer) {
        this.forwardedHeaderTransformer = transformer;
        return this;
    }

    public boolean hasForwardedHeaderTransformer() {
        return this.forwardedHeaderTransformer != null;
    }

    public WebHttpHandlerBuilder httpHandlerDecorator(Function<HttpHandler, HttpHandler> handlerDecorator) {
        this.httpHandlerDecorator = this.httpHandlerDecorator != null ? handlerDecorator.andThen(this.httpHandlerDecorator) : handlerDecorator;
        return this;
    }

    public boolean hasHttpHandlerDecorator() {
        return this.httpHandlerDecorator != null;
    }

    public HttpHandler build() {
        WebHandlerDecorator decorated = new FilteringWebHandler(this.webHandler, this.filters);
        decorated = new ExceptionHandlingWebHandler(decorated, this.exceptionHandlers);
        HttpWebHandlerAdapter adapted = new HttpWebHandlerAdapter(decorated);
        if (this.sessionManager != null) {
            adapted.setSessionManager(this.sessionManager);
        }
        if (this.codecConfigurer != null) {
            adapted.setCodecConfigurer(this.codecConfigurer);
        }
        if (this.localeContextResolver != null) {
            adapted.setLocaleContextResolver(this.localeContextResolver);
        }
        if (this.forwardedHeaderTransformer != null) {
            adapted.setForwardedHeaderTransformer(this.forwardedHeaderTransformer);
        }
        if (this.applicationContext != null) {
            adapted.setApplicationContext(this.applicationContext);
        }
        adapted.afterPropertiesSet();
        return this.httpHandlerDecorator != null ? this.httpHandlerDecorator.apply(adapted) : adapted;
    }

    public WebHttpHandlerBuilder clone() {
        return new WebHttpHandlerBuilder(this);
    }

    public static class SpringWebBlockHoundIntegration
    implements BlockHoundIntegration {
        public void applyTo(BlockHound.Builder builder) {
            builder.allowBlockingCallsInside("org.springframework.http.MediaTypeFactory", "<clinit>");
            builder.allowBlockingCallsInside("org.springframework.web.util.HtmlUtils", "<clinit>");
        }
    }
}

