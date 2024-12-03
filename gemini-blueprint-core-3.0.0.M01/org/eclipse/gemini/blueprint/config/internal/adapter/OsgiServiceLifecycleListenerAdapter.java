/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.ServiceReference
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.config.internal.adapter;

import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.config.internal.adapter.CustomListenerAdapterUtils;
import org.eclipse.gemini.blueprint.context.support.internal.security.SecurityUtils;
import org.eclipse.gemini.blueprint.service.importer.ImportedOsgiServiceProxy;
import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.eclipse.gemini.blueprint.service.importer.ServiceReferenceProxy;
import org.osgi.framework.ServiceReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public class OsgiServiceLifecycleListenerAdapter
implements OsgiServiceLifecycleListener,
InitializingBean,
BeanFactoryAware {
    private static final Log log = LogFactory.getLog(OsgiServiceLifecycleListenerAdapter.class);
    private Map<Class<?>, List<Method>> bindMethods;
    private Map<Class<?>, List<Method>> unbindMethods;
    private boolean isBlueprintCompliant = false;
    private Method bindReference;
    private Method unbindReference;
    private String bindMethod;
    private String unbindMethod;
    private boolean isLifecycleListener;
    private BeanFactory beanFactory;
    private String targetBeanName;
    private Object target;
    private boolean initialized;

    public void afterPropertiesSet() {
        Assert.notNull((Object)this.beanFactory);
        Assert.isTrue((this.target != null || StringUtils.hasText((String)this.targetBeanName) ? 1 : 0) != 0, (String)"one of 'target' or 'targetBeanName' properties has to be set");
        if (this.target != null) {
            this.initialized = true;
        }
        this.initialize();
    }

    private void retrieveTarget() {
        this.target = this.beanFactory.getBean(this.targetBeanName);
        this.initialized = true;
    }

    private void initialize() {
        Class clazz = this.target == null ? this.beanFactory.getType(this.targetBeanName) : this.target.getClass();
        Assert.notNull((Object)clazz, (String)("listener " + this.targetBeanName + " class type cannot be determined"));
        this.isLifecycleListener = OsgiServiceLifecycleListener.class.isAssignableFrom(clazz);
        if (this.isLifecycleListener && log.isDebugEnabled()) {
            log.debug((Object)(clazz.getName() + " is a lifecycle listener"));
        }
        this.bindMethods = CustomListenerAdapterUtils.determineCustomMethods(clazz, this.bindMethod, this.isBlueprintCompliant);
        boolean isSecurityEnabled = System.getSecurityManager() != null;
        final Class clz = clazz;
        if (StringUtils.hasText((String)this.bindMethod)) {
            this.bindReference = isSecurityEnabled ? AccessController.doPrivileged(new PrivilegedAction<Method>(){

                @Override
                public Method run() {
                    return OsgiServiceLifecycleListenerAdapter.this.findServiceReferenceMethod(clz, OsgiServiceLifecycleListenerAdapter.this.bindMethod);
                }
            }) : this.findServiceReferenceMethod(clz, this.bindMethod);
            if (this.bindMethods.isEmpty()) {
                String beanName = this.target == null ? "" : " bean [" + this.targetBeanName + "] ;";
                throw new IllegalArgumentException("Custom bind method [" + this.bindMethod + "] not found on " + beanName + "class " + clazz);
            }
        }
        this.unbindMethods = CustomListenerAdapterUtils.determineCustomMethods(clazz, this.unbindMethod, this.isBlueprintCompliant);
        if (StringUtils.hasText((String)this.unbindMethod)) {
            this.unbindReference = isSecurityEnabled ? AccessController.doPrivileged(new PrivilegedAction<Method>(){

                @Override
                public Method run() {
                    return OsgiServiceLifecycleListenerAdapter.this.findServiceReferenceMethod(clz, OsgiServiceLifecycleListenerAdapter.this.unbindMethod);
                }
            }) : this.findServiceReferenceMethod(clz, this.unbindMethod);
            if (this.unbindMethods.isEmpty()) {
                String beanName = this.target == null ? "" : " bean [" + this.targetBeanName + "] ;";
                throw new IllegalArgumentException("Custom unbind method [" + this.unbindMethod + "] not found on " + beanName + "class " + clazz);
            }
        }
        if (!this.isLifecycleListener && this.bindMethods.isEmpty() && this.unbindMethods.isEmpty() && this.bindReference == null && this.unbindReference == null) {
            throw new IllegalArgumentException("target object needs to implement " + OsgiServiceLifecycleListener.class.getName() + " or custom bind/unbind methods have to be specified");
        }
        if (log.isTraceEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append("Discovered bind methods=");
            builder.append(this.bindMethods.values());
            builder.append(", bind ServiceReference=");
            builder.append(this.bindReference);
            builder.append("\nunbind methods=");
            builder.append(this.unbindMethods.values());
            builder.append(", unbind ServiceReference=");
            builder.append(this.unbindReference);
            log.trace((Object)builder.toString());
        }
    }

    private Method findServiceReferenceMethod(Class<?> clazz, String methodName) {
        Method method = ReflectionUtils.findMethod(clazz, (String)methodName, (Class[])new Class[]{ServiceReference.class});
        if (method != null) {
            ReflectionUtils.makeAccessible((Method)method);
        }
        return method;
    }

    private void invokeCustomServiceReferenceMethod(Object target, Method method, Object service) {
        if (method != null) {
            boolean trace = log.isTraceEnabled();
            if (trace) {
                log.trace((Object)("invoking listener custom method " + method));
            }
            ServiceReferenceProxy ref = service != null ? ((ImportedOsgiServiceProxy)service).getServiceReference() : null;
            try {
                org.eclipse.gemini.blueprint.util.internal.ReflectionUtils.invokeMethod(method, target, new Object[]{ref});
            }
            catch (Exception ex) {
                Exception cause = org.eclipse.gemini.blueprint.util.internal.ReflectionUtils.getInvocationException(ex);
                log.warn((Object)("custom method [" + method + "] threw exception when passing service [" + ObjectUtils.identityToString((Object)service) + "]"), (Throwable)cause);
            }
        }
    }

    @Override
    public void bind(final Object service, final Map properties) throws Exception {
        boolean trace = log.isTraceEnabled();
        if (trace) {
            log.trace((Object)("Invoking bind method for service " + ObjectUtils.identityToString((Object)service) + " with props=" + properties));
        }
        if (!this.initialized) {
            this.retrieveTarget();
        }
        boolean isSecurityEnabled = System.getSecurityManager() != null;
        AccessControlContext acc = null;
        if (isSecurityEnabled) {
            acc = SecurityUtils.getAccFrom(this.beanFactory);
        }
        if (this.isLifecycleListener) {
            if (trace) {
                log.trace((Object)"Invoking listener interface methods");
            }
            try {
                if (isSecurityEnabled) {
                    AccessController.doPrivileged(new PrivilegedExceptionAction<Object>(){

                        @Override
                        public Object run() throws Exception {
                            ((OsgiServiceLifecycleListener)OsgiServiceLifecycleListenerAdapter.this.target).bind(service, properties);
                            return null;
                        }
                    }, acc);
                } else {
                    ((OsgiServiceLifecycleListener)this.target).bind(service, properties);
                }
            }
            catch (Exception ex) {
                if (ex instanceof PrivilegedActionException) {
                    ex = ((PrivilegedActionException)ex).getException();
                }
                log.warn((Object)("standard bind method on [" + this.target.getClass().getName() + "] threw exception"), (Throwable)ex);
            }
        }
        if (isSecurityEnabled) {
            AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    CustomListenerAdapterUtils.invokeCustomMethods(OsgiServiceLifecycleListenerAdapter.this.target, OsgiServiceLifecycleListenerAdapter.this.bindMethods, service, properties);
                    OsgiServiceLifecycleListenerAdapter.this.invokeCustomServiceReferenceMethod(OsgiServiceLifecycleListenerAdapter.this.target, OsgiServiceLifecycleListenerAdapter.this.bindReference, service);
                    return null;
                }
            }, acc);
        } else {
            CustomListenerAdapterUtils.invokeCustomMethods(this.target, this.bindMethods, service, properties);
            this.invokeCustomServiceReferenceMethod(this.target, this.bindReference, service);
        }
    }

    @Override
    public void unbind(final Object service, final Map properties) throws Exception {
        boolean trace = log.isTraceEnabled();
        if (!this.initialized) {
            this.retrieveTarget();
        }
        if (trace) {
            log.trace((Object)("Invoking unbind method for service " + ObjectUtils.identityToString((Object)service) + " with props=" + properties));
        }
        boolean isSecurityEnabled = System.getSecurityManager() != null;
        AccessControlContext acc = null;
        if (isSecurityEnabled) {
            acc = SecurityUtils.getAccFrom(this.beanFactory);
        }
        if (this.isLifecycleListener) {
            if (trace) {
                log.trace((Object)"Invoking listener interface methods");
            }
            try {
                if (isSecurityEnabled) {
                    AccessController.doPrivileged(new PrivilegedExceptionAction<Object>(){

                        @Override
                        public Object run() throws Exception {
                            ((OsgiServiceLifecycleListener)OsgiServiceLifecycleListenerAdapter.this.target).unbind(service, properties);
                            return null;
                        }
                    }, acc);
                } else {
                    ((OsgiServiceLifecycleListener)this.target).unbind(service, properties);
                }
            }
            catch (Exception ex) {
                log.warn((Object)("Standard unbind method on [" + this.target.getClass().getName() + "] threw exception"), (Throwable)ex);
            }
        }
        if (isSecurityEnabled) {
            AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    CustomListenerAdapterUtils.invokeCustomMethods(OsgiServiceLifecycleListenerAdapter.this.target, OsgiServiceLifecycleListenerAdapter.this.unbindMethods, service, properties);
                    OsgiServiceLifecycleListenerAdapter.this.invokeCustomServiceReferenceMethod(OsgiServiceLifecycleListenerAdapter.this.target, OsgiServiceLifecycleListenerAdapter.this.unbindReference, service);
                    return null;
                }
            }, acc);
        } else {
            CustomListenerAdapterUtils.invokeCustomMethods(this.target, this.unbindMethods, service, properties);
            this.invokeCustomServiceReferenceMethod(this.target, this.unbindReference, service);
        }
    }

    public void setBindMethod(String bindMethod) {
        this.bindMethod = bindMethod;
    }

    public void setUnbindMethod(String unbindMethod) {
        this.unbindMethod = unbindMethod;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTargetBeanName(String targetName) {
        this.targetBeanName = targetName;
    }

    public void setBlueprintCompliant(boolean compliant) {
        this.isBlueprintCompliant = compliant;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}

