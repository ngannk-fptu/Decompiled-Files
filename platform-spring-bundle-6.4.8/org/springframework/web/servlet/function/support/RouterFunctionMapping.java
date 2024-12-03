/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.servlet.function.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.SpringProperties;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

public class RouterFunctionMapping
extends AbstractHandlerMapping
implements InitializingBean {
    private static final boolean shouldIgnoreXml = SpringProperties.getFlag("spring.xml.ignore");
    @Nullable
    private RouterFunction<?> routerFunction;
    private List<HttpMessageConverter<?>> messageConverters = Collections.emptyList();
    private boolean detectHandlerFunctionsInAncestorContexts = false;

    public RouterFunctionMapping() {
    }

    public RouterFunctionMapping(RouterFunction<?> routerFunction) {
        this.routerFunction = routerFunction;
    }

    public void setRouterFunction(@Nullable RouterFunction<?> routerFunction) {
        this.routerFunction = routerFunction;
    }

    @Nullable
    public RouterFunction<?> getRouterFunction() {
        return this.routerFunction;
    }

    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = messageConverters;
    }

    public void setDetectHandlerFunctionsInAncestorContexts(boolean detectHandlerFunctionsInAncestorContexts) {
        this.detectHandlerFunctionsInAncestorContexts = detectHandlerFunctionsInAncestorContexts;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.routerFunction == null) {
            this.initRouterFunctions();
        }
        if (CollectionUtils.isEmpty(this.messageConverters)) {
            this.initMessageConverters();
        }
        if (this.routerFunction != null) {
            PathPatternParser patternParser = this.getPatternParser();
            if (patternParser == null) {
                patternParser = new PathPatternParser();
                this.setPatternParser(patternParser);
            }
            RouterFunctions.changeParser(this.routerFunction, patternParser);
        }
    }

    private void initRouterFunctions() {
        List<RouterFunction<?>> routerFunctions = this.obtainApplicationContext().getBeanProvider(RouterFunction.class).orderedStream().map(router -> router).collect(Collectors.toList());
        ApplicationContext parentContext = this.obtainApplicationContext().getParent();
        if (parentContext != null && !this.detectHandlerFunctionsInAncestorContexts) {
            parentContext.getBeanProvider(RouterFunction.class).stream().forEach(routerFunctions::remove);
        }
        this.routerFunction = routerFunctions.stream().reduce(RouterFunction::andOther).orElse(null);
        this.logRouterFunctions(routerFunctions);
    }

    private void logRouterFunctions(List<RouterFunction<?>> routerFunctions) {
        if (this.mappingsLogger.isDebugEnabled()) {
            routerFunctions.forEach(function -> this.mappingsLogger.debug((Object)("Mapped " + function)));
        } else if (this.logger.isDebugEnabled()) {
            int total = routerFunctions.size();
            String message = total + " RouterFunction(s) in " + this.formatMappingName();
            if (this.logger.isTraceEnabled()) {
                if (total > 0) {
                    routerFunctions.forEach(function -> this.logger.trace((Object)("Mapped " + function)));
                } else {
                    this.logger.trace((Object)message);
                }
            } else if (total > 0) {
                this.logger.debug((Object)message);
            }
        }
    }

    private void initMessageConverters() {
        ArrayList messageConverters = new ArrayList(4);
        messageConverters.add(new ByteArrayHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter());
        if (!shouldIgnoreXml) {
            try {
                messageConverters.add(new SourceHttpMessageConverter());
            }
            catch (Error error) {
                // empty catch block
            }
        }
        messageConverters.add(new AllEncompassingFormHttpMessageConverter());
        this.messageConverters = messageConverters;
    }

    @Override
    @Nullable
    protected Object getHandlerInternal(HttpServletRequest servletRequest) throws Exception {
        if (this.routerFunction != null) {
            ServerRequest request = ServerRequest.create(servletRequest, this.messageConverters);
            HandlerFunction handlerFunction = this.routerFunction.route(request).orElse(null);
            this.setAttributes(servletRequest, request, handlerFunction);
            return handlerFunction;
        }
        return null;
    }

    private void setAttributes(HttpServletRequest servletRequest, ServerRequest request, @Nullable HandlerFunction<?> handlerFunction) {
        PathPattern matchingPattern = (PathPattern)servletRequest.getAttribute(RouterFunctions.MATCHING_PATTERN_ATTRIBUTE);
        if (matchingPattern != null) {
            servletRequest.removeAttribute(RouterFunctions.MATCHING_PATTERN_ATTRIBUTE);
            servletRequest.setAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE, (Object)matchingPattern.getPatternString());
        }
        servletRequest.setAttribute(BEST_MATCHING_HANDLER_ATTRIBUTE, handlerFunction);
        servletRequest.setAttribute(RouterFunctions.REQUEST_ATTRIBUTE, (Object)request);
    }
}

