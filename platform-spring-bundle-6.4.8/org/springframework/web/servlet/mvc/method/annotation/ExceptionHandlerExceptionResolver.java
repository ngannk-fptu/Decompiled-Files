/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.SpringProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.ui.ModelMap;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.ControllerAdviceBean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.method.annotation.MapMethodProcessor;
import org.springframework.web.method.annotation.ModelMethodProcessor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.AbstractHandlerMethodExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ModelAndViewMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.PrincipalMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RedirectAttributesMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestAttributeMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletResponseMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.SessionAttributeMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ViewMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.ViewNameMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

public class ExceptionHandlerExceptionResolver
extends AbstractHandlerMethodExceptionResolver
implements ApplicationContextAware,
InitializingBean {
    private static final boolean shouldIgnoreXml = SpringProperties.getFlag("spring.xml.ignore");
    @Nullable
    private List<HandlerMethodArgumentResolver> customArgumentResolvers;
    @Nullable
    private HandlerMethodArgumentResolverComposite argumentResolvers;
    @Nullable
    private List<HandlerMethodReturnValueHandler> customReturnValueHandlers;
    @Nullable
    private HandlerMethodReturnValueHandlerComposite returnValueHandlers;
    private List<HttpMessageConverter<?>> messageConverters;
    private ContentNegotiationManager contentNegotiationManager = new ContentNegotiationManager();
    private final List<Object> responseBodyAdvice = new ArrayList<Object>();
    @Nullable
    private ApplicationContext applicationContext;
    private final Map<Class<?>, ExceptionHandlerMethodResolver> exceptionHandlerCache = new ConcurrentHashMap(64);
    private final Map<ControllerAdviceBean, ExceptionHandlerMethodResolver> exceptionHandlerAdviceCache = new LinkedHashMap<ControllerAdviceBean, ExceptionHandlerMethodResolver>();

    public ExceptionHandlerExceptionResolver() {
        this.messageConverters = new ArrayList();
        this.messageConverters.add(new ByteArrayHttpMessageConverter());
        this.messageConverters.add(new StringHttpMessageConverter());
        if (!shouldIgnoreXml) {
            try {
                this.messageConverters.add(new SourceHttpMessageConverter());
            }
            catch (Error error) {
                // empty catch block
            }
        }
        this.messageConverters.add(new AllEncompassingFormHttpMessageConverter());
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
    public HandlerMethodArgumentResolverComposite getArgumentResolvers() {
        return this.argumentResolvers;
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
    public HandlerMethodReturnValueHandlerComposite getReturnValueHandlers() {
        return this.returnValueHandlers;
    }

    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = messageConverters;
    }

    public List<HttpMessageConverter<?>> getMessageConverters() {
        return this.messageConverters;
    }

    public void setContentNegotiationManager(ContentNegotiationManager contentNegotiationManager) {
        this.contentNegotiationManager = contentNegotiationManager;
    }

    public ContentNegotiationManager getContentNegotiationManager() {
        return this.contentNegotiationManager;
    }

    public void setResponseBodyAdvice(@Nullable List<ResponseBodyAdvice<?>> responseBodyAdvice) {
        if (responseBodyAdvice != null) {
            this.responseBodyAdvice.addAll(responseBodyAdvice);
        }
    }

    @Override
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Nullable
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        this.initExceptionHandlerAdviceCache();
        if (this.argumentResolvers == null) {
            List<HandlerMethodArgumentResolver> resolvers = this.getDefaultArgumentResolvers();
            this.argumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
        }
        if (this.returnValueHandlers == null) {
            List<HandlerMethodReturnValueHandler> handlers = this.getDefaultReturnValueHandlers();
            this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite().addHandlers(handlers);
        }
    }

    private void initExceptionHandlerAdviceCache() {
        if (this.getApplicationContext() == null) {
            return;
        }
        List<ControllerAdviceBean> adviceBeans = ControllerAdviceBean.findAnnotatedBeans(this.getApplicationContext());
        for (ControllerAdviceBean adviceBean : adviceBeans) {
            Class<?> beanType = adviceBean.getBeanType();
            if (beanType == null) {
                throw new IllegalStateException("Unresolvable type for ControllerAdviceBean: " + adviceBean);
            }
            ExceptionHandlerMethodResolver resolver = new ExceptionHandlerMethodResolver(beanType);
            if (resolver.hasExceptionMappings()) {
                this.exceptionHandlerAdviceCache.put(adviceBean, resolver);
            }
            if (!ResponseBodyAdvice.class.isAssignableFrom(beanType)) continue;
            this.responseBodyAdvice.add(adviceBean);
        }
        if (this.logger.isDebugEnabled()) {
            int handlerSize = this.exceptionHandlerAdviceCache.size();
            int adviceSize = this.responseBodyAdvice.size();
            if (handlerSize == 0 && adviceSize == 0) {
                this.logger.debug((Object)"ControllerAdvice beans: none");
            } else {
                this.logger.debug((Object)("ControllerAdvice beans: " + handlerSize + " @ExceptionHandler, " + adviceSize + " ResponseBodyAdvice"));
            }
        }
    }

    public Map<ControllerAdviceBean, ExceptionHandlerMethodResolver> getExceptionHandlerAdviceCache() {
        return Collections.unmodifiableMap(this.exceptionHandlerAdviceCache);
    }

    protected List<HandlerMethodArgumentResolver> getDefaultArgumentResolvers() {
        ArrayList<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>();
        resolvers.add(new SessionAttributeMethodArgumentResolver());
        resolvers.add(new RequestAttributeMethodArgumentResolver());
        resolvers.add(new ServletRequestMethodArgumentResolver());
        resolvers.add(new ServletResponseMethodArgumentResolver());
        resolvers.add(new RedirectAttributesMethodArgumentResolver());
        resolvers.add(new ModelMethodProcessor());
        if (this.getCustomArgumentResolvers() != null) {
            resolvers.addAll(this.getCustomArgumentResolvers());
        }
        resolvers.add(new PrincipalMethodArgumentResolver());
        return resolvers;
    }

    protected List<HandlerMethodReturnValueHandler> getDefaultReturnValueHandlers() {
        ArrayList<HandlerMethodReturnValueHandler> handlers = new ArrayList<HandlerMethodReturnValueHandler>();
        handlers.add(new ModelAndViewMethodReturnValueHandler());
        handlers.add(new ModelMethodProcessor());
        handlers.add(new ViewMethodReturnValueHandler());
        handlers.add(new HttpEntityMethodProcessor(this.getMessageConverters(), this.contentNegotiationManager, this.responseBodyAdvice));
        handlers.add(new ServletModelAttributeMethodProcessor(false));
        handlers.add(new RequestResponseBodyMethodProcessor(this.getMessageConverters(), this.contentNegotiationManager, this.responseBodyAdvice));
        handlers.add(new ViewNameMethodReturnValueHandler());
        handlers.add(new MapMethodProcessor());
        if (this.getCustomReturnValueHandlers() != null) {
            handlers.addAll(this.getCustomReturnValueHandlers());
        }
        handlers.add(new ServletModelAttributeMethodProcessor(true));
        return handlers;
    }

    @Override
    protected boolean hasGlobalExceptionHandlers() {
        return !this.exceptionHandlerAdviceCache.isEmpty();
    }

    @Override
    @Nullable
    protected ModelAndView doResolveHandlerMethodException(HttpServletRequest request, HttpServletResponse response, @Nullable HandlerMethod handlerMethod, Exception exception) {
        ServletInvocableHandlerMethod exceptionHandlerMethod = this.getExceptionHandlerMethod(handlerMethod, exception);
        if (exceptionHandlerMethod == null) {
            return null;
        }
        if (this.argumentResolvers != null) {
            exceptionHandlerMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
        }
        if (this.returnValueHandlers != null) {
            exceptionHandlerMethod.setHandlerMethodReturnValueHandlers(this.returnValueHandlers);
        }
        ServletWebRequest webRequest = new ServletWebRequest(request, response);
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        ArrayList<Exception> exceptions = new ArrayList<Exception>();
        try {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Using @ExceptionHandler " + exceptionHandlerMethod));
            }
            Throwable exToExpose = exception;
            while (exToExpose != null) {
                exceptions.add((Exception)exToExpose);
                Throwable cause = exToExpose.getCause();
                exToExpose = cause != exToExpose ? cause : null;
            }
            Object[] arguments = new Object[exceptions.size() + 1];
            exceptions.toArray(arguments);
            arguments[arguments.length - 1] = handlerMethod;
            exceptionHandlerMethod.invokeAndHandle(webRequest, mavContainer, arguments);
        }
        catch (Throwable invocationEx) {
            if (!exceptions.contains(invocationEx) && this.logger.isWarnEnabled()) {
                this.logger.warn((Object)("Failure in @ExceptionHandler " + exceptionHandlerMethod), invocationEx);
            }
            return null;
        }
        if (mavContainer.isRequestHandled()) {
            return new ModelAndView();
        }
        ModelMap model = mavContainer.getModel();
        HttpStatus status = mavContainer.getStatus();
        ModelAndView mav = new ModelAndView(mavContainer.getViewName(), model, status);
        mav.setViewName(mavContainer.getViewName());
        if (!mavContainer.isViewReference()) {
            mav.setView((View)mavContainer.getView());
        }
        if (model instanceof RedirectAttributes) {
            Map<String, ?> flashAttributes = ((RedirectAttributes)((Object)model)).getFlashAttributes();
            RequestContextUtils.getOutputFlashMap(request).putAll(flashAttributes);
        }
        return mav;
    }

    @Nullable
    protected ServletInvocableHandlerMethod getExceptionHandlerMethod(@Nullable HandlerMethod handlerMethod, Exception exception) {
        Class<?> handlerType = null;
        if (handlerMethod != null) {
            Method method;
            handlerType = handlerMethod.getBeanType();
            ExceptionHandlerMethodResolver resolver = this.exceptionHandlerCache.get(handlerType);
            if (resolver == null) {
                resolver = new ExceptionHandlerMethodResolver(handlerType);
                this.exceptionHandlerCache.put(handlerType, resolver);
            }
            if ((method = resolver.resolveMethod(exception)) != null) {
                return new ServletInvocableHandlerMethod(handlerMethod.getBean(), method, this.applicationContext);
            }
            if (Proxy.isProxyClass(handlerType)) {
                handlerType = AopUtils.getTargetClass(handlerMethod.getBean());
            }
        }
        for (Map.Entry<ControllerAdviceBean, ExceptionHandlerMethodResolver> entry : this.exceptionHandlerAdviceCache.entrySet()) {
            ExceptionHandlerMethodResolver resolver;
            Method method;
            ControllerAdviceBean advice = entry.getKey();
            if (!advice.isApplicableToBeanType(handlerType) || (method = (resolver = entry.getValue()).resolveMethod(exception)) == null) continue;
            return new ServletInvocableHandlerMethod(advice.resolveBean(), method, this.applicationContext);
        }
        return null;
    }
}

