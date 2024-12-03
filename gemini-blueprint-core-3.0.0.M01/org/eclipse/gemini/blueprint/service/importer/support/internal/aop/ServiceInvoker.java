/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.ServiceReference
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.beans.factory.DisposableBean
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.aop;

import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ServiceReferenceProvider;
import org.osgi.framework.ServiceReference;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.DisposableBean;

public abstract class ServiceInvoker
implements MethodInterceptor,
ServiceReferenceProvider,
DisposableBean {
    protected final transient Log log = LogFactory.getLog(this.getClass());

    protected Object doInvoke(Object service, MethodInvocation invocation) throws Throwable {
        return AopUtils.invokeJoinpointUsingReflection((Object)service, (Method)invocation.getMethod(), (Object[])invocation.getArguments());
    }

    public final Object invoke(MethodInvocation invocation) throws Throwable {
        return this.doInvoke(this.getTarget(), invocation);
    }

    protected abstract Object getTarget();

    @Override
    public ServiceReference getServiceReference() {
        return null;
    }

    public abstract void destroy();
}

