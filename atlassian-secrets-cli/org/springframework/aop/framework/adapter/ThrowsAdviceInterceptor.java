/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.AfterAdvice;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ThrowsAdviceInterceptor
implements MethodInterceptor,
AfterAdvice {
    private static final String AFTER_THROWING = "afterThrowing";
    private static final Log logger = LogFactory.getLog(ThrowsAdviceInterceptor.class);
    private final Object throwsAdvice;
    private final Map<Class<?>, Method> exceptionHandlerMap = new HashMap();

    public ThrowsAdviceInterceptor(Object throwsAdvice) {
        Method[] methods;
        Assert.notNull(throwsAdvice, "Advice must not be null");
        this.throwsAdvice = throwsAdvice;
        for (Method method : methods = throwsAdvice.getClass().getMethods()) {
            Class<?> throwableParam;
            if (!method.getName().equals(AFTER_THROWING) || method.getParameterCount() != 1 && method.getParameterCount() != 4 || !Throwable.class.isAssignableFrom(throwableParam = method.getParameterTypes()[method.getParameterCount() - 1])) continue;
            this.exceptionHandlerMap.put(throwableParam, method);
            if (!logger.isDebugEnabled()) continue;
            logger.debug("Found exception handler method on throws advice: " + method);
        }
        if (this.exceptionHandlerMap.isEmpty()) {
            throw new IllegalArgumentException("At least one handler method must be found in class [" + throwsAdvice.getClass() + "]");
        }
    }

    public int getHandlerMethodCount() {
        return this.exceptionHandlerMap.size();
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        }
        catch (Throwable ex) {
            Method handlerMethod = this.getExceptionHandler(ex);
            if (handlerMethod != null) {
                this.invokeHandlerMethod(mi, ex, handlerMethod);
            }
            throw ex;
        }
    }

    @Nullable
    private Method getExceptionHandler(Throwable exception) {
        Class<?> exceptionClass = exception.getClass();
        if (logger.isTraceEnabled()) {
            logger.trace("Trying to find handler for exception of type [" + exceptionClass.getName() + "]");
        }
        Method handler = this.exceptionHandlerMap.get(exceptionClass);
        while (handler == null && exceptionClass != Throwable.class) {
            exceptionClass = exceptionClass.getSuperclass();
            handler = this.exceptionHandlerMap.get(exceptionClass);
        }
        if (handler != null && logger.isDebugEnabled()) {
            logger.debug("Found handler for exception of type [" + exceptionClass.getName() + "]: " + handler);
        }
        return handler;
    }

    private void invokeHandlerMethod(MethodInvocation mi, Throwable ex, Method method) throws Throwable {
        Object[] handlerArgs = method.getParameterCount() == 1 ? new Object[]{ex} : new Object[]{mi.getMethod(), mi.getArguments(), mi.getThis(), ex};
        try {
            method.invoke(this.throwsAdvice, handlerArgs);
        }
        catch (InvocationTargetException targetEx) {
            throw targetEx.getTargetException();
        }
    }
}

