/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.plugin.jmx;

import com.atlassian.plugin.jmx.JmxUtil;
import com.google.common.annotations.VisibleForTesting;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.management.ObjectName;

public abstract class AbstractJmxBridge<MXBean> {
    private final ObjectName objectName;
    private final Class<MXBean> mxBeanClass;

    public AbstractJmxBridge(ObjectName objectName, Class<MXBean> mxBeanClass) {
        this.objectName = objectName;
        this.mxBeanClass = mxBeanClass;
    }

    protected abstract MXBean getMXBean();

    public void register() {
        this.registerInternal();
    }

    public ObjectName getObjectName() {
        return this.objectName;
    }

    @VisibleForTesting
    WeakMXBeanInvocationHandler<MXBean> registerInternal() {
        WeakMXBeanInvocationHandler<MXBean> handler = new WeakMXBeanInvocationHandler<MXBean>(this.objectName, this.getMXBean());
        Object proxy = Proxy.newProxyInstance(this.mxBeanClass.getClassLoader(), new Class[]{this.mxBeanClass}, handler);
        JmxUtil.register(proxy, this.objectName);
        return handler;
    }

    public void unregister() {
        JmxUtil.unregister(this.objectName);
    }

    @VisibleForTesting
    static class WeakMXBeanInvocationHandler<MXBean>
    implements InvocationHandler {
        private final ObjectName objectName;
        private final WeakReference<MXBean> implementationReference;

        public WeakMXBeanInvocationHandler(ObjectName objectName, MXBean implementation) {
            this.objectName = objectName;
            this.implementationReference = new WeakReference<MXBean>(implementation);
        }

        @VisibleForTesting
        WeakReference<MXBean> getImplementationReference() {
            return this.implementationReference;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object implementation = this.implementationReference.get();
            if (null == implementation) {
                JmxUtil.unregister(this.objectName);
                throw new IllegalStateException("Cannot use stale MXBean '" + this.objectName + "'");
            }
            return method.invoke(implementation, args);
        }
    }
}

