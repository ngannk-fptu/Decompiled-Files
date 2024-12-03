/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.server.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.adapter.HttpWebHandlerAdapter;
import org.springframework.web.server.handler.ExceptionHandlingWebHandler;
import org.springframework.web.server.handler.FilteringWebHandler;
import org.springframework.web.server.handler.WebHandlerDecorator;
import org.springframework.web.server.i18n.LocaleContextResolver;
import org.springframework.web.server.session.WebSessionManager;

public class WebHttpHandlerBuilder {
    public static final String WEB_HANDLER_BEAN_NAME = "webHandler";
    public static final String WEB_SESSION_MANAGER_BEAN_NAME = "webSessionManager";
    public static final String SERVER_CODEC_CONFIGURER_BEAN_NAME = "serverCodecConfigurer";
    public static final String LOCALE_CONTEXT_RESOLVER_BEAN_NAME = "localeContextResolver";
    private final WebHandler webHandler;
    @Nullable
    private final ApplicationContext applicationContext;
    private final List<WebFilter> filters = new ArrayList<WebFilter>();
    private final List<WebExceptionHandler> exceptionHandlers = new ArrayList<WebExceptionHandler>();
    @Nullable
    private WebSessionManager sessionManager;
    @Nullable
    private ServerCodecConfigurer codecConfigurer;
    @Nullable
    private LocaleContextResolver localeContextResolver;

    private WebHttpHandlerBuilder(WebHandler webHandler, @Nullable ApplicationContext applicationContext) {
        Assert.notNull((Object)webHandler, "WebHandler must not be null");
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
    }

    public static WebHttpHandlerBuilder webHandler(WebHandler webHandler) {
        return new WebHttpHandlerBuilder(webHandler, null);
    }

    public static WebHttpHandlerBuilder applicationContext(ApplicationContext context) {
        WebHttpHandlerBuilder builder = new WebHttpHandlerBuilder(context.getBean(WEB_HANDLER_BEAN_NAME, WebHandler.class), context);
        SortedBeanContainer container = new SortedBeanContainer();
        context.getAutowireCapableBeanFactory().autowireBean(container);
        builder.filters(filters -> filters.addAll(container.getFilters()));
        builder.exceptionHandlers(handlers -> handlers.addAll(container.getExceptionHandlers()));
        try {
            builder.sessionManager(context.getBean(WEB_SESSION_MANAGER_BEAN_NAME, WebSessionManager.class));
        }
        catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
            // empty catch block
        }
        try {
            builder.codecConfigurer(context.getBean(SERVER_CODEC_CONFIGURER_BEAN_NAME, ServerCodecConfigurer.class));
        }
        catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
            // empty catch block
        }
        try {
            builder.localeContextResolver(context.getBean(LOCALE_CONTEXT_RESOLVER_BEAN_NAME, LocaleContextResolver.class));
        }
        catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
            // empty catch block
        }
        return builder;
    }

    public WebHttpHandlerBuilder filter(WebFilter ... filters) {
        if (!ObjectUtils.isEmpty(filters)) {
            this.filters.addAll(Arrays.asList(filters));
        }
        return this;
    }

    public WebHttpHandlerBuilder filters(Consumer<List<WebFilter>> consumer) {
        consumer.accept(this.filters);
        return this;
    }

    public WebHttpHandlerBuilder exceptionHandler(WebExceptionHandler ... handlers) {
        if (!ObjectUtils.isEmpty(handlers)) {
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
        if (this.applicationContext != null) {
            adapted.setApplicationContext(this.applicationContext);
        }
        return adapted;
    }

    public WebHttpHandlerBuilder clone() {
        return new WebHttpHandlerBuilder(this);
    }

    private static class SortedBeanContainer {
        private List<WebFilter> filters = Collections.emptyList();
        private List<WebExceptionHandler> exceptionHandlers = Collections.emptyList();

        private SortedBeanContainer() {
        }

        @Autowired(required=false)
        public void setFilters(List<WebFilter> filters) {
            this.filters = filters;
        }

        public List<WebFilter> getFilters() {
            return this.filters;
        }

        @Autowired(required=false)
        public void setExceptionHandlers(List<WebExceptionHandler> exceptionHandlers) {
            this.exceptionHandlers = exceptionHandlers;
        }

        public List<WebExceptionHandler> getExceptionHandlers() {
            return this.exceptionHandlers;
        }
    }
}

