/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.atlassian.plugin.osgi.factory.OsgiPlugin
 *  com.atlassian.util.profiling.Metrics
 *  com.atlassian.util.profiling.Ticker
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.rest.module;

import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugin.osgi.factory.OsgiPlugin;
import com.atlassian.plugins.rest.module.ChainingClassLoader;
import com.atlassian.plugins.rest.module.OsgiComponentProviderFactory;
import com.atlassian.plugins.rest.module.ResourceConfigManager;
import com.atlassian.plugins.rest.module.RestApiContext;
import com.atlassian.plugins.rest.module.RestModuleDescriptor;
import com.atlassian.plugins.rest.module.Slf4jBridge;
import com.atlassian.util.profiling.Metrics;
import com.atlassian.util.profiling.Ticker;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;
import com.sun.jersey.spi.inject.Errors;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RestDelegatingServletFilter
implements Filter {
    private static final Logger log = LoggerFactory.getLogger(RestDelegatingServletFilter.class);
    private static final Slf4jBridge.Helper SLF4J_BRIDGE = Slf4jBridge.createHelper();
    private static final String METRIC_NAME = "http.rest.request";
    private final ServletContainer servletContainer;
    private final ResourceConfigManager resourceConfigManager;
    private ClassLoader chainingClassLoader;

    RestDelegatingServletFilter(OsgiPlugin plugin, RestApiContext restContextPath) {
        this.resourceConfigManager = new ResourceConfigManager((ContainerManagedPlugin)plugin, plugin.getBundle());
        this.servletContainer = new JerseyOsgiServletContainer(plugin, restContextPath, this.resourceConfigManager);
    }

    public void init(FilterConfig config) throws ServletException {
        this.initChainingClassLoader();
        this.initServletContainer(config);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.chainingClassLoader);
        try {
            this.servletContainer.doFilter(request, response, chain);
        }
        finally {
            Thread.currentThread().setContextClassLoader(currentThreadClassLoader);
        }
    }

    public void destroy() {
        this.destroyServletContainer();
        this.resourceConfigManager.destroy();
    }

    private void initChainingClassLoader() {
        this.chainingClassLoader = new ChainingClassLoader(RestModuleDescriptor.class.getClassLoader(), Thread.currentThread().getContextClassLoader());
    }

    private void initServletContainer(FilterConfig config) throws ServletException {
        ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.chainingClassLoader);
        try {
            SLF4J_BRIDGE.install();
            this.servletContainer.init(config);
        }
        finally {
            Thread.currentThread().setContextClassLoader(currentThreadClassLoader);
        }
    }

    private void destroyServletContainer() {
        ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.chainingClassLoader);
        try {
            this.servletContainer.destroy();
            SLF4J_BRIDGE.uninstall();
        }
        finally {
            Thread.currentThread().setContextClassLoader(currentThreadClassLoader);
        }
    }

    private static class JerseyOsgiServletContainer
    extends ServletContainer {
        private final OsgiPlugin plugin;
        private final RestApiContext restApiContext;
        private final ResourceConfigManager resourceConfigManager;
        private static final String PARAM_EXTENSION_FILTER_EXCLUDES = "extension.filter.excludes";

        public JerseyOsgiServletContainer(OsgiPlugin plugin, RestApiContext restApiContext, ResourceConfigManager resourceConfigManager) {
            this.resourceConfigManager = Objects.requireNonNull(resourceConfigManager, "resourceConfigManager can't be null");
            this.plugin = Objects.requireNonNull(plugin, "plugin can't be null");
            this.restApiContext = Objects.requireNonNull(restApiContext, "restApiContext can't be null");
        }

        @Override
        protected ResourceConfig getDefaultResourceConfig(Map<String, Object> props, WebConfig webConfig) throws ServletException {
            String deprecatedName = "com.atlassian.plugins.rest.module.filter.ExtensionJerseyFilter#excludes";
            String excludeParam = webConfig.getInitParameter("com.atlassian.plugins.rest.module.filter.ExtensionJerseyFilter#excludes") != null ? webConfig.getInitParameter("com.atlassian.plugins.rest.module.filter.ExtensionJerseyFilter#excludes") : webConfig.getInitParameter(PARAM_EXTENSION_FILTER_EXCLUDES);
            String[] excludes = StringUtils.split((String)excludeParam, (String)" ,;");
            DefaultResourceConfig resourceConfig = this.resourceConfigManager.createResourceConfig(props, excludes, this.restApiContext.getPackages(), this.restApiContext.getIndexBundledJars());
            this.restApiContext.setConfig(resourceConfig);
            return resourceConfig;
        }

        @Override
        public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
            String baseUriPath;
            if (request.getRequestURI().contains(this.restApiContext.getPathToLatest())) {
                baseUriPath = request.getContextPath() + this.restApiContext.getPathToLatest();
                log.debug("Setting base uri for REST to 'latest'");
                log.debug("Incoming URI : {}", (Object)request.getRequestURI());
            } else {
                baseUriPath = request.getContextPath() + this.restApiContext.getPathToVersion();
            }
            UriBuilder absoluteUriBuilder = UriBuilder.fromUri(request.getRequestURL().toString());
            URI baseUri = absoluteUriBuilder.replacePath(baseUriPath).path("/").build(new Object[0]);
            URI requestUri = absoluteUriBuilder.replacePath(request.getRequestURI()).replaceQuery(request.getQueryString()).build(new Object[0]);
            try (Ticker ignored = Metrics.metric((String)RestDelegatingServletFilter.METRIC_NAME).fromPluginKey(this.plugin.getKey()).tag("path", this.restApiContext.getPathToVersion()).tag("action", request.getMethod()).withAnalytics().startTimer();){
                this.service(baseUri, requestUri, request, response);
            }
        }

        @Override
        protected void initiate(ResourceConfig resourceConfig, WebApplication webApplication) {
            try {
                webApplication.initiate(resourceConfig, new OsgiComponentProviderFactory(resourceConfig, (ContainerManagedPlugin)this.plugin));
            }
            catch (Errors.ErrorMessagesException errorMessages) {
                try {
                    for (Errors.ErrorMessage message : errorMessages.messages) {
                        Field field = message.getClass().getField("message");
                        log.error(String.valueOf(field.get(message)));
                    }
                }
                catch (ReflectiveOperationException reflexiveException) {
                    throw new RuntimeException(reflexiveException);
                }
                throw errorMessages;
            }
        }
    }
}

