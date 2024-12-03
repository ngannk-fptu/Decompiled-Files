/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.method.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.springframework.context.MessageSource;
import org.springframework.core.CoroutinesUtils;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.ModelAndViewContainer;

public class InvocableHandlerMethod
extends HandlerMethod {
    private static final Object[] EMPTY_ARGS = new Object[0];
    private HandlerMethodArgumentResolverComposite resolvers = new HandlerMethodArgumentResolverComposite();
    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    @Nullable
    private WebDataBinderFactory dataBinderFactory;

    public InvocableHandlerMethod(HandlerMethod handlerMethod) {
        super(handlerMethod);
    }

    public InvocableHandlerMethod(Object bean2, Method method) {
        super(bean2, method);
    }

    protected InvocableHandlerMethod(Object bean2, Method method, @Nullable MessageSource messageSource) {
        super(bean2, method, messageSource);
    }

    public InvocableHandlerMethod(Object bean2, String methodName, Class<?> ... parameterTypes) throws NoSuchMethodException {
        super(bean2, methodName, parameterTypes);
    }

    public void setHandlerMethodArgumentResolvers(HandlerMethodArgumentResolverComposite argumentResolvers) {
        this.resolvers = argumentResolvers;
    }

    public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    public void setDataBinderFactory(WebDataBinderFactory dataBinderFactory) {
        this.dataBinderFactory = dataBinderFactory;
    }

    @Nullable
    public Object invokeForRequest(NativeWebRequest request, @Nullable ModelAndViewContainer mavContainer, Object ... providedArgs) throws Exception {
        Object[] args = this.getMethodArgumentValues(request, mavContainer, providedArgs);
        if (logger.isTraceEnabled()) {
            logger.trace((Object)("Arguments: " + Arrays.toString(args)));
        }
        return this.doInvoke(args);
    }

    protected Object[] getMethodArgumentValues(NativeWebRequest request, @Nullable ModelAndViewContainer mavContainer, Object ... providedArgs) throws Exception {
        Object[] parameters = this.getMethodParameters();
        if (ObjectUtils.isEmpty(parameters)) {
            return EMPTY_ARGS;
        }
        Object[] args = new Object[parameters.length];
        for (int i2 = 0; i2 < parameters.length; ++i2) {
            Object parameter = parameters[i2];
            ((MethodParameter)parameter).initParameterNameDiscovery(this.parameterNameDiscoverer);
            args[i2] = InvocableHandlerMethod.findProvidedArgument((MethodParameter)parameter, providedArgs);
            if (args[i2] != null) continue;
            if (!this.resolvers.supportsParameter((MethodParameter)parameter)) {
                throw new IllegalStateException(InvocableHandlerMethod.formatArgumentError((MethodParameter)parameter, "No suitable resolver"));
            }
            try {
                args[i2] = this.resolvers.resolveArgument((MethodParameter)parameter, mavContainer, request, this.dataBinderFactory);
                continue;
            }
            catch (Exception ex) {
                String exMsg;
                if (logger.isDebugEnabled() && (exMsg = ex.getMessage()) != null && !exMsg.contains(((MethodParameter)parameter).getExecutable().toGenericString())) {
                    logger.debug((Object)InvocableHandlerMethod.formatArgumentError((MethodParameter)parameter, exMsg));
                }
                throw ex;
            }
        }
        return args;
    }

    @Nullable
    protected Object doInvoke(Object ... args) throws Exception {
        Method method = this.getBridgedMethod();
        try {
            if (KotlinDetector.isSuspendingFunction(method)) {
                return CoroutinesUtils.invokeSuspendingFunction(method, this.getBean(), args);
            }
            return method.invoke(this.getBean(), args);
        }
        catch (IllegalArgumentException ex) {
            this.assertTargetBean(method, this.getBean(), args);
            String text = ex.getMessage() != null ? ex.getMessage() : "Illegal argument";
            throw new IllegalStateException(this.formatInvokeError(text, args), ex);
        }
        catch (InvocationTargetException ex) {
            Throwable targetException = ex.getTargetException();
            if (targetException instanceof RuntimeException) {
                throw (RuntimeException)targetException;
            }
            if (targetException instanceof Error) {
                throw (Error)targetException;
            }
            if (targetException instanceof Exception) {
                throw (Exception)targetException;
            }
            throw new IllegalStateException(this.formatInvokeError("Invocation failure", args), targetException);
        }
    }
}

