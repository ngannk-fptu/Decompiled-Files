/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.DispatcherType
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 */
package org.springframework.web.servlet;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SourceFilteringListener;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ConfigurableWebEnvironment;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.context.support.ServletRequestHandledEvent;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HttpServletBean;
import org.springframework.web.util.NestedServletException;
import org.springframework.web.util.WebUtils;

public abstract class FrameworkServlet
extends HttpServletBean
implements ApplicationContextAware {
    public static final String DEFAULT_NAMESPACE_SUFFIX = "-servlet";
    public static final Class<?> DEFAULT_CONTEXT_CLASS = XmlWebApplicationContext.class;
    public static final String SERVLET_CONTEXT_PREFIX = FrameworkServlet.class.getName() + ".CONTEXT.";
    private static final String INIT_PARAM_DELIMITERS = ",; \t\n";
    @Nullable
    private String contextAttribute;
    private Class<?> contextClass = DEFAULT_CONTEXT_CLASS;
    @Nullable
    private String contextId;
    @Nullable
    private String namespace;
    @Nullable
    private String contextConfigLocation;
    private final List<ApplicationContextInitializer<ConfigurableApplicationContext>> contextInitializers = new ArrayList<ApplicationContextInitializer<ConfigurableApplicationContext>>();
    @Nullable
    private String contextInitializerClasses;
    private boolean publishContext = true;
    private boolean publishEvents = true;
    private boolean threadContextInheritable = false;
    private boolean dispatchOptionsRequest = false;
    private boolean dispatchTraceRequest = false;
    private boolean enableLoggingRequestDetails = false;
    @Nullable
    private WebApplicationContext webApplicationContext;
    private boolean webApplicationContextInjected = false;
    private volatile boolean refreshEventReceived;
    private final Object onRefreshMonitor = new Object();

    public FrameworkServlet() {
    }

    public FrameworkServlet(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    public void setContextAttribute(@Nullable String contextAttribute) {
        this.contextAttribute = contextAttribute;
    }

    @Nullable
    public String getContextAttribute() {
        return this.contextAttribute;
    }

    public void setContextClass(Class<?> contextClass) {
        this.contextClass = contextClass;
    }

    public Class<?> getContextClass() {
        return this.contextClass;
    }

    public void setContextId(@Nullable String contextId) {
        this.contextId = contextId;
    }

    @Nullable
    public String getContextId() {
        return this.contextId;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getNamespace() {
        return this.namespace != null ? this.namespace : this.getServletName() + DEFAULT_NAMESPACE_SUFFIX;
    }

    public void setContextConfigLocation(@Nullable String contextConfigLocation) {
        this.contextConfigLocation = contextConfigLocation;
    }

    @Nullable
    public String getContextConfigLocation() {
        return this.contextConfigLocation;
    }

    public void setContextInitializers(ApplicationContextInitializer<?> ... initializers) {
        if (initializers != null) {
            for (ApplicationContextInitializer<?> initializer : initializers) {
                this.contextInitializers.add(initializer);
            }
        }
    }

    public void setContextInitializerClasses(String contextInitializerClasses) {
        this.contextInitializerClasses = contextInitializerClasses;
    }

    public void setPublishContext(boolean publishContext) {
        this.publishContext = publishContext;
    }

    public void setPublishEvents(boolean publishEvents) {
        this.publishEvents = publishEvents;
    }

    public void setThreadContextInheritable(boolean threadContextInheritable) {
        this.threadContextInheritable = threadContextInheritable;
    }

    public void setDispatchOptionsRequest(boolean dispatchOptionsRequest) {
        this.dispatchOptionsRequest = dispatchOptionsRequest;
    }

    public void setDispatchTraceRequest(boolean dispatchTraceRequest) {
        this.dispatchTraceRequest = dispatchTraceRequest;
    }

    public void setEnableLoggingRequestDetails(boolean enable) {
        this.enableLoggingRequestDetails = enable;
    }

    public boolean isEnableLoggingRequestDetails() {
        return this.enableLoggingRequestDetails;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        if (this.webApplicationContext == null && applicationContext instanceof WebApplicationContext) {
            this.webApplicationContext = (WebApplicationContext)applicationContext;
            this.webApplicationContextInjected = true;
        }
    }

    @Override
    protected final void initServletBean() throws ServletException {
        this.getServletContext().log("Initializing Spring " + this.getClass().getSimpleName() + " '" + this.getServletName() + "'");
        if (this.logger.isInfoEnabled()) {
            this.logger.info((Object)("Initializing Servlet '" + this.getServletName() + "'"));
        }
        long startTime = System.currentTimeMillis();
        try {
            this.webApplicationContext = this.initWebApplicationContext();
            this.initFrameworkServlet();
        }
        catch (RuntimeException | ServletException ex) {
            this.logger.error((Object)"Context initialization failed", ex);
            throw ex;
        }
        if (this.logger.isDebugEnabled()) {
            String value = this.enableLoggingRequestDetails ? "shown which may lead to unsafe logging of potentially sensitive data" : "masked to prevent unsafe logging of potentially sensitive data";
            this.logger.debug((Object)("enableLoggingRequestDetails='" + this.enableLoggingRequestDetails + "': request parameters and headers will be " + value));
        }
        if (this.logger.isInfoEnabled()) {
            this.logger.info((Object)("Completed initialization in " + (System.currentTimeMillis() - startTime) + " ms"));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected WebApplicationContext initWebApplicationContext() {
        Object cwac;
        WebApplicationContext rootContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        WebApplicationContext wac = null;
        if (this.webApplicationContext != null && (wac = this.webApplicationContext) instanceof ConfigurableWebApplicationContext && !(cwac = (ConfigurableWebApplicationContext)wac).isActive()) {
            if (cwac.getParent() == null) {
                cwac.setParent(rootContext);
            }
            this.configureAndRefreshWebApplicationContext((ConfigurableWebApplicationContext)cwac);
        }
        if (wac == null) {
            wac = this.findWebApplicationContext();
        }
        if (wac == null) {
            wac = this.createWebApplicationContext(rootContext);
        }
        if (!this.refreshEventReceived) {
            cwac = this.onRefreshMonitor;
            synchronized (cwac) {
                this.onRefresh(wac);
            }
        }
        if (this.publishContext) {
            String attrName = this.getServletContextAttributeName();
            this.getServletContext().setAttribute(attrName, (Object)wac);
        }
        return wac;
    }

    @Nullable
    protected WebApplicationContext findWebApplicationContext() {
        String attrName = this.getContextAttribute();
        if (attrName == null) {
            return null;
        }
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext(), attrName);
        if (wac == null) {
            throw new IllegalStateException("No WebApplicationContext found: initializer not registered?");
        }
        return wac;
    }

    protected WebApplicationContext createWebApplicationContext(@Nullable ApplicationContext parent) {
        Class<?> contextClass = this.getContextClass();
        if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
            throw new ApplicationContextException("Fatal initialization error in servlet with name '" + this.getServletName() + "': custom WebApplicationContext class [" + contextClass.getName() + "] is not of type ConfigurableWebApplicationContext");
        }
        ConfigurableWebApplicationContext wac = (ConfigurableWebApplicationContext)BeanUtils.instantiateClass(contextClass);
        wac.setEnvironment(this.getEnvironment());
        wac.setParent(parent);
        String configLocation = this.getContextConfigLocation();
        if (configLocation != null) {
            wac.setConfigLocation(configLocation);
        }
        this.configureAndRefreshWebApplicationContext(wac);
        return wac;
    }

    protected void configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext wac) {
        if (ObjectUtils.identityToString(wac).equals(wac.getId())) {
            if (this.contextId != null) {
                wac.setId(this.contextId);
            } else {
                wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX + ObjectUtils.getDisplayString(this.getServletContext().getContextPath()) + '/' + this.getServletName());
            }
        }
        wac.setServletContext(this.getServletContext());
        wac.setServletConfig(this.getServletConfig());
        wac.setNamespace(this.getNamespace());
        wac.addApplicationListener(new SourceFilteringListener(wac, new ContextRefreshListener()));
        Environment env = wac.getEnvironment();
        if (env instanceof ConfigurableWebEnvironment) {
            ((ConfigurableWebEnvironment)env).initPropertySources(this.getServletContext(), this.getServletConfig());
        }
        this.postProcessWebApplicationContext(wac);
        this.applyInitializers(wac);
        wac.refresh();
    }

    protected WebApplicationContext createWebApplicationContext(@Nullable WebApplicationContext parent) {
        return this.createWebApplicationContext((ApplicationContext)parent);
    }

    protected void postProcessWebApplicationContext(ConfigurableWebApplicationContext wac) {
    }

    protected void applyInitializers(ConfigurableApplicationContext wac) {
        String globalClassNames = this.getServletContext().getInitParameter("globalInitializerClasses");
        if (globalClassNames != null) {
            for (String string : StringUtils.tokenizeToStringArray(globalClassNames, INIT_PARAM_DELIMITERS)) {
                this.contextInitializers.add(this.loadInitializer(string, wac));
            }
        }
        if (this.contextInitializerClasses != null) {
            for (String string : StringUtils.tokenizeToStringArray(this.contextInitializerClasses, INIT_PARAM_DELIMITERS)) {
                this.contextInitializers.add(this.loadInitializer(string, wac));
            }
        }
        AnnotationAwareOrderComparator.sort(this.contextInitializers);
        for (ApplicationContextInitializer applicationContextInitializer : this.contextInitializers) {
            applicationContextInitializer.initialize(wac);
        }
    }

    private ApplicationContextInitializer<ConfigurableApplicationContext> loadInitializer(String className, ConfigurableApplicationContext wac) {
        try {
            Class<?> initializerClass = ClassUtils.forName(className, wac.getClassLoader());
            Class<?> initializerContextClass = GenericTypeResolver.resolveTypeArgument(initializerClass, ApplicationContextInitializer.class);
            if (initializerContextClass != null && !initializerContextClass.isInstance(wac)) {
                throw new ApplicationContextException(String.format("Could not apply context initializer [%s] since its generic parameter [%s] is not assignable from the type of application context used by this framework servlet: [%s]", initializerClass.getName(), initializerContextClass.getName(), wac.getClass().getName()));
            }
            return BeanUtils.instantiateClass(initializerClass, ApplicationContextInitializer.class);
        }
        catch (ClassNotFoundException ex) {
            throw new ApplicationContextException(String.format("Could not load class [%s] specified via 'contextInitializerClasses' init-param", className), ex);
        }
    }

    public String getServletContextAttributeName() {
        return SERVLET_CONTEXT_PREFIX + this.getServletName();
    }

    @Nullable
    public final WebApplicationContext getWebApplicationContext() {
        return this.webApplicationContext;
    }

    protected void initFrameworkServlet() throws ServletException {
    }

    public void refresh() {
        WebApplicationContext wac = this.getWebApplicationContext();
        if (!(wac instanceof ConfigurableApplicationContext)) {
            throw new IllegalStateException("WebApplicationContext does not support refresh: " + wac);
        }
        ((ConfigurableApplicationContext)((Object)wac)).refresh();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.refreshEventReceived = true;
        Object object = this.onRefreshMonitor;
        synchronized (object) {
            this.onRefresh(event.getApplicationContext());
        }
    }

    protected void onRefresh(ApplicationContext context) {
    }

    public void destroy() {
        this.getServletContext().log("Destroying Spring FrameworkServlet '" + this.getServletName() + "'");
        if (this.webApplicationContext instanceof ConfigurableApplicationContext && !this.webApplicationContextInjected) {
            ((ConfigurableApplicationContext)((Object)this.webApplicationContext)).close();
        }
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpMethod httpMethod = HttpMethod.resolve(request.getMethod());
        if (httpMethod == HttpMethod.PATCH || httpMethod == null) {
            this.processRequest(request, response);
        } else {
            super.service(request, response);
        }
    }

    protected final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.processRequest(request, response);
    }

    protected final void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.processRequest(request, response);
    }

    protected final void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.processRequest(request, response);
    }

    protected final void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.processRequest(request, response);
    }

    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (this.dispatchOptionsRequest || CorsUtils.isPreFlightRequest(request)) {
            this.processRequest(request, response);
            if (response.containsHeader("Allow")) {
                return;
            }
        }
        super.doOptions(request, (HttpServletResponse)new HttpServletResponseWrapper(response){

            public void setHeader(String name, String value) {
                if ("Allow".equals(name)) {
                    value = (StringUtils.hasLength(value) ? value + ", " : "") + HttpMethod.PATCH.name();
                }
                super.setHeader(name, value);
            }
        });
    }

    protected void doTrace(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (this.dispatchTraceRequest) {
            this.processRequest(request, response);
            if ("message/http".equals(response.getContentType())) {
                return;
            }
        }
        super.doTrace(request, response);
    }

    protected final void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        Throwable failureCause = null;
        LocaleContext previousLocaleContext = LocaleContextHolder.getLocaleContext();
        LocaleContext localeContext = this.buildLocaleContext(request);
        RequestAttributes previousAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes requestAttributes = this.buildRequestAttributes(request, response, previousAttributes);
        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager((ServletRequest)request);
        asyncManager.registerCallableInterceptor(FrameworkServlet.class.getName(), new RequestBindingInterceptor());
        this.initContextHolders(request, localeContext, requestAttributes);
        try {
            this.doService(request, response);
        }
        catch (IOException | ServletException ex) {
            failureCause = ex;
            throw ex;
        }
        catch (Throwable ex) {
            failureCause = ex;
            throw new NestedServletException("Request processing failed", ex);
        }
        finally {
            this.resetContextHolders(request, previousLocaleContext, previousAttributes);
            if (requestAttributes != null) {
                requestAttributes.requestCompleted();
            }
            this.logResult(request, response, failureCause, asyncManager);
            this.publishRequestHandledEvent(request, response, startTime, failureCause);
        }
    }

    @Nullable
    protected LocaleContext buildLocaleContext(HttpServletRequest request) {
        return new SimpleLocaleContext(request.getLocale());
    }

    @Nullable
    protected ServletRequestAttributes buildRequestAttributes(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable RequestAttributes previousAttributes) {
        if (previousAttributes == null || previousAttributes instanceof ServletRequestAttributes) {
            return new ServletRequestAttributes(request, response);
        }
        return null;
    }

    private void initContextHolders(HttpServletRequest request, @Nullable LocaleContext localeContext, @Nullable RequestAttributes requestAttributes) {
        if (localeContext != null) {
            LocaleContextHolder.setLocaleContext(localeContext, this.threadContextInheritable);
        }
        if (requestAttributes != null) {
            RequestContextHolder.setRequestAttributes(requestAttributes, this.threadContextInheritable);
        }
    }

    private void resetContextHolders(HttpServletRequest request, @Nullable LocaleContext prevLocaleContext, @Nullable RequestAttributes previousAttributes) {
        LocaleContextHolder.setLocaleContext(prevLocaleContext, this.threadContextInheritable);
        RequestContextHolder.setRequestAttributes(previousAttributes, this.threadContextInheritable);
    }

    private void logResult(HttpServletRequest request, HttpServletResponse response, @Nullable Throwable failureCause, WebAsyncManager asyncManager) {
        boolean initialDispatch;
        if (!this.logger.isDebugEnabled()) {
            return;
        }
        DispatcherType dispatchType = request.getDispatcherType();
        boolean bl = initialDispatch = dispatchType == DispatcherType.REQUEST;
        if (failureCause != null) {
            if (!initialDispatch) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Unresolved failure from \"" + dispatchType + "\" dispatch: " + failureCause));
                }
            } else if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)"Failed to complete request", failureCause);
            } else {
                this.logger.debug((Object)("Failed to complete request: " + failureCause));
            }
            return;
        }
        if (asyncManager.isConcurrentHandlingStarted()) {
            this.logger.debug((Object)"Exiting but response remains open for further handling");
            return;
        }
        int status = response.getStatus();
        String headers = "";
        if (this.logger.isTraceEnabled()) {
            Collection names = response.getHeaderNames();
            headers = this.enableLoggingRequestDetails ? names.stream().map(name -> name + ":" + response.getHeaders(name)).collect(Collectors.joining(", ")) : (names.isEmpty() ? "" : "masked");
            headers = ", headers={" + headers + "}";
        }
        if (!initialDispatch) {
            this.logger.debug((Object)("Exiting from \"" + dispatchType + "\" dispatch, status " + status + headers));
        } else {
            HttpStatus httpStatus = HttpStatus.resolve(status);
            this.logger.debug((Object)("Completed " + (httpStatus != null ? httpStatus : Integer.valueOf(status)) + headers));
        }
    }

    private void publishRequestHandledEvent(HttpServletRequest request, HttpServletResponse response, long startTime, @Nullable Throwable failureCause) {
        if (this.publishEvents && this.webApplicationContext != null) {
            long processingTime = System.currentTimeMillis() - startTime;
            this.webApplicationContext.publishEvent(new ServletRequestHandledEvent(this, request.getRequestURI(), request.getRemoteAddr(), request.getMethod(), this.getServletConfig().getServletName(), WebUtils.getSessionId(request), this.getUsernameForRequest(request), processingTime, failureCause, response.getStatus()));
        }
    }

    @Nullable
    protected String getUsernameForRequest(HttpServletRequest request) {
        Principal userPrincipal = request.getUserPrincipal();
        return userPrincipal != null ? userPrincipal.getName() : null;
    }

    protected abstract void doService(HttpServletRequest var1, HttpServletResponse var2) throws Exception;

    private class RequestBindingInterceptor
    implements CallableProcessingInterceptor {
        private RequestBindingInterceptor() {
        }

        @Override
        public <T> void preProcess(NativeWebRequest webRequest, Callable<T> task) {
            HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            if (request != null) {
                HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
                FrameworkServlet.this.initContextHolders(request, FrameworkServlet.this.buildLocaleContext(request), FrameworkServlet.this.buildRequestAttributes(request, response, null));
            }
        }

        @Override
        public <T> void postProcess(NativeWebRequest webRequest, Callable<T> task, Object concurrentResult) {
            HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            if (request != null) {
                FrameworkServlet.this.resetContextHolders(request, null, null);
            }
        }
    }

    private class ContextRefreshListener
    implements ApplicationListener<ContextRefreshedEvent> {
        private ContextRefreshListener() {
        }

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            FrameworkServlet.this.onApplicationEvent(event);
        }
    }
}

