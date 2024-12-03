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
import org.osgi.service.blueprint.reflect.ReferenceListener;
import org.osgi.service.blueprint.reflect.Target;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

class SimpleReferenceListenerMetadata
implements ReferenceListener {
    private static final String BIND_PROP = "bindMethod";
    private static final String UNBIND_PROP = "unbindMethod";
    private static final String LISTENER_NAME_PROP = "targetBeanName";
    private static final String LISTENER_PROP = "target";
    private final String bindMethodName;
    private final String unbindMethodName;
    private final Target listenerComponent;

    public SimpleReferenceListenerMetadata(AbstractBeanDefinition beanDefinition) {
        MutablePropertyValues pvs = beanDefinition.getPropertyValues();
        this.bindMethodName = (String)MetadataUtils.getValue((PropertyValues)pvs, BIND_PROP);
        this.unbindMethodName = (String)MetadataUtils.getValue((PropertyValues)pvs, UNBIND_PROP);
        this.listenerComponent = pvs.contains(LISTENER_NAME_PROP) ? new SimpleRefMetadata((String)MetadataUtils.getValue((PropertyValues)pvs, LISTENER_NAME_PROP)) : (Target)ValueFactory.buildValue(MetadataUtils.getValue((PropertyValues)pvs, LISTENER_PROP));
    }

    public SimpleReferenceListenerMetadata(String bindMethodName, String unbindMethodName, Target listenerComponent) {
        this.bindMethodName = bindMethodName;
        this.unbindMethodName = unbindMethodName;
        this.listenerComponent = listenerComponent;
    }

    @Override
    public String getBindMethod() {
        return this.bindMethodName;
    }

    @Override
    public Target getListenerComponent() {
        return this.listenerComponent;
    }

    @Override
    public String getUnbindMethod() {
        return this.unbindMethodName;
    }
}

