/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.util.Assert
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
import org.eclipse.gemini.blueprint.service.exporter.OsgiServiceRegistrationListener;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class OsgiServiceRegistrationListenerAdapter
implements OsgiServiceRegistrationListener,
InitializingBean,
BeanFactoryAware {
    private static final Log log = LogFactory.getLog(OsgiServiceRegistrationListenerAdapter.class);
    private boolean isListener;
    private String registrationMethod;
    private String unregistrationMethod;
    private Object target;
    private String targetBeanName;
    private BeanFactory beanFactory;
    private boolean initialized;
    private Map<Class<?>, List<Method>> registrationMethods;
    private Map<Class<?>, List<Method>> unregistrationMethods;
    private boolean isBlueprintCompliant = false;

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
        this.isListener = OsgiServiceRegistrationListener.class.isAssignableFrom(clazz);
        if (this.isListener && log.isDebugEnabled()) {
            log.debug((Object)(clazz.getName() + " is a registration listener"));
        }
        this.registrationMethods = CustomListenerAdapterUtils.determineCustomMethods(clazz, this.registrationMethod, this.isBlueprintCompliant);
        this.unregistrationMethods = CustomListenerAdapterUtils.determineCustomMethods(clazz, this.unregistrationMethod, this.isBlueprintCompliant);
        if (!this.isListener && this.registrationMethods.isEmpty() && this.unregistrationMethods.isEmpty()) {
            throw new IllegalArgumentException("Target object needs to implement " + OsgiServiceRegistrationListener.class.getName() + " or custom registered/unregistered methods have to be specified");
        }
        if (log.isTraceEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append("Discovered bind methods=");
            builder.append(this.registrationMethods.values());
            builder.append("\nunbind methods=");
            builder.append(this.unregistrationMethods.values());
            log.trace((Object)builder.toString());
        }
    }

    @Override
    public void registered(final Object service, final Map serviceProperties) {
        boolean trace = log.isTraceEnabled();
        if (trace) {
            log.trace((Object)("Invoking registered method with props=" + serviceProperties));
        }
        if (!this.initialized) {
            this.retrieveTarget();
        }
        boolean isSecurityEnabled = System.getSecurityManager() != null;
        AccessControlContext acc = null;
        if (isSecurityEnabled) {
            acc = SecurityUtils.getAccFrom(this.beanFactory);
        }
        if (this.isListener) {
            if (trace) {
                log.trace((Object)"Invoking listener interface methods");
            }
            try {
                if (isSecurityEnabled) {
                    AccessController.doPrivileged(new PrivilegedExceptionAction<Object>(){

                        @Override
                        public Object run() throws Exception {
                            ((OsgiServiceRegistrationListener)OsgiServiceRegistrationListenerAdapter.this.target).registered(service, serviceProperties);
                            return null;
                        }
                    }, acc);
                } else {
                    ((OsgiServiceRegistrationListener)this.target).registered(service, serviceProperties);
                }
            }
            catch (Exception ex) {
                if (ex instanceof PrivilegedActionException) {
                    ex = ((PrivilegedActionException)ex).getException();
                }
                log.warn((Object)("Standard registered method on [" + this.target.getClass().getName() + "] threw exception"), (Throwable)ex);
            }
        }
        if (isSecurityEnabled) {
            AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    CustomListenerAdapterUtils.invokeCustomMethods(OsgiServiceRegistrationListenerAdapter.this.target, OsgiServiceRegistrationListenerAdapter.this.registrationMethods, service, serviceProperties);
                    return null;
                }
            }, acc);
        } else {
            CustomListenerAdapterUtils.invokeCustomMethods(this.target, this.registrationMethods, service, serviceProperties);
        }
    }

    @Override
    public void unregistered(final Object service, final Map serviceProperties) {
        boolean trace = log.isTraceEnabled();
        if (trace) {
            log.trace((Object)("Invoking unregistered method with props=" + serviceProperties));
        }
        if (!this.initialized) {
            this.retrieveTarget();
        }
        boolean isSecurityEnabled = System.getSecurityManager() != null;
        AccessControlContext acc = null;
        if (isSecurityEnabled) {
            acc = SecurityUtils.getAccFrom(this.beanFactory);
        }
        if (this.isListener) {
            if (trace) {
                log.trace((Object)"Invoking listener interface methods");
            }
            try {
                if (isSecurityEnabled) {
                    AccessController.doPrivileged(new PrivilegedExceptionAction<Object>(){

                        @Override
                        public Object run() throws Exception {
                            ((OsgiServiceRegistrationListener)OsgiServiceRegistrationListenerAdapter.this.target).unregistered(service, serviceProperties);
                            return null;
                        }
                    }, acc);
                } else {
                    ((OsgiServiceRegistrationListener)this.target).unregistered(service, serviceProperties);
                }
            }
            catch (Exception ex) {
                log.warn((Object)("Standard unregistered method on [" + this.target.getClass().getName() + "] threw exception"), (Throwable)ex);
            }
        }
        if (isSecurityEnabled) {
            AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    CustomListenerAdapterUtils.invokeCustomMethods(OsgiServiceRegistrationListenerAdapter.this.target, OsgiServiceRegistrationListenerAdapter.this.unregistrationMethods, service, serviceProperties);
                    return null;
                }
            }, acc);
        } else {
            CustomListenerAdapterUtils.invokeCustomMethods(this.target, this.unregistrationMethods, service, serviceProperties);
        }
    }

    public void setRegistrationMethod(String registrationMethod) {
        this.registrationMethod = registrationMethod;
    }

    public void setUnregistrationMethod(String unregistrationMethod) {
        this.unregistrationMethod = unregistrationMethod;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTargetBeanName(String targetBeanName) {
        this.targetBeanName = targetBeanName;
    }

    public void setBlueprintCompliant(boolean compliant) {
        this.isBlueprintCompliant = compliant;
    }
}

