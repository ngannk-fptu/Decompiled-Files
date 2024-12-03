/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.support.AbstractBeanFactory
 */
package org.eclipse.gemini.blueprint.compendium.internal.cm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.compendium.internal.cm.CMUtils;
import org.eclipse.gemini.blueprint.compendium.internal.cm.ConfigurationAdminManager;
import org.eclipse.gemini.blueprint.compendium.internal.cm.ManagedServiceBeanManager;
import org.eclipse.gemini.blueprint.compendium.internal.cm.UpdateCallback;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.support.AbstractBeanFactory;

public class DefaultManagedServiceBeanManager
implements DisposableBean,
ManagedServiceBeanManager {
    private static final Log log = LogFactory.getLog(DefaultManagedServiceBeanManager.class);
    private final Map<Integer, Object> instanceRegistry = new ConcurrentHashMap<Integer, Object>(8);
    private final UpdateCallback updateCallback;
    private final ConfigurationAdminManager cam;
    private final AbstractBeanFactory bf;

    public DefaultManagedServiceBeanManager(boolean autowireOnUpdate, String methodName, ConfigurationAdminManager cam, BeanFactory beanFactory) {
        this.updateCallback = CMUtils.createCallback(autowireOnUpdate, methodName, beanFactory);
        this.bf = beanFactory instanceof AbstractBeanFactory ? (AbstractBeanFactory)beanFactory : null;
        this.cam = cam;
        this.cam.setBeanManager(this);
    }

    @Override
    public Object register(Object bean) {
        int hashCode = System.identityHashCode(bean);
        if (log.isTraceEnabled()) {
            log.trace((Object)("Start tracking instance " + bean.getClass().getName() + "@" + hashCode));
        }
        this.instanceRegistry.put(hashCode, bean);
        this.applyInitialInjection(bean, this.cam.getConfiguration());
        return bean;
    }

    void applyInitialInjection(Object instance, Map configuration) {
        if (log.isTraceEnabled()) {
            log.trace((Object)("Applying injection to instance " + instance.getClass() + "@" + System.identityHashCode(instance) + " using map " + configuration));
        }
        CMUtils.applyMapOntoInstance(instance, configuration, this.bf);
    }

    @Override
    public void unregister(Object bean) {
        int hashCode = System.identityHashCode(bean);
        if (log.isTraceEnabled()) {
            log.trace((Object)("Stopped tracking instance " + bean.getClass().getName() + "@" + hashCode));
        }
        this.instanceRegistry.remove(new Integer(hashCode));
    }

    @Override
    public void updated(Map properties) {
        if (this.updateCallback != null) {
            CMUtils.bulkUpdate(this.updateCallback, this.instanceRegistry.values(), properties);
        }
    }

    public void destroy() {
        this.cam.destroy();
        this.instanceRegistry.clear();
    }
}

