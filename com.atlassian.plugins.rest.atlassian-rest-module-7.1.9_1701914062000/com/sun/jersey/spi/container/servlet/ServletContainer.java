/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.sun.jersey.spi.container.servlet;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.uri.UriComponent;
import com.sun.jersey.server.impl.application.DeferredResourceConfig;
import com.sun.jersey.spi.container.ContainerNotifier;
import com.sun.jersey.spi.container.ReloadListener;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.WebApplicationFactory;
import com.sun.jersey.spi.container.servlet.WebComponent;
import com.sun.jersey.spi.container.servlet.WebConfig;
import com.sun.jersey.spi.container.servlet.WebFilterConfig;
import com.sun.jersey.spi.container.servlet.WebServletConfig;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import com.sun.jersey.spi.service.ServiceFinder;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

public class ServletContainer
extends HttpServlet
implements Filter {
    public static final String GLASSFISH_DEFAULT_ERROR_PAGE_RESPONSE = "org.glassfish.web.isDefaultErrorPageEnabled";
    public static final String APPLICATION_CONFIG_CLASS = "javax.ws.rs.Application";
    public static final String RESOURCE_CONFIG_CLASS = "com.sun.jersey.config.property.resourceConfigClass";
    public static final String JSP_TEMPLATES_BASE_PATH = "com.sun.jersey.config.property.JSPTemplatesBasePath";
    public static final String PROPERTY_WEB_PAGE_CONTENT_REGEX = "com.sun.jersey.config.property.WebPageContentRegex";
    public static final String FEATURE_FILTER_FORWARD_ON_404 = "com.sun.jersey.config.feature.FilterForwardOn404";
    public static final String PROPERTY_FILTER_CONTEXT_PATH = "com.sun.jersey.config.feature.FilterContextPath";
    public static final String FEATURE_ALLOW_RAW_MANAGED_BEANS = "com.sun.jersey.config.feature.AllowRawManagedBeans";
    private transient WebComponent webComponent;
    private transient FilterConfig filterConfig;
    private transient Pattern staticContentPattern;
    private transient boolean forwardOn404;
    private final transient Application app;
    private String filterContextPath = null;

    public ServletContainer() {
        this.app = null;
    }

    public ServletContainer(Class<? extends Application> appClass) {
        this.app = new DeferredResourceConfig(appClass);
    }

    public ServletContainer(Application app) {
        this.app = app;
    }

    public ServletContext getServletContext() {
        if (this.filterConfig != null) {
            return this.filterConfig.getServletContext();
        }
        return super.getServletContext();
    }

    protected void init(WebConfig webConfig) throws ServletException {
        this.webComponent = this.app == null ? new InternalWebComponent() : new InternalWebComponent(this.app);
        this.webComponent.init(webConfig);
    }

    protected WebConfig getWebConfig() {
        return this.webComponent.getWebConfig();
    }

    protected WebApplication create() {
        return WebApplicationFactory.createWebApplication();
    }

    protected ResourceConfig getDefaultResourceConfig(Map<String, Object> props, WebConfig wc) throws ServletException {
        return this.webComponent.getWebAppResourceConfig(props, wc);
    }

    protected void configure(WebConfig wc, ResourceConfig rc, WebApplication wa) {
        if (this.getServletConfig() != null) {
            this.configure(this.getServletConfig(), rc, wa);
        } else if (this.filterConfig != null) {
            this.configure(this.filterConfig, rc, wa);
        }
        if (rc instanceof ReloadListener) {
            ArrayList<ContainerNotifier> notifiers = new ArrayList<ContainerNotifier>();
            Object o = rc.getProperties().get("com.sun.jersey.spi.container.ContainerNotifier");
            if (o instanceof ContainerNotifier) {
                notifiers.add((ContainerNotifier)o);
            } else if (o instanceof List) {
                for (Object elem : (List)o) {
                    if (!(elem instanceof ContainerNotifier)) continue;
                    notifiers.add((ContainerNotifier)elem);
                }
            }
            for (ContainerNotifier cn : ServiceFinder.find(ContainerNotifier.class)) {
                notifiers.add(cn);
            }
            rc.getProperties().put("com.sun.jersey.spi.container.ContainerNotifier", notifiers);
        }
    }

    protected void initiate(ResourceConfig rc, WebApplication wa) {
        wa.initiate(rc);
    }

    public void load() {
        this.webComponent.load();
    }

    public void reload() {
        this.webComponent.onReload();
    }

    public int service(URI baseUri, URI requestUri, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return this.webComponent.service(baseUri, requestUri, request, response);
    }

    public void destroy() {
        if (this.webComponent != null) {
            this.webComponent.destroy();
        }
    }

    public void init() throws ServletException {
        this.init(new WebServletConfig(this));
    }

    @Deprecated
    protected ResourceConfig getDefaultResourceConfig(Map<String, Object> props, ServletConfig servletConfig) throws ServletException {
        return this.getDefaultResourceConfig(props, this.getWebConfig());
    }

    protected void configure(ServletConfig sc, ResourceConfig rc, WebApplication wa) {
        rc.getSingletons().add(new ContextInjectableProvider<ServletConfig>((Type)((Object)ServletConfig.class), sc));
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String encodedBasePath;
        String decodedBasePath;
        int i;
        UriBuilder absoluteUriBuilder;
        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();
        StringBuffer requestURL = request.getRequestURL();
        String requestURI = request.getRequestURI();
        boolean checkPathInfo = pathInfo == null || pathInfo.isEmpty() || pathInfo.equals("/");
        try {
            absoluteUriBuilder = UriBuilder.fromUri(requestURL.toString());
        }
        catch (IllegalArgumentException iae) {
            Response.Status badRequest = Response.Status.BAD_REQUEST;
            response.sendError(badRequest.getStatusCode(), badRequest.getReasonPhrase());
            return;
        }
        if (checkPathInfo && !request.getRequestURI().endsWith("/") && servletPath.substring((i = servletPath.lastIndexOf("/")) + 1).indexOf(46) < 0) {
            if (this.webComponent.getResourceConfig().getFeature("com.sun.jersey.config.feature.Redirect")) {
                URI l = absoluteUriBuilder.path("/").replaceQuery(request.getQueryString()).build(new Object[0]);
                response.setStatus(307);
                response.setHeader("Location", l.toASCIIString());
                return;
            }
            requestURL.append("/");
            requestURI = requestURI + "/";
        }
        if (!(decodedBasePath = request.getContextPath() + servletPath + "/").equals(encodedBasePath = UriComponent.encode(decodedBasePath, UriComponent.Type.PATH))) {
            throw new ContainerException("The servlet context path and/or the servlet path contain characters that are percent encoded");
        }
        URI baseUri = absoluteUriBuilder.replacePath(encodedBasePath).build(new Object[0]);
        String queryParameters = request.getQueryString();
        if (queryParameters == null) {
            queryParameters = "";
        }
        URI requestUri = absoluteUriBuilder.replacePath(requestURI).replaceQuery(queryParameters).build(new Object[0]);
        this.service(baseUri, requestUri, request, response);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        this.init(new WebFilterConfig(filterConfig));
    }

    public Pattern getStaticContentPattern() {
        return this.staticContentPattern;
    }

    protected void configure(FilterConfig fc, ResourceConfig rc, WebApplication wa) {
        rc.getSingletons().add(new ContextInjectableProvider<FilterConfig>((Type)((Object)FilterConfig.class), fc));
        String regex = (String)rc.getProperty(PROPERTY_WEB_PAGE_CONTENT_REGEX);
        if (regex != null && regex.length() > 0) {
            try {
                this.staticContentPattern = Pattern.compile(regex);
            }
            catch (PatternSyntaxException ex) {
                throw new ContainerException("The syntax is invalid for the regular expression, " + regex + ", associated with the initialization parameter " + PROPERTY_WEB_PAGE_CONTENT_REGEX, ex);
            }
        }
        this.forwardOn404 = rc.getFeature(FEATURE_FILTER_FORWARD_ON_404);
        this.filterContextPath = this.filterConfig.getInitParameter(PROPERTY_FILTER_CONTEXT_PATH);
        if (this.filterContextPath != null) {
            if (this.filterContextPath.isEmpty()) {
                this.filterContextPath = null;
            } else {
                if (!this.filterContextPath.startsWith("/")) {
                    this.filterContextPath = '/' + this.filterContextPath;
                }
                if (this.filterContextPath.endsWith("/")) {
                    this.filterContextPath = this.filterContextPath.substring(0, this.filterContextPath.length() - 1);
                }
            }
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            this.doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
        }
        catch (ClassCastException e) {
            throw new ServletException("non-HTTP request or response");
        }
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String includeRequestURI;
        if (request.getAttribute("javax.servlet.include.request_uri") != null && !(includeRequestURI = (String)request.getAttribute("javax.servlet.include.request_uri")).equals(request.getRequestURI())) {
            this.doFilter(request, response, chain, includeRequestURI, (String)request.getAttribute("javax.servlet.include.servlet_path"), (String)request.getAttribute("javax.servlet.include.query_string"));
            return;
        }
        String servletPath = request.getServletPath() + (request.getPathInfo() == null ? "" : request.getPathInfo());
        this.doFilter(request, response, chain, request.getRequestURI(), servletPath, request.getQueryString());
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain, String requestURI, String servletPath, String queryString) throws IOException, ServletException {
        Pattern p = this.getStaticContentPattern();
        if (p != null && p.matcher(servletPath).matches()) {
            chain.doFilter((ServletRequest)request, (ServletResponse)response);
            return;
        }
        if (this.filterContextPath != null) {
            if (!servletPath.startsWith(this.filterContextPath)) {
                throw new ContainerException("The servlet path, \"" + servletPath + "\", does not start with the filter context path, \"" + this.filterContextPath + "\"");
            }
            if (servletPath.length() == this.filterContextPath.length()) {
                if (this.webComponent.getResourceConfig().getFeature("com.sun.jersey.config.feature.Redirect")) {
                    URI l = UriBuilder.fromUri(request.getRequestURL().toString()).path("/").replaceQuery(queryString).build(new Object[0]);
                    response.setStatus(307);
                    response.setHeader("Location", l.toASCIIString());
                    return;
                }
                requestURI = requestURI + "/";
            }
        }
        UriBuilder absoluteUriBuilder = UriBuilder.fromUri(request.getRequestURL().toString());
        URI baseUri = this.filterContextPath == null ? absoluteUriBuilder.replacePath(request.getContextPath()).path("/").build(new Object[0]) : absoluteUriBuilder.replacePath(request.getContextPath()).path(this.filterContextPath).path("/").build(new Object[0]);
        URI requestUri = absoluteUriBuilder.replacePath(requestURI).replaceQuery(queryString).build(new Object[0]);
        int status = this.service(baseUri, requestUri, request, response);
        if (this.forwardOn404 && status == 404 && !response.isCommitted()) {
            response.setStatus(200);
            chain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
    }

    private class InternalWebComponent
    extends WebComponent {
        InternalWebComponent() {
        }

        InternalWebComponent(Application app) {
            super(app);
        }

        @Override
        protected WebApplication create() {
            return ServletContainer.this.create();
        }

        @Override
        protected void configure(WebConfig wc, ResourceConfig rc, WebApplication wa) {
            super.configure(wc, rc, wa);
            ServletContainer.this.configure(wc, rc, wa);
        }

        @Override
        protected void initiate(ResourceConfig rc, WebApplication wa) {
            ServletContainer.this.initiate(rc, wa);
        }

        @Override
        protected ResourceConfig getDefaultResourceConfig(Map<String, Object> props, WebConfig wc) throws ServletException {
            return ServletContainer.this.getDefaultResourceConfig(props, wc);
        }
    }

    protected static class ContextInjectableProvider<T>
    extends SingletonTypeInjectableProvider<Context, T> {
        protected ContextInjectableProvider(Type type, T instance) {
            super(type, instance);
        }
    }
}

