/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.MutablePropertyValues
 *  org.springframework.beans.PropertyValues
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.TypedStringValue
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 */
package org.eclipse.gemini.blueprint.blueprint.reflect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.gemini.blueprint.blueprint.reflect.MetadataUtils;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimpleComponentMetadata;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimpleReferenceListenerMetadata;
import org.eclipse.gemini.blueprint.service.importer.support.Availability;
import org.osgi.service.blueprint.reflect.ReferenceListener;
import org.osgi.service.blueprint.reflect.ServiceReferenceMetadata;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

abstract class SimpleServiceReferenceComponentMetadata
extends SimpleComponentMetadata
implements ServiceReferenceMetadata {
    private static final String FILTER_PROP = "filter";
    private static final String INTERFACES_PROP = "interfaces";
    private static final String AVAILABILITY_PROP = "availability";
    private static final String SERVICE_NAME_PROP = "serviceBeanName";
    private static final String LISTENERS_PROP = "listeners";
    private final String componentName;
    private final String filter;
    private final int availability;
    private final String intf;
    private final Collection<ReferenceListener> listeners;

    public SimpleServiceReferenceComponentMetadata(String name, BeanDefinition definition) {
        super(name, definition);
        MutablePropertyValues pvs = this.beanDefinition.getPropertyValues();
        this.componentName = (String)MetadataUtils.getValue((PropertyValues)pvs, SERVICE_NAME_PROP);
        this.filter = (String)MetadataUtils.getValue((PropertyValues)pvs, FILTER_PROP);
        Availability avail = (Availability)((Object)MetadataUtils.getValue((PropertyValues)pvs, AVAILABILITY_PROP));
        this.availability = Availability.OPTIONAL.equals((Object)avail) ? 2 : 1;
        Object value = MetadataUtils.getValue((PropertyValues)pvs, INTERFACES_PROP);
        if (value instanceof String) {
            this.intf = (String)value;
        } else if (value instanceof Collection) {
            Collection values = (Collection)value;
            this.intf = ((TypedStringValue)values.iterator().next()).getValue();
        } else {
            this.intf = null;
        }
        ArrayList<SimpleReferenceListenerMetadata> foundListeners = new ArrayList<SimpleReferenceListenerMetadata>(4);
        List listenerDefinitions = (List)MetadataUtils.getValue((PropertyValues)pvs, LISTENERS_PROP);
        if (listenerDefinitions != null) {
            for (AbstractBeanDefinition beanDef : listenerDefinitions) {
                foundListeners.add(new SimpleReferenceListenerMetadata(beanDef));
            }
        }
        this.listeners = Collections.unmodifiableCollection(foundListeners);
    }

    @Override
    public int getAvailability() {
        return this.availability;
    }

    @Override
    public String getComponentName() {
        return this.componentName;
    }

    @Override
    public String getFilter() {
        return this.filter;
    }

    @Override
    public String getInterface() {
        return this.intf;
    }

    @Override
    public Collection<ReferenceListener> getReferenceListeners() {
        return this.listeners;
    }
}

