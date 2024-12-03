/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.PropertyValues
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor
 *  org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor
 */
package com.atlassian.plugin.spring.scanner.runtime.impl;

import java.beans.PropertyDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Objects;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;

public class DevModeBeanInitialisationLoggerBeanPostProcessor
implements InstantiationAwareBeanPostProcessor,
InitializingBean,
DestructionAwareBeanPostProcessor,
DisposableBean {
    public static final String ATLASSIAN_DEV_MODE = "atlassian.dev.mode";
    private static final String BUNDLE_LOGGER_NAME_FORMAT = "com.atlassian.plugin.spring.scanner.%s";
    private final Bundle bundle;
    private final Logger bundleLogger;

    public DevModeBeanInitialisationLoggerBeanPostProcessor(BundleContext bundleContext) {
        this.bundle = Objects.requireNonNull(bundleContext.getBundle());
        this.bundleLogger = LoggerFactory.getLogger((String)String.format(BUNDLE_LOGGER_NAME_FORMAT, bundleContext.getBundle().getSymbolicName()));
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) {
        this.logBeanDetail("AfterInitialisation", bean.getClass(), beanName);
        return bean;
    }

    public Object postProcessBeforeInstantiation(Class beanClass, String beanName) {
        this.logBeanDetail("BeforeInstantiation", beanClass, beanName);
        return null;
    }

    public boolean postProcessAfterInstantiation(Object bean, String beanName) {
        return true;
    }

    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) {
        return pvs;
    }

    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        this.logBeanDetail("BeforeDestruction", bean.getClass(), beanName);
    }

    private void logBeanDetail(String stage, Class beanClass, String beanName) {
        this.bundleLogger.debug("{} [bean={}, type={}]", new Object[]{stage, beanName, beanClass.getName()});
    }

    public void afterPropertiesSet() {
        this.logInDevMode("Spring context started for bundle: {} id({}) v({}) {}", this.bundle.getSymbolicName(), this.bundle.getBundleId(), this.bundle.getVersion(), this.bundle.getLocation());
        if (this.bundleLogger.isTraceEnabled()) {
            StringWriter sw = new StringWriter();
            PrintWriter out = new PrintWriter(sw);
            out.format("\tBundle Headers :\n", new Object[0]);
            Dictionary headers = this.bundle.getHeaders();
            Enumeration keys = headers.keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = headers.get(key);
                out.format("\t\t%s: %s\n", key, value);
            }
            this.bundleLogger.trace(sw.toString());
        }
    }

    public void destroy() {
        this.logInDevMode("Spring context destroyed for bundle: {} id({}) v({})", this.bundle.getSymbolicName(), this.bundle.getBundleId(), this.bundle.getVersion());
    }

    private void logInDevMode(String message, Object ... arguments) {
        if (DevModeBeanInitialisationLoggerBeanPostProcessor.isDevMode()) {
            this.bundleLogger.warn(message, arguments);
        }
    }

    private static boolean isDevMode() {
        return Boolean.getBoolean(ATLASSIAN_DEV_MODE);
    }
}

