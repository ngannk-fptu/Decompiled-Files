/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.method.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.ModelAndViewContainer;

public class InvocableHandlerMethod
extends HandlerMethod {
    @Nullable
    private WebDataBinderFactory dataBinderFactory;
    private HandlerMethodArgumentResolverComposite argumentResolvers = new HandlerMethodArgumentResolverComposite();
    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public InvocableHandlerMethod(HandlerMethod handlerMethod) {
        super(handlerMethod);
    }

    public InvocableHandlerMethod(Object bean2, Method method) {
        super(bean2, method);
    }

    public InvocableHandlerMethod(Object bean2, String methodName, Class<?> ... parameterTypes) throws NoSuchMethodException {
        super(bean2, methodName, parameterTypes);
    }

    public void setDataBinderFactory(WebDataBinderFactory dataBinderFactory) {
        this.dataBinderFactory = dataBinderFactory;
    }

    public void setHandlerMethodArgumentResolvers(HandlerMethodArgumentResolverComposite argumentResolvers) {
        this.argumentResolvers = argumentResolvers;
    }

    public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    @Nullable
    public Object invokeForRequest(NativeWebRequest request, @Nullable ModelAndViewContainer mavContainer, Object ... providedArgs) throws Exception {
        Object[] args = this.getMethodArgumentValues(request, mavContainer, providedArgs);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Invoking '" + ClassUtils.getQualifiedMethodName(this.getMethod(), this.getBeanType()) + "' with arguments " + Arrays.toString(args));
        }
        Object returnValue = this.doInvoke(args);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Method [" + ClassUtils.getQualifiedMethodName(this.getMethod(), this.getBeanType()) + "] returned [" + returnValue + "]");
        }
        return returnValue;
    }

    private Object[] getMethodArgumentValues(NativeWebRequest request, @Nullable ModelAndViewContainer mavContainer, Object ... providedArgs) throws Exception {
        MethodParameter[] parameters = this.getMethodParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; ++i) {
            MethodParameter parameter = parameters[i];
            parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);
            args[i] = this.resolveProvidedArgument(parameter, providedArgs);
            if (args[i] != null) continue;
            if (this.argumentResolvers.supportsParameter(parameter)) {
                try {
                    args[i] = this.argumentResolvers.resolveArgument(parameter, mavContainer, request, this.dataBinderFactory);
                    continue;
                }
                catch (Exception ex) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug(this.getArgumentResolutionErrorMessage("Failed to resolve", i), ex);
                    }
                    throw ex;
                }
            }
            if (args[i] != null) continue;
            throw new IllegalStateException("Could not resolve method parameter at index " + parameter.getParameterIndex() + " in " + parameter.getExecutable().toGenericString() + ": " + this.getArgumentResolutionErrorMessage("No suitable resolver for", i));
        }
        return args;
    }

    private String getArgumentResolutionErrorMessage(String text, int index) {
        Class<?> paramType = this.getMethodParameters()[index].getParameterType();
        return text + " argument " + index + " of type '" + paramType.getName() + "'";
    }

    @Nullable
    private Object resolveProvidedArgument(MethodParameter parameter, Object ... providedArgs) {
        if (providedArgs == null) {
            return null;
        }
        for (Object providedArg : providedArgs) {
            if (!parameter.getParameterType().isInstance(providedArg)) continue;
            return providedArg;
        }
        return null;
    }

    protected Object doInvoke(Object ... args) throws Exception {
        ReflectionUtils.makeAccessible(this.getBridgedMethod());
        try {
            return this.getBridgedMethod().invoke(this.getBean(), args);
        }
        catch (IllegalArgumentException ex) {
            this.assertTargetBean(this.getBridgedMethod(), this.getBean(), args);
            String text = ex.getMessage() != null ? ex.getMessage() : "Illegal argument";
            throw new IllegalStateException(this.getInvocationErrorMessage(text, args), ex);
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
            String text = this.getInvocationErrorMessage("Failed to invoke handler method", args);
            throw new IllegalStateException(text, targetException);
        }
    }

    private void assertTargetBean(Method method, Object targetBean, Object[] args) {
        Class<?> targetBeanClass;
        Class<?> methodDeclaringClass = method.getDeclaringClass();
        if (!methodDeclaringClass.isAssignableFrom(targetBeanClass = targetBean.getClass())) {
            String text = "The mapped handler method class '" + methodDeclaringClass.getName() + "' is not an instance of the actual controller bean class '" + targetBeanClass.getName() + "'. If the controller requires proxying (e.g. due to @Transactional), please use class-based proxying.";
            throw new IllegalStateException(this.getInvocationErrorMessage(text, args));
        }
    }

    private String getInvocationErrorMessage(String text, Object[] resolvedArgs) {
        StringBuilder sb = new StringBuilder(this.getDetailedErrorMessage(text));
        sb.append("Resolved arguments: \n");
        for (int i = 0; i < resolvedArgs.length; ++i) {
            sb.append("[").append(i).append("] ");
            if (resolvedArgs[i] == null) {
                sb.append("[null] \n");
                continue;
            }
            sb.append("[type=").append(resolvedArgs[i].getClass().getName()).append("] ");
            sb.append("[value=").append(resolvedArgs[i]).append("]\n");
        }
        return sb.toString();
    }

    protected String getDetailedErrorMessage(String text) {
        StringBuilder sb = new StringBuilder(text).append("\n");
        sb.append("HandlerMethod details: \n");
        sb.append("Controller [").append(this.getBeanType().getName()).append("]\n");
        sb.append("Method [").append(this.getBridgedMethod().toGenericString()).append("]\n");
        return sb.toString();
    }
}

