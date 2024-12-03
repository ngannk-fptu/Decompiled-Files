/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.MutablePropertyValues
 *  org.springframework.beans.PropertyValues
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 */
package org.eclipse.gemini.blueprint.blueprint.reflect;

import org.eclipse.gemini.blueprint.blueprint.reflect.MetadataUtils;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimpleRefMetadata;
import org.eclipse.gemini.blueprint.blueprint.reflect.ValueFactory;
import org.osgi.service.blueprint.reflect.RegistrationListener;
import org.osgi.service.blueprint.reflect.Target;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

class SimpleRegistrationListener
implements RegistrationListener {
    private static final String REG_PROP = "registrationMethod";
    private static final String UNREG_PROP = "unregistrationMethod";
    private static final String LISTENER_NAME_PROP = "targetBeanName";
    private static final String LISTENER_PROP = "target";
    private final Target listenerComponent;
    private final String registrationMethod;
    private final String unregistrationMethod;

    public SimpleRegistrationListener(AbstractBeanDefinition beanDefinition) {
        MutablePropertyValues pvs = beanDefinition.getPropertyValues();
        this.registrationMethod = (String)MetadataUtils.getValue((PropertyValues)pvs, REG_PROP);
        this.unregistrationMethod = (String)MetadataUtils.getValue((PropertyValues)pvs, UNREG_PROP);
        this.listenerComponent = pvs.contains(LISTENER_NAME_PROP) ? new SimpleRefMetadata((String)MetadataUtils.getValue((PropertyValues)pvs, LISTENER_NAME_PROP)) : (Target)ValueFactory.buildValue(MetadataUtils.getValue((PropertyValues)pvs, LISTENER_PROP));
    }

    @Override
    public Target getListenerComponent() {
        return this.listenerComponent;
    }

    @Override
    public String getRegistrationMethod() {
        return this.registrationMethod;
    }

    @Override
    public String getUnregistrationMethod() {
        return this.unregistrationMethod;
    }
}

