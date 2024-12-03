/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.support.AbstractBeanFactory
 */
package org.eclipse.gemini.blueprint.compendium.internal.cm;

import java.util.Map;
import org.eclipse.gemini.blueprint.compendium.internal.cm.CMUtils;
import org.eclipse.gemini.blueprint.compendium.internal.cm.UpdateCallback;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.AbstractBeanFactory;

class ContainerManagedUpdate
implements UpdateCallback {
    private final AbstractBeanFactory beanFactory;

    public ContainerManagedUpdate(BeanFactory beanFactory) {
        this.beanFactory = beanFactory instanceof AbstractBeanFactory ? (AbstractBeanFactory)beanFactory : null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void update(Object instance, Map properties) {
        Object object = instance;
        synchronized (object) {
            CMUtils.applyMapOntoInstance(instance, properties, this.beanFactory);
        }
    }
}

