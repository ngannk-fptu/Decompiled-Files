/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.DispatcherType
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.ui.context.ThemeSource;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.FrameworkServlet;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.util.NestedServletException;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.WebUtils;

public class DispatcherServlet
extends FrameworkServlet {
    public static final String MULTIPART_RESOLVER_BEAN_NAME = "multipartResolver";
    public static final String LOCALE_RESOLVER_BEAN_NAME = "localeResolver";
    public static final String THEME_RESOLVER_BEAN_NAME = "themeResolver";
    public static final String HANDLER_MAPPING_BEAN_NAME = "handlerMapping";
    public static final String HANDLER_ADAPTER_BEAN_NAME = "handlerAdapter";
    public static final String HANDLER_EXCEPTION_RESOLVER_BEAN_NAME = "handlerExceptionResolver";
    public static final String REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME = "viewNameTranslator";
    public static final String VIEW_RESOLVER_BEAN_NAME = "viewResolver";
    public static final String FLASH_MAP_MANAGER_BEAN_NAME = "flashMapManager";
    public static final String WEB_APPLICATION_CONTEXT_ATTRIBUTE = DispatcherServlet.class.getName() + ".CONTEXT";
    public static final String LOCALE_RESOLVER_ATTRIBUTE = DispatcherServlet.class.getName() + ".LOCALE_RESOLVER";
    public static final String THEME_RESOLVER_ATTRIBUTE = DispatcherServlet.class.getName() + ".THEME_RESOLVER";
    public static final String THEME_SOURCE_ATTRIBUTE = DispatcherServlet.class.getName() + ".THEME_SOURCE";
    public static final String INPUT_FLASH_MAP_ATTRIBUTE = DispatcherServlet.class.getName() + ".INPUT_FLASH_MAP";
    public static final String OUTPUT_FLASH_MAP_ATTRIBUTE = DispatcherServlet.class.getName() + ".OUTPUT_FLASH_MAP";
    public static final String FLASH_MAP_MANAGER_ATTRIBUTE = DispatcherServlet.class.getName() + ".FLASH_MAP_MANAGER";
    public static final String EXCEPTION_ATTRIBUTE = DispatcherServlet.class.getName() + ".EXCEPTION";
    public static final String PAGE_NOT_FOUND_LOG_CATEGORY = "org.springframework.web.servlet.PageNotFound";
    private static final String DEFAULT_STRATEGIES_PATH = "DispatcherServlet.properties";
    private static final String DEFAULT_STRATEGIES_PREFIX = "org.springframework.web.servlet";
    protected static final Log pageNotFoundLogger = LogFactory.getLog((String)"org.springframework.web.servlet.PageNotFound");
    @Nullable
    private static Properties defaultStrategies;
    private boolean detectAllHandlerMappings = true;
    private boolean detectAllHandlerAdapters = true;
    private boolean detectAllHandlerExceptionResolvers = true;
    private boolean detectAllViewResolvers = true;
    private boolean throwExceptionIfNoHandlerFound = false;
    private boolean cleanupAfterInclude = true;
    @Nullable
    private MultipartResolver multipartResolver;
    @Nullable
    private LocaleResolver localeResolver;
    @Nullable
    private ThemeResolver themeResolver;
    @Nullable
    private List<HandlerMapping> handlerMappings;
    @Nullable
    private List<HandlerAdapter> handlerAdapters;
    @Nullable
    private List<HandlerExceptionResolver> handlerExceptionResolvers;
    @Nullable
    private RequestToViewNameTranslator viewNameTranslator;
    @Nullable
    private FlashMapManager flashMapManager;
    @Nullable
    private List<ViewResolver> viewResolvers;
    private boolean parseRequestPath;

    public DispatcherServlet() {
        this.setDispatchOptionsRequest(true);
    }

    public DispatcherServlet(WebApplicationContext webApplicationContext) {
        super(webApplicationContext);
        this.setDispatchOptionsRequest(true);
    }

    public void setDetectAllHandlerMappings(boolean detectAllHandlerMappings) {
        this.detectAllHandlerMappings = detectAllHandlerMappings;
    }

    public void setDetectAllHandlerAdapters(boolean detectAllHandlerAdapters) {
        this.detectAllHandlerAdapters = detectAllHandlerAdapters;
    }

    public void setDetectAllHandlerExceptionResolvers(boolean detectAllHandlerExceptionResolvers) {
        this.detectAllHandlerExceptionResolvers = detectAllHandlerExceptionResolvers;
    }

    public void setDetectAllViewResolvers(boolean detectAllViewResolvers) {
        this.detectAllViewResolvers = detectAllViewResolvers;
    }

    public void setThrowExceptionIfNoHandlerFound(boolean throwExceptionIfNoHandlerFound) {
        this.throwExceptionIfNoHandlerFound = throwExceptionIfNoHandlerFound;
    }

    public void setCleanupAfterInclude(boolean cleanupAfterInclude) {
        this.cleanupAfterInclude = cleanupAfterInclude;
    }

    @Override
    protected void onRefresh(ApplicationContext context) {
        this.initStrategies(context);
    }

    protected void initStrategies(ApplicationContext context) {
        this.initMultipartResolver(context);
        this.initLocaleResolver(context);
        this.initThemeResolver(context);
        this.initHandlerMappings(context);
        this.initHandlerAdapters(context);
        this.initHandlerExceptionResolvers(context);
        this.initRequestToViewNameTranslator(context);
        this.initViewResolvers(context);
        this.initFlashMapManager(context);
    }

    private void initMultipartResolver(ApplicationContext context) {
        block5: {
            try {
                this.multipartResolver = context.getBean(MULTIPART_RESOLVER_BEAN_NAME, MultipartResolver.class);
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)("Detected " + this.multipartResolver));
                } else if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Detected " + this.multipartResolver.getClass().getSimpleName()));
                }
            }
            catch (NoSuchBeanDefinitionException ex) {
                this.multipartResolver = null;
                if (!this.logger.isTraceEnabled()) break block5;
                this.logger.trace((Object)"No MultipartResolver 'multipartResolver' declared");
            }
        }
    }

    private void initLocaleResolver(ApplicationContext context) {
        block5: {
            try {
                this.localeResolver = context.getBean(LOCALE_RESOLVER_BEAN_NAME, LocaleResolver.class);
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)("Detected " + this.localeResolver));
                } else if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Detected " + this.localeResolver.getClass().getSimpleName()));
                }
            }
            catch (NoSuchBeanDefinitionException ex) {
                this.localeResolver = this.getDefaultStrategy(context, LocaleResolver.class);
                if (!this.logger.isTraceEnabled()) break block5;
                this.logger.trace((Object)("No LocaleResolver 'localeResolver': using default [" + this.localeResolver.getClass().getSimpleName() + "]"));
            }
        }
    }

    private void initThemeResolver(ApplicationContext context) {
        block5: {
            try {
                this.themeResolver = context.getBean(THEME_RESOLVER_BEAN_NAME, ThemeResolver.class);
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)("Detected " + this.themeResolver));
                } else if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Detected " + this.themeResolver.getClass().getSimpleName()));
                }
            }
            catch (NoSuchBeanDefinitionException ex) {
                this.themeResolver = this.getDefaultStrategy(context, ThemeResolver.class);
                if (!this.logger.isTraceEnabled()) break block5;
                this.logger.trace((Object)("No ThemeResolver 'themeResolver': using default [" + this.themeResolver.getClass().getSimpleName() + "]"));
            }
        }
    }

    private void initHandlerMappings(ApplicationContext context) {
        this.handlerMappings = null;
        if (this.detectAllHandlerMappings) {
            Map<String, HandlerMapping> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
            if (!matchingBeans.isEmpty()) {
                this.handlerMappings = new ArrayList<HandlerMapping>(matchingBeans.values());
                AnnotationAwareOrderComparator.sort(this.handlerMappings);
            }
        } else {
            try {
                HandlerMapping hm = context.getBean(HANDLER_MAPPING_BEAN_NAME, HandlerMapping.class);
                this.handlerMappings = Collections.singletonList(hm);
            }
            catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
                // empty catch block
            }
        }
        if (this.handlerMappings == null) {
            this.handlerMappings = this.getDefaultStrategies(context, HandlerMapping.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("No HandlerMappings declared for servlet '" + this.getServletName() + "': using default strategies from DispatcherServlet.properties"));
            }
        }
        for (HandlerMapping mapping : this.handlerMappings) {
            if (!mapping.usesPathPatterns()) continue;
            this.parseRequestPath = true;
            break;
        }
    }

    private void initHandlerAdapters(ApplicationContext context) {
        this.handlerAdapters = null;
        if (this.detectAllHandlerAdapters) {
            Map<String, HandlerAdapter> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerAdapter.class, true, false);
            if (!matchingBeans.isEmpty()) {
                this.handlerAdapters = new ArrayList<HandlerAdapter>(matchingBeans.values());
                AnnotationAwareOrderComparator.sort(this.handlerAdapters);
            }
        } else {
            try {
                HandlerAdapter ha = context.getBean(HANDLER_ADAPTER_BEAN_NAME, HandlerAdapter.class);
                this.handlerAdapters = Collections.singletonList(ha);
            }
            catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
                // empty catch block
            }
        }
        if (this.handlerAdapters == null) {
            this.handlerAdapters = this.getDefaultStrategies(context, HandlerAdapter.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("No HandlerAdapters declared for servlet '" + this.getServletName() + "': using default strategies from DispatcherServlet.properties"));
            }
        }
    }

    private void initHandlerExceptionResolvers(ApplicationContext context) {
        this.handlerExceptionResolvers = null;
        if (this.detectAllHandlerExceptionResolvers) {
            Map<String, HandlerExceptionResolver> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerExceptionResolver.class, true, false);
            if (!matchingBeans.isEmpty()) {
                this.handlerExceptionResolvers = new ArrayList<HandlerExceptionResolver>(matchingBeans.values());
                AnnotationAwareOrderComparator.sort(this.handlerExceptionResolvers);
            }
        } else {
            try {
                HandlerExceptionResolver her = context.getBean(HANDLER_EXCEPTION_RESOLVER_BEAN_NAME, HandlerExceptionResolver.class);
                this.handlerExceptionResolvers = Collections.singletonList(her);
            }
            catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
                // empty catch block
            }
        }
        if (this.handlerExceptionResolvers == null) {
            this.handlerExceptionResolvers = this.getDefaultStrategies(context, HandlerExceptionResolver.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("No HandlerExceptionResolvers declared in servlet '" + this.getServletName() + "': using default strategies from DispatcherServlet.properties"));
            }
        }
    }

    private void initRequestToViewNameTranslator(ApplicationContext context) {
        block5: {
            try {
                this.viewNameTranslator = context.getBean(REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME, RequestToViewNameTranslator.class);
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)("Detected " + this.viewNameTranslator.getClass().getSimpleName()));
                } else if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Detected " + this.viewNameTranslator));
                }
            }
            catch (NoSuchBeanDefinitionException ex) {
                this.viewNameTranslator = this.getDefaultStrategy(context, RequestToViewNameTranslator.class);
                if (!this.logger.isTraceEnabled()) break block5;
                this.logger.trace((Object)("No RequestToViewNameTranslator 'viewNameTranslator': using default [" + this.viewNameTranslator.getClass().getSimpleName() + "]"));
            }
        }
    }

    private void initViewResolvers(ApplicationContext context) {
        this.viewResolvers = null;
        if (this.detectAllViewResolvers) {
            Map<String, ViewResolver> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, ViewResolver.class, true, false);
            if (!matchingBeans.isEmpty()) {
                this.viewResolvers = new ArrayList<ViewResolver>(matchingBeans.values());
                AnnotationAwareOrderComparator.sort(this.viewResolvers);
            }
        } else {
            try {
                ViewResolver vr = context.getBean(VIEW_RESOLVER_BEAN_NAME, ViewResolver.class);
                this.viewResolvers = Collections.singletonList(vr);
            }
            catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
                // empty catch block
            }
        }
        if (this.viewResolvers == null) {
            this.viewResolvers = this.getDefaultStrategies(context, ViewResolver.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("No ViewResolvers declared for servlet '" + this.getServletName() + "': using default strategies from DispatcherServlet.properties"));
            }
        }
    }

    private void initFlashMapManager(ApplicationContext context) {
        block5: {
            try {
                this.flashMapManager = context.getBean(FLASH_MAP_MANAGER_BEAN_NAME, FlashMapManager.class);
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)("Detected " + this.flashMapManager.getClass().getSimpleName()));
                } else if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Detected " + this.flashMapManager));
                }
            }
            catch (NoSuchBeanDefinitionException ex) {
                this.flashMapManager = this.getDefaultStrategy(context, FlashMapManager.class);
                if (!this.logger.isTraceEnabled()) break block5;
                this.logger.trace((Object)("No FlashMapManager 'flashMapManager': using default [" + this.flashMapManager.getClass().getSimpleName() + "]"));
            }
        }
    }

    @Nullable
    public final ThemeSource getThemeSource() {
        return this.getWebApplicationContext() instanceof ThemeSource ? (ThemeSource)((Object)this.getWebApplicationContext()) : null;
    }

    @Nullable
    public final MultipartResolver getMultipartResolver() {
        return this.multipartResolver;
    }

    @Nullable
    public final List<HandlerMapping> getHandlerMappings() {
        return this.handlerMappings != null ? Collections.unmodifiableList(this.handlerMappings) : null;
    }

    protected <T> T getDefaultStrategy(ApplicationContext context, Class<T> strategyInterface) {
        List<T> strategies = this.getDefaultStrategies(context, strategyInterface);
        if (strategies.size() != 1) {
            throw new BeanInitializationException("DispatcherServlet needs exactly 1 strategy for interface [" + strategyInterface.getName() + "]");
        }
        return strategies.get(0);
    }

    protected <T> List<T> getDefaultStrategies(ApplicationContext context, Class<T> strategyInterface) {
        String key;
        String value;
        if (defaultStrategies == null) {
            try {
                ClassPathResource resource = new ClassPathResource(DEFAULT_STRATEGIES_PATH, DispatcherServlet.class);
                defaultStrategies = PropertiesLoaderUtils.loadProperties(resource);
            }
            catch (IOException ex) {
                throw new IllegalStateException("Could not load 'DispatcherServlet.properties': " + ex.getMessage());
            }
        }
        if ((value = defaultStrategies.getProperty(key = strategyInterface.getName())) != null) {
            String[] classNames = StringUtils.commaDelimitedListToStringArray(value);
            ArrayList<Object> strategies = new ArrayList<Object>(classNames.length);
            for (String className : classNames) {
                try {
                    Class<?> clazz = ClassUtils.forName(className, DispatcherServlet.class.getClassLoader());
                    Object strategy = this.createDefaultStrategy(context, clazz);
                    strategies.add(strategy);
                }
                catch (ClassNotFoundException ex) {
                    throw new BeanInitializationException("Could not find DispatcherServlet's default strategy class [" + className + "] for interface [" + key + "]", ex);
                }
                catch (LinkageError err) {
                    throw new BeanInitializationException("Unresolvable class definition for DispatcherServlet's default strategy class [" + className + "] for interface [" + key + "]", err);
                }
            }
            return strategies;
        }
        return Collections.emptyList();
    }

    protected Object createDefaultStrategy(ApplicationContext context, Class<?> clazz) {
        return context.getAutowireCapableBeanFactory().createBean(clazz);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.logRequest(request);
        HashMap<String, Object> attributesSnapshot = null;
        if (WebUtils.isIncludeRequest((ServletRequest)request)) {
            attributesSnapshot = new HashMap<String, Object>();
            Enumeration attrNames = request.getAttributeNames();
            while (attrNames.hasMoreElements()) {
                String attrName = (String)attrNames.nextElement();
                if (!this.cleanupAfterInclude && !attrName.startsWith(DEFAULT_STRATEGIES_PREFIX)) continue;
                attributesSnapshot.put(attrName, request.getAttribute(attrName));
            }
        }
        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, (Object)this.getWebApplicationContext());
        request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, (Object)this.localeResolver);
        request.setAttribute(THEME_RESOLVER_ATTRIBUTE, (Object)this.themeResolver);
        request.setAttribute(THEME_SOURCE_ATTRIBUTE, (Object)this.getThemeSource());
        if (this.flashMapManager != null) {
            FlashMap inputFlashMap = this.flashMapManager.retrieveAndUpdate(request, response);
            if (inputFlashMap != null) {
                request.setAttribute(INPUT_FLASH_MAP_ATTRIBUTE, Collections.unmodifiableMap(inputFlashMap));
            }
            request.setAttribute(OUTPUT_FLASH_MAP_ATTRIBUTE, (Object)new FlashMap());
            request.setAttribute(FLASH_MAP_MANAGER_ATTRIBUTE, (Object)this.flashMapManager);
        }
        RequestPath previousRequestPath = null;
        if (this.parseRequestPath) {
            previousRequestPath = (RequestPath)request.getAttribute(ServletRequestPathUtils.PATH_ATTRIBUTE);
            ServletRequestPathUtils.parseAndCache(request);
        }
        try {
            this.doDispatch(request, response);
        }
        finally {
            if (!WebAsyncUtils.getAsyncManager((ServletRequest)request).isConcurrentHandlingStarted() && attributesSnapshot != null) {
                this.restoreAttributesAfterInclude(request, attributesSnapshot);
            }
            if (this.parseRequestPath) {
                ServletRequestPathUtils.setParsedRequestPath(previousRequestPath, (ServletRequest)request);
            }
        }
    }

    private void logRequest(HttpServletRequest request) {
        LogFormatUtils.traceDebug(this.logger, traceOn -> {
            String contentType = request.getContentType();
            String params = StringUtils.startsWithIgnoreCase(contentType, "multipart/") ? "multipart" : (this.isEnableLoggingRequestDetails() ? request.getParameterMap().entrySet().stream().map(entry -> (String)entry.getKey() + ":" + Arrays.toString((Object[])entry.getValue())).collect(Collectors.joining(", ")) : (StringUtils.startsWithIgnoreCase(contentType, "application/x-www-form-urlencoded") || !request.getParameterMap().isEmpty() ? "masked" : ""));
            String queryString = request.getQueryString();
            String queryClause = StringUtils.hasLength(queryString) ? "?" + queryString : "";
            String dispatchType = !DispatcherType.REQUEST.equals((Object)request.getDispatcherType()) ? "\"" + request.getDispatcherType() + "\" dispatch for " : "";
            String message = dispatchType + request.getMethod() + " \"" + DispatcherServlet.getRequestUri(request) + queryClause + "\", parameters={" + params + "}";
            if (traceOn.booleanValue()) {
                String headers;
                ArrayList values = Collections.list(request.getHeaderNames());
                String string = headers = values.size() > 0 ? "masked" : "";
                if (this.isEnableLoggingRequestDetails()) {
                    headers = values.stream().map(name -> name + ":" + Collections.list(request.getHeaders(name))).collect(Collectors.joining(", "));
                }
                return message + ", headers={" + headers + "} in DispatcherServlet '" + this.getServletName() + "'";
            }
            return message;
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object dispatchException;
        ModelAndView mv;
        WebAsyncManager asyncManager;
        boolean multipartRequestParsed;
        HandlerExecutionChain mappedHandler;
        HttpServletRequest processedRequest;
        block38: {
            HandlerAdapter ha;
            block37: {
                block36: {
                    long lastModified;
                    block35: {
                        processedRequest = request;
                        mappedHandler = null;
                        multipartRequestParsed = false;
                        asyncManager = WebAsyncUtils.getAsyncManager((ServletRequest)request);
                        mv = null;
                        dispatchException = null;
                        processedRequest = this.checkMultipart(request);
                        multipartRequestParsed = processedRequest != request;
                        mappedHandler = this.getHandler(processedRequest);
                        if (mappedHandler != null) break block35;
                        this.noHandlerFound(processedRequest, response);
                        if (asyncManager.isConcurrentHandlingStarted()) {
                            if (mappedHandler != null) {
                                mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
                            }
                        } else if (multipartRequestParsed) {
                            this.cleanupMultipart(processedRequest);
                        }
                        return;
                    }
                    ha = this.getHandlerAdapter(mappedHandler.getHandler());
                    String method = request.getMethod();
                    boolean isGet = HttpMethod.GET.matches(method);
                    if (!isGet && !HttpMethod.HEAD.matches(method) || !new ServletWebRequest(request, response).checkNotModified(lastModified = ha.getLastModified(request, mappedHandler.getHandler())) || !isGet) break block36;
                    if (asyncManager.isConcurrentHandlingStarted()) {
                        if (mappedHandler != null) {
                            mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
                        }
                    } else if (multipartRequestParsed) {
                        this.cleanupMultipart(processedRequest);
                    }
                    return;
                }
                if (mappedHandler.applyPreHandle(processedRequest, response)) break block37;
                if (asyncManager.isConcurrentHandlingStarted()) {
                    if (mappedHandler != null) {
                        mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
                    }
                } else if (multipartRequestParsed) {
                    this.cleanupMultipart(processedRequest);
                }
                return;
            }
            mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
            if (!asyncManager.isConcurrentHandlingStarted()) break block38;
            if (asyncManager.isConcurrentHandlingStarted()) {
                if (mappedHandler != null) {
                    mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
                }
            } else if (multipartRequestParsed) {
                this.cleanupMultipart(processedRequest);
            }
            return;
        }
        try {
            block39: {
                this.applyDefaultViewName(processedRequest, mv);
                mappedHandler.applyPostHandle(processedRequest, response, mv);
                {
                    catch (Exception ex) {
                        dispatchException = ex;
                        break block39;
                    }
                    catch (Throwable err) {
                        dispatchException = new NestedServletException("Handler dispatch failed", err);
                    }
                }
            }
            this.processDispatchResult(processedRequest, response, mappedHandler, mv, (Exception)dispatchException);
        }
        catch (Exception ex) {
            this.triggerAfterCompletion(processedRequest, response, mappedHandler, ex);
        }
        catch (Throwable err) {
            this.triggerAfterCompletion(processedRequest, response, mappedHandler, (Exception)((Object)new NestedServletException("Handler processing failed", err)));
        }
        catch (Throwable throwable) {
            throw throwable;
        }
        finally {
            if (asyncManager.isConcurrentHandlingStarted()) {
                if (mappedHandler != null) {
                    mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
                }
            } else if (multipartRequestParsed) {
                this.cleanupMultipart(processedRequest);
            }
        }
    }

    private void applyDefaultViewName(HttpServletRequest request, @Nullable ModelAndView mv) throws Exception {
        String defaultViewName;
        if (mv != null && !mv.hasView() && (defaultViewName = this.getDefaultViewName(request)) != null) {
            mv.setViewName(defaultViewName);
        }
    }

    private void processDispatchResult(HttpServletRequest request, HttpServletResponse response, @Nullable HandlerExecutionChain mappedHandler, @Nullable ModelAndView mv, @Nullable Exception exception) throws Exception {
        boolean errorView = false;
        if (exception != null) {
            if (exception instanceof ModelAndViewDefiningException) {
                this.logger.debug((Object)"ModelAndViewDefiningException encountered", (Throwable)exception);
                mv = ((ModelAndViewDefiningException)((Object)exception)).getModelAndView();
            } else {
                Object handler = mappedHandler != null ? mappedHandler.getHandler() : null;
                mv = this.processHandlerException(request, response, handler, exception);
                boolean bl = errorView = mv != null;
            }
        }
        if (mv != null && !mv.wasCleared()) {
            this.render(mv, request, response);
            if (errorView) {
                WebUtils.clearErrorRequestAttributes(request);
            }
        } else if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)"No view rendering, null ModelAndView returned.");
        }
        if (WebAsyncUtils.getAsyncManager((ServletRequest)request).isConcurrentHandlingStarted()) {
            return;
        }
        if (mappedHandler != null) {
            mappedHandler.triggerAfterCompletion(request, response, null);
        }
    }

    @Override
    protected LocaleContext buildLocaleContext(HttpServletRequest request) {
        LocaleResolver lr = this.localeResolver;
        if (lr instanceof LocaleContextResolver) {
            return ((LocaleContextResolver)lr).resolveLocaleContext(request);
        }
        return () -> lr != null ? lr.resolveLocale(request) : request.getLocale();
    }

    protected HttpServletRequest checkMultipart(HttpServletRequest request) throws MultipartException {
        if (this.multipartResolver != null && this.multipartResolver.isMultipart(request)) {
            if (WebUtils.getNativeRequest((ServletRequest)request, MultipartHttpServletRequest.class) != null) {
                if (DispatcherType.REQUEST.equals((Object)request.getDispatcherType())) {
                    this.logger.trace((Object)"Request already resolved to MultipartHttpServletRequest, e.g. by MultipartFilter");
                }
            } else if (this.hasMultipartException(request)) {
                this.logger.debug((Object)"Multipart resolution previously failed for current request - skipping re-resolution for undisturbed error rendering");
            } else {
                try {
                    return this.multipartResolver.resolveMultipart(request);
                }
                catch (MultipartException ex) {
                    if (request.getAttribute("javax.servlet.error.exception") != null) {
                        this.logger.debug((Object)"Multipart resolution failed for error dispatch", (Throwable)ex);
                    }
                    throw ex;
                }
            }
        }
        return request;
    }

    private boolean hasMultipartException(HttpServletRequest request) {
        for (Throwable error = (Throwable)request.getAttribute("javax.servlet.error.exception"); error != null; error = error.getCause()) {
            if (!(error instanceof MultipartException)) continue;
            return true;
        }
        return false;
    }

    protected void cleanupMultipart(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest;
        if (this.multipartResolver != null && (multipartRequest = WebUtils.getNativeRequest((ServletRequest)request, MultipartHttpServletRequest.class)) != null) {
            this.multipartResolver.cleanupMultipart(multipartRequest);
        }
    }

    @Nullable
    protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        if (this.handlerMappings != null) {
            for (HandlerMapping mapping : this.handlerMappings) {
                HandlerExecutionChain handler = mapping.getHandler(request);
                if (handler == null) continue;
                return handler;
            }
        }
        return null;
    }

    protected void noHandlerFound(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (pageNotFoundLogger.isWarnEnabled()) {
            pageNotFoundLogger.warn((Object)("No mapping for " + request.getMethod() + " " + DispatcherServlet.getRequestUri(request)));
        }
        if (this.throwExceptionIfNoHandlerFound) {
            throw new NoHandlerFoundException(request.getMethod(), DispatcherServlet.getRequestUri(request), new ServletServerHttpRequest(request).getHeaders());
        }
        response.sendError(404);
    }

    protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
        if (this.handlerAdapters != null) {
            for (HandlerAdapter adapter : this.handlerAdapters) {
                if (!adapter.supports(handler)) continue;
                return adapter;
            }
        }
        throw new ServletException("No adapter for handler [" + handler + "]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler");
    }

    @Nullable
    protected ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) throws Exception {
        request.removeAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
        ModelAndView exMv = null;
        if (this.handlerExceptionResolvers != null) {
            HandlerExceptionResolver resolver;
            Iterator<HandlerExceptionResolver> iterator = this.handlerExceptionResolvers.iterator();
            while (iterator.hasNext() && (exMv = (resolver = iterator.next()).resolveException(request, response, handler, ex)) == null) {
            }
        }
        if (exMv != null) {
            String defaultViewName;
            if (exMv.isEmpty()) {
                request.setAttribute(EXCEPTION_ATTRIBUTE, (Object)ex);
                return null;
            }
            if (!exMv.hasView() && (defaultViewName = this.getDefaultViewName(request)) != null) {
                exMv.setViewName(defaultViewName);
            }
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Using resolved error view: " + exMv), (Throwable)ex);
            } else if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Using resolved error view: " + exMv));
            }
            WebUtils.exposeErrorRequestAttributes(request, ex, this.getServletName());
            return exMv;
        }
        throw ex;
    }

    protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
        View view;
        Locale locale = this.localeResolver != null ? this.localeResolver.resolveLocale(request) : request.getLocale();
        response.setLocale(locale);
        String viewName = mv.getViewName();
        if (viewName != null) {
            view = this.resolveViewName(viewName, mv.getModelInternal(), locale, request);
            if (view == null) {
                throw new ServletException("Could not resolve view with name '" + mv.getViewName() + "' in servlet with name '" + this.getServletName() + "'");
            }
        } else {
            view = mv.getView();
            if (view == null) {
                throw new ServletException("ModelAndView [" + mv + "] neither contains a view name nor a View object in servlet with name '" + this.getServletName() + "'");
            }
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Rendering view [" + view + "] "));
        }
        try {
            if (mv.getStatus() != null) {
                request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, (Object)mv.getStatus());
                response.setStatus(mv.getStatus().value());
            }
            view.render(mv.getModelInternal(), request, response);
        }
        catch (Exception ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Error rendering view [" + view + "]"), (Throwable)ex);
            }
            throw ex;
        }
    }

    @Nullable
    protected String getDefaultViewName(HttpServletRequest request) throws Exception {
        return this.viewNameTranslator != null ? this.viewNameTranslator.getViewName(request) : null;
    }

    @Nullable
    protected View resolveViewName(String viewName, @Nullable Map<String, Object> model, Locale locale, HttpServletRequest request) throws Exception {
        if (this.viewResolvers != null) {
            for (ViewResolver viewResolver : this.viewResolvers) {
                View view = viewResolver.resolveViewName(viewName, locale);
                if (view == null) continue;
                return view;
            }
        }
        return null;
    }

    private void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, @Nullable HandlerExecutionChain mappedHandler, Exception ex) throws Exception {
        if (mappedHandler != null) {
            mappedHandler.triggerAfterCompletion(request, response, ex);
        }
        throw ex;
    }

    private void restoreAttributesAfterInclude(HttpServletRequest request, Map<?, ?> attributesSnapshot) {
        HashSet<String> attrsToCheck = new HashSet<String>();
        Enumeration attrNames = request.getAttributeNames();
        while (attrNames.hasMoreElements()) {
            String attrName = (String)attrNames.nextElement();
            if (!this.cleanupAfterInclude && !attrName.startsWith(DEFAULT_STRATEGIES_PREFIX)) continue;
            attrsToCheck.add(attrName);
        }
        attrsToCheck.addAll(attributesSnapshot.keySet());
        for (String attrName : attrsToCheck) {
            Object attrValue = attributesSnapshot.get(attrName);
            if (attrValue == null) {
                request.removeAttribute(attrName);
                continue;
            }
            if (attrValue == request.getAttribute(attrName)) continue;
            request.setAttribute(attrName, attrValue);
        }
    }

    private static String getRequestUri(HttpServletRequest request) {
        String uri = (String)request.getAttribute("javax.servlet.include.request_uri");
        if (uri == null) {
            uri = request.getRequestURI();
        }
        return uri;
    }
}

