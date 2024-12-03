/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.remoting.RemoteAccessException
 *  org.springframework.remoting.RemoteConnectFailureException
 *  org.springframework.remoting.RemoteInvocationFailureException
 *  org.springframework.remoting.support.RemoteInvocation
 *  org.springframework.remoting.support.RemoteInvocationBasedAccessor
 *  org.springframework.remoting.support.RemoteInvocationResult
 */
package org.springframework.remoting.httpinvoker;

import java.io.InvalidClassException;
import java.lang.reflect.Method;
import java.net.ConnectException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.lang.Nullable;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteInvocationFailureException;
import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;
import org.springframework.remoting.httpinvoker.HttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationBasedAccessor;
import org.springframework.remoting.support.RemoteInvocationResult;

@Deprecated
public class HttpInvokerClientInterceptor
extends RemoteInvocationBasedAccessor
implements MethodInterceptor,
HttpInvokerClientConfiguration {
    @Nullable
    private String codebaseUrl;
    @Nullable
    private HttpInvokerRequestExecutor httpInvokerRequestExecutor;

    public void setCodebaseUrl(@Nullable String codebaseUrl) {
        this.codebaseUrl = codebaseUrl;
    }

    @Override
    @Nullable
    public String getCodebaseUrl() {
        return this.codebaseUrl;
    }

    public void setHttpInvokerRequestExecutor(HttpInvokerRequestExecutor httpInvokerRequestExecutor) {
        this.httpInvokerRequestExecutor = httpInvokerRequestExecutor;
    }

    public HttpInvokerRequestExecutor getHttpInvokerRequestExecutor() {
        if (this.httpInvokerRequestExecutor == null) {
            SimpleHttpInvokerRequestExecutor executor = new SimpleHttpInvokerRequestExecutor();
            executor.setBeanClassLoader(this.getBeanClassLoader());
            this.httpInvokerRequestExecutor = executor;
        }
        return this.httpInvokerRequestExecutor;
    }

    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.getHttpInvokerRequestExecutor();
    }

    @Nullable
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        RemoteInvocationResult result;
        if (AopUtils.isToStringMethod((Method)methodInvocation.getMethod())) {
            return "HTTP invoker proxy for service URL [" + this.getServiceUrl() + "]";
        }
        RemoteInvocation invocation = this.createRemoteInvocation(methodInvocation);
        try {
            result = this.executeRequest(invocation, methodInvocation);
        }
        catch (Throwable ex) {
            RemoteAccessException rae = this.convertHttpInvokerAccessException(ex);
            throw rae != null ? rae : ex;
        }
        try {
            return this.recreateRemoteInvocationResult(result);
        }
        catch (Throwable ex) {
            if (result.hasInvocationTargetException()) {
                throw ex;
            }
            throw new RemoteInvocationFailureException("Invocation of method [" + methodInvocation.getMethod() + "] failed in HTTP invoker remote service at [" + this.getServiceUrl() + "]", ex);
        }
    }

    protected RemoteInvocationResult executeRequest(RemoteInvocation invocation, MethodInvocation originalInvocation) throws Exception {
        return this.executeRequest(invocation);
    }

    protected RemoteInvocationResult executeRequest(RemoteInvocation invocation) throws Exception {
        return this.getHttpInvokerRequestExecutor().executeRequest(this, invocation);
    }

    @Nullable
    protected RemoteAccessException convertHttpInvokerAccessException(Throwable ex) {
        if (ex instanceof ConnectException) {
            return new RemoteConnectFailureException("Could not connect to HTTP invoker remote service at [" + this.getServiceUrl() + "]", ex);
        }
        if (ex instanceof ClassNotFoundException || ex instanceof NoClassDefFoundError || ex instanceof InvalidClassException) {
            return new RemoteAccessException("Could not deserialize result from HTTP invoker remote service [" + this.getServiceUrl() + "]", ex);
        }
        if (ex instanceof Exception) {
            return new RemoteAccessException("Could not access HTTP invoker remote service at [" + this.getServiceUrl() + "]", ex);
        }
        return null;
    }
}

