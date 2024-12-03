/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.apache.commons.logging.Log
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.context.ApplicationContext
 *  org.springframework.core.DefaultParameterNameDiscoverer
 *  org.springframework.core.KotlinDetector
 *  org.springframework.core.MethodIntrospector
 *  org.springframework.core.ParameterNameDiscoverer
 *  org.springframework.core.ReactiveAdapterRegistry
 *  org.springframework.core.SpringProperties
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.core.log.LogFormatUtils
 *  org.springframework.core.task.AsyncTaskExecutor
 *  org.springframework.core.task.SimpleAsyncTaskExecutor
 *  org.springframework.core.task.TaskExecutor
 *  org.springframework.http.converter.ByteArrayHttpMessageConverter
 *  org.springframework.http.converter.HttpMessageConverter
 *  org.springframework.http.converter.StringHttpMessageConverter
 *  org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter
 *  org.springframework.http.converter.xml.SourceHttpMessageConverter
 *  org.springframework.lang.Nullable
 *  org.springframework.ui.ModelMap
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ReflectionUtils$MethodFilter
 *  org.springframework.web.accept.ContentNegotiationManager
 *  org.springframework.web.bind.annotation.InitBinder
 *  org.springframework.web.bind.annotation.ModelAttribute
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.support.DefaultDataBinderFactory
 *  org.springframework.web.bind.support.DefaultSessionAttributeStore
 *  org.springframework.web.bind.support.SessionAttributeStore
 *  org.springframework.web.bind.support.WebBindingInitializer
 *  org.springframework.web.bind.support.WebDataBinderFactory
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.context.request.ServletWebRequest
 *  org.springframework.web.context.request.async.AsyncWebRequest
 *  org.springframework.web.context.request.async.CallableProcessingInterceptor
 *  org.springframework.web.context.request.async.DeferredResultProcessingInterceptor
 *  org.springframework.web.context.request.async.WebAsyncManager
 *  org.springframework.web.context.request.async.WebAsyncUtils
 *  org.springframework.web.method.ControllerAdviceBean
 *  org.springframework.web.method.HandlerMethod
 *  org.springframework.web.method.annotation.ErrorsMethodArgumentResolver
 *  org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver
 *  org.springframework.web.method.annotation.InitBinderDataBinderFactory
 *  org.springframework.web.method.annotation.MapMethodProcessor
 *  org.springframework.web.method.annotation.ModelFactory
 *  org.springframework.web.method.annotation.ModelMethodProcessor
 *  org.springframework.web.method.annotation.RequestHeaderMapMethodArgumentResolver
 *  org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver
 *  org.springframework.web.method.annotation.RequestParamMapMethodArgumentResolver
 *  org.springframework.web.method.annotation.RequestParamMethodArgumentResolver
 *  org.springframework.web.method.annotation.SessionAttributesHandler
 *  org.springframework.web.method.annotation.SessionStatusMethodArgumentResolver
 *  org.springframework.web.method.support.HandlerMethodArgumentResolver
 *  org.springframework.web.method.support.HandlerMethodArgumentResolverComposite
 *  org.springframework.web.method.support.HandlerMethodReturnValueHandler
 *  org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite
 *  org.springframework.web.method.support.InvocableHandlerMethod
 *  org.springframework.web.method.support.ModelAndViewContainer
 *  org.springframework.web.util.WebUtils
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.SpringProperties;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.DefaultDataBinderFactory;
import org.springframework.web.bind.support.DefaultSessionAttributeStore;
import org.springframework.web.bind.support.SessionAttributeStore;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.async.AsyncWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.ControllerAdviceBean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ErrorsMethodArgumentResolver;
import org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver;
import org.springframework.web.method.annotation.InitBinderDataBinderFactory;
import org.springframework.web.method.annotation.MapMethodProcessor;
import org.springframework.web.method.annotation.ModelFactory;
import org.springframework.web.method.annotation.ModelMethodProcessor;
import org.springframework.web.method.annotation.RequestHeaderMapMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMapMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.annotation.SessionAttributesHandler;
import org.springframework.web.method.annotation.SessionStatusMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.annotation.ModelAndViewResolver;
import org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter;
import org.springframework.web.servlet.mvc.method.annotation.AsyncTaskMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.CallableMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.ContinuationHandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.DeferredResultMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.HttpHeadersReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.MatrixVariableMapMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.MatrixVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ModelAndViewMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.ModelAndViewResolverMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMapMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.PrincipalMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RedirectAttributesMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestAttributeMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestPartMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyAdviceChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitterReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.ServletCookieValueMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletResponseMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.SessionAttributeMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBodyReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.UriComponentsBuilderMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ViewMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.ViewNameMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

public class RequestMappingHandlerAdapter
extends AbstractHandlerMethodAdapter
implements BeanFactoryAware,
InitializingBean {
    private static final boolean shouldIgnoreXml = SpringProperties.getFlag((String)"spring.xml.ignore");
    public static final ReflectionUtils.MethodFilter INIT_BINDER_METHODS = method -> AnnotatedElementUtils.hasAnnotation((AnnotatedElement)method, InitBinder.class);
    public static final ReflectionUtils.MethodFilter MODEL_ATTRIBUTE_METHODS = method -> !AnnotatedElementUtils.hasAnnotation((AnnotatedElement)method, RequestMapping.class) && AnnotatedElementUtils.hasAnnotation((AnnotatedElement)method, ModelAttribute.class);
    @Nullable
    private List<HandlerMethodArgumentResolver> customArgumentResolvers;
    @Nullable
    private HandlerMethodArgumentResolverComposite argumentResolvers;
    @Nullable
    private HandlerMethodArgumentResolverComposite initBinderArgumentResolvers;
    @Nullable
    private List<HandlerMethodReturnValueHandler> customReturnValueHandlers;
    @Nullable
    private HandlerMethodReturnValueHandlerComposite returnValueHandlers;
    @Nullable
    private List<ModelAndViewResolver> modelAndViewResolvers;
    private ContentNegotiationManager contentNegotiationManager = new ContentNegotiationManager();
    private List<HttpMessageConverter<?>> messageConverters;
    private final List<Object> requestResponseBodyAdvice = new ArrayList<Object>();
    @Nullable
    private WebBindingInitializer webBindingInitializer;
    private AsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("MvcAsync");
    @Nullable
    private Long asyncRequestTimeout;
    private CallableProcessingInterceptor[] callableInterceptors = new CallableProcessingInterceptor[0];
    private DeferredResultProcessingInterceptor[] deferredResultInterceptors = new DeferredResultProcessingInterceptor[0];
    private ReactiveAdapterRegistry reactiveAdapterRegistry = ReactiveAdapterRegistry.getSharedInstance();
    private boolean ignoreDefaultModelOnRedirect = false;
    private int cacheSecondsForSessionAttributeHandlers = 0;
    private boolean synchronizeOnSession = false;
    private SessionAttributeStore sessionAttributeStore = new DefaultSessionAttributeStore();
    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    @Nullable
    private ConfigurableBeanFactory beanFactory;
    private final Map<Class<?>, SessionAttributesHandler> sessionAttributesHandlerCache = new ConcurrentHashMap(64);
    private final Map<Class<?>, Set<Method>> initBinderCache = new ConcurrentHashMap(64);
    private final Map<ControllerAdviceBean, Set<Method>> initBinderAdviceCache = new LinkedHashMap<ControllerAdviceBean, Set<Method>>();
    private final Map<Class<?>, Set<Method>> modelAttributeCache = new ConcurrentHashMap(64);
    private final Map<ControllerAdviceBean, Set<Method>> modelAttributeAdviceCache = new LinkedHashMap<ControllerAdviceBean, Set<Method>>();

    public RequestMappingHandlerAdapter() {
        this.messageConverters = new ArrayList(4);
        this.messageConverters.add((HttpMessageConverter<?>)new ByteArrayHttpMessageConverter());
        this.messageConverters.add((HttpMessageConverter<?>)new StringHttpMessageConverter());
        if (!shouldIgnoreXml) {
            try {
                this.messageConverters.add((HttpMessageConverter<?>)new SourceHttpMessageConverter());
            }
            catch (Error error) {
                // empty catch block
            }
        }
        this.messageConverters.add((HttpMessageConverter<?>)new AllEncompassingFormHttpMessageConverter());
    }

    public void setCustomArgumentResolvers(@Nullable List<HandlerMethodArgumentResolver> argumentResolvers) {
        this.customArgumentResolvers = argumentResolvers;
    }

    @Nullable
    public List<HandlerMethodArgumentResolver> getCustomArgumentResolvers() {
        return this.customArgumentResolvers;
    }

    public void setArgumentResolvers(@Nullable List<HandlerMethodArgumentResolver> argumentResolvers) {
        if (argumentResolvers == null) {
            this.argumentResolvers = null;
        } else {
            this.argumentResolvers = new HandlerMethodArgumentResolverComposite();
            this.argumentResolvers.addResolvers(argumentResolvers);
        }
    }

    @Nullable
    public List<HandlerMethodArgumentResolver> getArgumentResolvers() {
        return this.argumentResolvers != null ? this.argumentResolvers.getResolvers() : null;
    }

    public void setInitBinderArgumentResolvers(@Nullable List<HandlerMethodArgumentResolver> argumentResolvers) {
        if (argumentResolvers == null) {
            this.initBinderArgumentResolvers = null;
        } else {
            this.initBinderArgumentResolvers = new HandlerMethodArgumentResolverComposite();
            this.initBinderArgumentResolvers.addResolvers(argumentResolvers);
        }
    }

    @Nullable
    public List<HandlerMethodArgumentResolver> getInitBinderArgumentResolvers() {
        return this.initBinderArgumentResolvers != null ? this.initBinderArgumentResolvers.getResolvers() : null;
    }

    public void setCustomReturnValueHandlers(@Nullable List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        this.customReturnValueHandlers = returnValueHandlers;
    }

    @Nullable
    public List<HandlerMethodReturnValueHandler> getCustomReturnValueHandlers() {
        return this.customReturnValueHandlers;
    }

    public void setReturnValueHandlers(@Nullable List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        if (returnValueHandlers == null) {
            this.returnValueHandlers = null;
        } else {
            this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite();
            this.returnValueHandlers.addHandlers(returnValueHandlers);
        }
    }

    @Nullable
    public List<HandlerMethodReturnValueHandler> getReturnValueHandlers() {
        return this.returnValueHandlers != null ? this.returnValueHandlers.getHandlers() : null;
    }

    public void setModelAndViewResolvers(@Nullable List<ModelAndViewResolver> modelAndViewResolvers) {
        this.modelAndViewResolvers = modelAndViewResolvers;
    }

    @Nullable
    public List<ModelAndViewResolver> getModelAndViewResolvers() {
        return this.modelAndViewResolvers;
    }

    public void setContentNegotiationManager(ContentNegotiationManager contentNegotiationManager) {
        this.contentNegotiationManager = contentNegotiationManager;
    }

    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = messageConverters;
    }

    public List<HttpMessageConverter<?>> getMessageConverters() {
        return this.messageConverters;
    }

    public void setRequestBodyAdvice(@Nullable List<RequestBodyAdvice> requestBodyAdvice) {
        if (requestBodyAdvice != null) {
            this.requestResponseBodyAdvice.addAll(requestBodyAdvice);
        }
    }

    public void setResponseBodyAdvice(@Nullable List<ResponseBodyAdvice<?>> responseBodyAdvice) {
        if (responseBodyAdvice != null) {
            this.requestResponseBodyAdvice.addAll(responseBodyAdvice);
        }
    }

    public void setWebBindingInitializer(@Nullable WebBindingInitializer webBindingInitializer) {
        this.webBindingInitializer = webBindingInitializer;
    }

    @Nullable
    public WebBindingInitializer getWebBindingInitializer() {
        return this.webBindingInitializer;
    }

    public void setTaskExecutor(AsyncTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void setAsyncRequestTimeout(long timeout) {
        this.asyncRequestTimeout = timeout;
    }

    public void setCallableInterceptors(List<CallableProcessingInterceptor> interceptors) {
        this.callableInterceptors = interceptors.toArray(new CallableProcessingInterceptor[0]);
    }

    public void setDeferredResultInterceptors(List<DeferredResultProcessingInterceptor> interceptors) {
        this.deferredResultInterceptors = interceptors.toArray(new DeferredResultProcessingInterceptor[0]);
    }

    public void setReactiveAdapterRegistry(ReactiveAdapterRegistry reactiveAdapterRegistry) {
        this.reactiveAdapterRegistry = reactiveAdapterRegistry;
    }

    public ReactiveAdapterRegistry getReactiveAdapterRegistry() {
        return this.reactiveAdapterRegistry;
    }

    public void setIgnoreDefaultModelOnRedirect(boolean ignoreDefaultModelOnRedirect) {
        this.ignoreDefaultModelOnRedirect = ignoreDefaultModelOnRedirect;
    }

    public void setSessionAttributeStore(SessionAttributeStore sessionAttributeStore) {
        this.sessionAttributeStore = sessionAttributeStore;
    }

    public void setCacheSecondsForSessionAttributeHandlers(int cacheSecondsForSessionAttributeHandlers) {
        this.cacheSecondsForSessionAttributeHandlers = cacheSecondsForSessionAttributeHandlers;
    }

    public void setSynchronizeOnSession(boolean synchronizeOnSession) {
        this.synchronizeOnSession = synchronizeOnSession;
    }

    public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof ConfigurableBeanFactory) {
            this.beanFactory = (ConfigurableBeanFactory)beanFactory;
        }
    }

    @Nullable
    protected ConfigurableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public void afterPropertiesSet() {
        List<HandlerMethodArgumentResolver> resolvers;
        this.initControllerAdviceCache();
        if (this.argumentResolvers == null) {
            resolvers = this.getDefaultArgumentResolvers();
            this.argumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
        }
        if (this.initBinderArgumentResolvers == null) {
            resolvers = this.getDefaultInitBinderArgumentResolvers();
            this.initBinderArgumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
        }
        if (this.returnValueHandlers == null) {
            List<HandlerMethodReturnValueHandler> handlers = this.getDefaultReturnValueHandlers();
            this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite().addHandlers(handlers);
        }
    }

    private void initControllerAdviceCache() {
        if (this.getApplicationContext() == null) {
            return;
        }
        List adviceBeans = ControllerAdviceBean.findAnnotatedBeans((ApplicationContext)this.getApplicationContext());
        ArrayList<ControllerAdviceBean> requestResponseBodyAdviceBeans = new ArrayList<ControllerAdviceBean>();
        for (ControllerAdviceBean adviceBean : adviceBeans) {
            Set binderMethods;
            Class beanType = adviceBean.getBeanType();
            if (beanType == null) {
                throw new IllegalStateException("Unresolvable type for ControllerAdviceBean: " + adviceBean);
            }
            Set attrMethods = MethodIntrospector.selectMethods((Class)beanType, (ReflectionUtils.MethodFilter)MODEL_ATTRIBUTE_METHODS);
            if (!attrMethods.isEmpty()) {
                this.modelAttributeAdviceCache.put(adviceBean, attrMethods);
            }
            if (!(binderMethods = MethodIntrospector.selectMethods((Class)beanType, (ReflectionUtils.MethodFilter)INIT_BINDER_METHODS)).isEmpty()) {
                this.initBinderAdviceCache.put(adviceBean, binderMethods);
            }
            if (!RequestBodyAdvice.class.isAssignableFrom(beanType) && !ResponseBodyAdvice.class.isAssignableFrom(beanType)) continue;
            requestResponseBodyAdviceBeans.add(adviceBean);
        }
        if (!requestResponseBodyAdviceBeans.isEmpty()) {
            this.requestResponseBodyAdvice.addAll(0, requestResponseBodyAdviceBeans);
        }
        if (this.logger.isDebugEnabled()) {
            int modelSize = this.modelAttributeAdviceCache.size();
            int binderSize = this.initBinderAdviceCache.size();
            int reqCount = this.getBodyAdviceCount(RequestBodyAdvice.class);
            int resCount = this.getBodyAdviceCount(ResponseBodyAdvice.class);
            if (modelSize == 0 && binderSize == 0 && reqCount == 0 && resCount == 0) {
                this.logger.debug((Object)"ControllerAdvice beans: none");
            } else {
                this.logger.debug((Object)("ControllerAdvice beans: " + modelSize + " @ModelAttribute, " + binderSize + " @InitBinder, " + reqCount + " RequestBodyAdvice, " + resCount + " ResponseBodyAdvice"));
            }
        }
    }

    private int getBodyAdviceCount(Class<?> adviceType) {
        List<Object> advice = this.requestResponseBodyAdvice;
        return RequestBodyAdvice.class.isAssignableFrom(adviceType) ? RequestResponseBodyAdviceChain.getAdviceByType(advice, RequestBodyAdvice.class).size() : RequestResponseBodyAdviceChain.getAdviceByType(advice, ResponseBodyAdvice.class).size();
    }

    private List<HandlerMethodArgumentResolver> getDefaultArgumentResolvers() {
        ArrayList<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>(30);
        resolvers.add((HandlerMethodArgumentResolver)new RequestParamMethodArgumentResolver(this.getBeanFactory(), false));
        resolvers.add((HandlerMethodArgumentResolver)new RequestParamMapMethodArgumentResolver());
        resolvers.add((HandlerMethodArgumentResolver)new PathVariableMethodArgumentResolver());
        resolvers.add(new PathVariableMapMethodArgumentResolver());
        resolvers.add((HandlerMethodArgumentResolver)new MatrixVariableMethodArgumentResolver());
        resolvers.add(new MatrixVariableMapMethodArgumentResolver());
        resolvers.add((HandlerMethodArgumentResolver)new ServletModelAttributeMethodProcessor(false));
        resolvers.add(new RequestResponseBodyMethodProcessor(this.getMessageConverters(), this.requestResponseBodyAdvice));
        resolvers.add(new RequestPartMethodArgumentResolver(this.getMessageConverters(), this.requestResponseBodyAdvice));
        resolvers.add((HandlerMethodArgumentResolver)new RequestHeaderMethodArgumentResolver(this.getBeanFactory()));
        resolvers.add((HandlerMethodArgumentResolver)new RequestHeaderMapMethodArgumentResolver());
        resolvers.add((HandlerMethodArgumentResolver)new ServletCookieValueMethodArgumentResolver(this.getBeanFactory()));
        resolvers.add((HandlerMethodArgumentResolver)new ExpressionValueMethodArgumentResolver(this.getBeanFactory()));
        resolvers.add((HandlerMethodArgumentResolver)new SessionAttributeMethodArgumentResolver());
        resolvers.add((HandlerMethodArgumentResolver)new RequestAttributeMethodArgumentResolver());
        resolvers.add(new ServletRequestMethodArgumentResolver());
        resolvers.add(new ServletResponseMethodArgumentResolver());
        resolvers.add(new HttpEntityMethodProcessor(this.getMessageConverters(), this.requestResponseBodyAdvice));
        resolvers.add(new RedirectAttributesMethodArgumentResolver());
        resolvers.add((HandlerMethodArgumentResolver)new ModelMethodProcessor());
        resolvers.add((HandlerMethodArgumentResolver)new MapMethodProcessor());
        resolvers.add((HandlerMethodArgumentResolver)new ErrorsMethodArgumentResolver());
        resolvers.add((HandlerMethodArgumentResolver)new SessionStatusMethodArgumentResolver());
        resolvers.add(new UriComponentsBuilderMethodArgumentResolver());
        if (KotlinDetector.isKotlinPresent()) {
            resolvers.add(new ContinuationHandlerMethodArgumentResolver());
        }
        if (this.getCustomArgumentResolvers() != null) {
            resolvers.addAll(this.getCustomArgumentResolvers());
        }
        resolvers.add(new PrincipalMethodArgumentResolver());
        resolvers.add((HandlerMethodArgumentResolver)new RequestParamMethodArgumentResolver(this.getBeanFactory(), true));
        resolvers.add((HandlerMethodArgumentResolver)new ServletModelAttributeMethodProcessor(true));
        return resolvers;
    }

    private List<HandlerMethodArgumentResolver> getDefaultInitBinderArgumentResolvers() {
        ArrayList<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>(20);
        resolvers.add((HandlerMethodArgumentResolver)new RequestParamMethodArgumentResolver(this.getBeanFactory(), false));
        resolvers.add((HandlerMethodArgumentResolver)new RequestParamMapMethodArgumentResolver());
        resolvers.add((HandlerMethodArgumentResolver)new PathVariableMethodArgumentResolver());
        resolvers.add(new PathVariableMapMethodArgumentResolver());
        resolvers.add((HandlerMethodArgumentResolver)new MatrixVariableMethodArgumentResolver());
        resolvers.add(new MatrixVariableMapMethodArgumentResolver());
        resolvers.add((HandlerMethodArgumentResolver)new ExpressionValueMethodArgumentResolver(this.getBeanFactory()));
        resolvers.add((HandlerMethodArgumentResolver)new SessionAttributeMethodArgumentResolver());
        resolvers.add((HandlerMethodArgumentResolver)new RequestAttributeMethodArgumentResolver());
        resolvers.add(new ServletRequestMethodArgumentResolver());
        resolvers.add(new ServletResponseMethodArgumentResolver());
        if (this.getCustomArgumentResolvers() != null) {
            resolvers.addAll(this.getCustomArgumentResolvers());
        }
        resolvers.add(new PrincipalMethodArgumentResolver());
        resolvers.add((HandlerMethodArgumentResolver)new RequestParamMethodArgumentResolver(this.getBeanFactory(), true));
        return resolvers;
    }

    private List<HandlerMethodReturnValueHandler> getDefaultReturnValueHandlers() {
        ArrayList<HandlerMethodReturnValueHandler> handlers = new ArrayList<HandlerMethodReturnValueHandler>(20);
        handlers.add(new ModelAndViewMethodReturnValueHandler());
        handlers.add((HandlerMethodReturnValueHandler)new ModelMethodProcessor());
        handlers.add(new ViewMethodReturnValueHandler());
        handlers.add(new ResponseBodyEmitterReturnValueHandler(this.getMessageConverters(), this.reactiveAdapterRegistry, (TaskExecutor)this.taskExecutor, this.contentNegotiationManager));
        handlers.add(new StreamingResponseBodyReturnValueHandler());
        handlers.add(new HttpEntityMethodProcessor(this.getMessageConverters(), this.contentNegotiationManager, this.requestResponseBodyAdvice));
        handlers.add(new HttpHeadersReturnValueHandler());
        handlers.add(new CallableMethodReturnValueHandler());
        handlers.add(new DeferredResultMethodReturnValueHandler());
        handlers.add(new AsyncTaskMethodReturnValueHandler((BeanFactory)this.beanFactory));
        handlers.add((HandlerMethodReturnValueHandler)new ServletModelAttributeMethodProcessor(false));
        handlers.add(new RequestResponseBodyMethodProcessor(this.getMessageConverters(), this.contentNegotiationManager, this.requestResponseBodyAdvice));
        handlers.add(new ViewNameMethodReturnValueHandler());
        handlers.add((HandlerMethodReturnValueHandler)new MapMethodProcessor());
        if (this.getCustomReturnValueHandlers() != null) {
            handlers.addAll(this.getCustomReturnValueHandlers());
        }
        if (!CollectionUtils.isEmpty(this.getModelAndViewResolvers())) {
            handlers.add(new ModelAndViewResolverMethodReturnValueHandler(this.getModelAndViewResolvers()));
        } else {
            handlers.add((HandlerMethodReturnValueHandler)new ServletModelAttributeMethodProcessor(true));
        }
        return handlers;
    }

    @Override
    protected boolean supportsInternal(HandlerMethod handlerMethod) {
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected ModelAndView handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        ModelAndView mav;
        this.checkRequest(request);
        if (this.synchronizeOnSession) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object mutex;
                Object object = mutex = WebUtils.getSessionMutex((HttpSession)session);
                synchronized (object) {
                    mav = this.invokeHandlerMethod(request, response, handlerMethod);
                }
            } else {
                mav = this.invokeHandlerMethod(request, response, handlerMethod);
            }
        } else {
            mav = this.invokeHandlerMethod(request, response, handlerMethod);
        }
        if (!response.containsHeader("Cache-Control")) {
            if (this.getSessionAttributesHandler(handlerMethod).hasSessionAttributes()) {
                this.applyCacheSeconds(response, this.cacheSecondsForSessionAttributeHandlers);
            } else {
                this.prepareResponse(response);
            }
        }
        return mav;
    }

    @Override
    protected long getLastModifiedInternal(HttpServletRequest request, HandlerMethod handlerMethod) {
        return -1L;
    }

    private SessionAttributesHandler getSessionAttributesHandler(HandlerMethod handlerMethod) {
        return this.sessionAttributesHandlerCache.computeIfAbsent(handlerMethod.getBeanType(), type -> new SessionAttributesHandler(type, this.sessionAttributeStore));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    protected ModelAndView invokeHandlerMethod(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        ServletWebRequest webRequest = new ServletWebRequest(request, response);
        try {
            ModelAndView modelAndView;
            WebDataBinderFactory binderFactory = this.getDataBinderFactory(handlerMethod);
            ModelFactory modelFactory = this.getModelFactory(handlerMethod, binderFactory);
            ServletInvocableHandlerMethod invocableMethod = this.createInvocableHandlerMethod(handlerMethod);
            if (this.argumentResolvers != null) {
                invocableMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
            }
            if (this.returnValueHandlers != null) {
                invocableMethod.setHandlerMethodReturnValueHandlers(this.returnValueHandlers);
            }
            invocableMethod.setDataBinderFactory(binderFactory);
            invocableMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);
            ModelAndViewContainer mavContainer = new ModelAndViewContainer();
            mavContainer.addAllAttributes(RequestContextUtils.getInputFlashMap(request));
            modelFactory.initModel((NativeWebRequest)webRequest, mavContainer, (HandlerMethod)invocableMethod);
            mavContainer.setIgnoreDefaultModelOnRedirect(this.ignoreDefaultModelOnRedirect);
            AsyncWebRequest asyncWebRequest = WebAsyncUtils.createAsyncWebRequest((HttpServletRequest)request, (HttpServletResponse)response);
            asyncWebRequest.setTimeout(this.asyncRequestTimeout);
            WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager((ServletRequest)request);
            asyncManager.setTaskExecutor(this.taskExecutor);
            asyncManager.setAsyncWebRequest(asyncWebRequest);
            asyncManager.registerCallableInterceptors(this.callableInterceptors);
            asyncManager.registerDeferredResultInterceptors(this.deferredResultInterceptors);
            if (asyncManager.hasConcurrentResult()) {
                Object result = asyncManager.getConcurrentResult();
                mavContainer = (ModelAndViewContainer)asyncManager.getConcurrentResultContext()[0];
                asyncManager.clearConcurrentResult();
                LogFormatUtils.traceDebug((Log)this.logger, traceOn -> {
                    String formatted = LogFormatUtils.formatValue((Object)result, (traceOn == false ? 1 : 0) != 0);
                    return "Resume with async result [" + formatted + "]";
                });
                invocableMethod = invocableMethod.wrapConcurrentResult(result);
            }
            invocableMethod.invokeAndHandle(webRequest, mavContainer, new Object[0]);
            if (asyncManager.isConcurrentHandlingStarted()) {
                modelAndView = null;
                return modelAndView;
            }
            modelAndView = this.getModelAndView(mavContainer, modelFactory, (NativeWebRequest)webRequest);
            return modelAndView;
        }
        finally {
            webRequest.requestCompleted();
        }
    }

    protected ServletInvocableHandlerMethod createInvocableHandlerMethod(HandlerMethod handlerMethod) {
        return new ServletInvocableHandlerMethod(handlerMethod);
    }

    private ModelFactory getModelFactory(HandlerMethod handlerMethod, WebDataBinderFactory binderFactory) {
        SessionAttributesHandler sessionAttrHandler = this.getSessionAttributesHandler(handlerMethod);
        Class handlerType = handlerMethod.getBeanType();
        Set methods = this.modelAttributeCache.get(handlerType);
        if (methods == null) {
            methods = MethodIntrospector.selectMethods((Class)handlerType, (ReflectionUtils.MethodFilter)MODEL_ATTRIBUTE_METHODS);
            this.modelAttributeCache.put(handlerType, methods);
        }
        ArrayList<InvocableHandlerMethod> attrMethods = new ArrayList<InvocableHandlerMethod>();
        this.modelAttributeAdviceCache.forEach((controllerAdviceBean, methodSet) -> {
            if (controllerAdviceBean.isApplicableToBeanType(handlerType)) {
                Object bean = controllerAdviceBean.resolveBean();
                for (Method method : methodSet) {
                    attrMethods.add(this.createModelAttributeMethod(binderFactory, bean, method));
                }
            }
        });
        for (Method method : methods) {
            Object bean = handlerMethod.getBean();
            attrMethods.add(this.createModelAttributeMethod(binderFactory, bean, method));
        }
        return new ModelFactory(attrMethods, binderFactory, sessionAttrHandler);
    }

    private InvocableHandlerMethod createModelAttributeMethod(WebDataBinderFactory factory, Object bean, Method method) {
        InvocableHandlerMethod attrMethod = new InvocableHandlerMethod(bean, method);
        if (this.argumentResolvers != null) {
            attrMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
        }
        attrMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);
        attrMethod.setDataBinderFactory(factory);
        return attrMethod;
    }

    private WebDataBinderFactory getDataBinderFactory(HandlerMethod handlerMethod) throws Exception {
        Class handlerType = handlerMethod.getBeanType();
        Set methods = this.initBinderCache.get(handlerType);
        if (methods == null) {
            methods = MethodIntrospector.selectMethods((Class)handlerType, (ReflectionUtils.MethodFilter)INIT_BINDER_METHODS);
            this.initBinderCache.put(handlerType, methods);
        }
        ArrayList<InvocableHandlerMethod> initBinderMethods = new ArrayList<InvocableHandlerMethod>();
        this.initBinderAdviceCache.forEach((controllerAdviceBean, methodSet) -> {
            if (controllerAdviceBean.isApplicableToBeanType(handlerType)) {
                Object bean = controllerAdviceBean.resolveBean();
                for (Method method : methodSet) {
                    initBinderMethods.add(this.createInitBinderMethod(bean, method));
                }
            }
        });
        for (Method method : methods) {
            Object bean = handlerMethod.getBean();
            initBinderMethods.add(this.createInitBinderMethod(bean, method));
        }
        return this.createDataBinderFactory(initBinderMethods);
    }

    private InvocableHandlerMethod createInitBinderMethod(Object bean, Method method) {
        InvocableHandlerMethod binderMethod = new InvocableHandlerMethod(bean, method);
        if (this.initBinderArgumentResolvers != null) {
            binderMethod.setHandlerMethodArgumentResolvers(this.initBinderArgumentResolvers);
        }
        binderMethod.setDataBinderFactory((WebDataBinderFactory)new DefaultDataBinderFactory(this.webBindingInitializer));
        binderMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);
        return binderMethod;
    }

    protected InitBinderDataBinderFactory createDataBinderFactory(List<InvocableHandlerMethod> binderMethods) throws Exception {
        return new ServletRequestDataBinderFactory(binderMethods, this.getWebBindingInitializer());
    }

    @Nullable
    private ModelAndView getModelAndView(ModelAndViewContainer mavContainer, ModelFactory modelFactory, NativeWebRequest webRequest) throws Exception {
        modelFactory.updateModel(webRequest, mavContainer);
        if (mavContainer.isRequestHandled()) {
            return null;
        }
        ModelMap model = mavContainer.getModel();
        ModelAndView mav = new ModelAndView(mavContainer.getViewName(), (Map<String, ?>)model, mavContainer.getStatus());
        if (!mavContainer.isViewReference()) {
            mav.setView((View)mavContainer.getView());
        }
        if (model instanceof RedirectAttributes) {
            Map<String, ?> flashAttributes = ((RedirectAttributes)model).getFlashAttributes();
            HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest(HttpServletRequest.class);
            if (request != null) {
                RequestContextUtils.getOutputFlashMap(request).putAll(flashAttributes);
            }
        }
        return mav;
    }
}

