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
import java.util.Map;
import org.eclipse.gemini.blueprint.blueprint.reflect.MetadataUtils;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimpleComponentMetadata;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimpleRefMetadata;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimpleRegistrationListener;
import org.eclipse.gemini.blueprint.blueprint.reflect.ValueFactory;
import org.eclipse.gemini.blueprint.service.exporter.support.DefaultInterfaceDetector;
import org.osgi.service.blueprint.reflect.MapEntry;
import org.osgi.service.blueprint.reflect.RegistrationListener;
import org.osgi.service.blueprint.reflect.ServiceMetadata;
import org.osgi.service.blueprint.reflect.Target;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

class SimpleServiceExportComponentMetadata
extends SimpleComponentMetadata
implements ServiceMetadata {
    private static final String AUTO_EXPORT_PROP = "interfaceDetector";
    private static final String RANKING_PROP = "ranking";
    private static final String INTERFACES_PROP = "interfaces";
    private static final String SERVICE_NAME_PROP = "targetBeanName";
    private static final String SERVICE_INSTANCE_PROP = "target";
    private static final String SERVICE_PROPERTIES_PROP = "serviceProperties";
    private static final String LISTENERS_PROP = "listeners";
    private static final String LAZY_LISTENERS = "lazyListeners";
    private final int autoExport;
    private final List<String> interfaces;
    private final int ranking;
    private final Target component;
    private final List<MapEntry> serviceProperties;
    private final Collection<RegistrationListener> listeners;
    private final int activation;

    public SimpleServiceExportComponentMetadata(String name, BeanDefinition definition) {
        super(name, definition);
        MutablePropertyValues pvs = definition.getPropertyValues();
        DefaultInterfaceDetector autoExp = (DefaultInterfaceDetector)MetadataUtils.getValue((PropertyValues)pvs, AUTO_EXPORT_PROP);
        this.autoExport = autoExp.ordinal() + 1;
        if (pvs.contains(RANKING_PROP)) {
            String rank = (String)MetadataUtils.getValue((PropertyValues)pvs, RANKING_PROP);
            this.ranking = Integer.valueOf(rank);
        } else {
            this.ranking = 0;
        }
        if (pvs.contains(SERVICE_NAME_PROP)) {
            String compName = (String)MetadataUtils.getValue((PropertyValues)pvs, SERVICE_NAME_PROP);
            this.component = new SimpleRefMetadata(compName);
        } else {
            this.component = (Target)ValueFactory.buildValue(MetadataUtils.getValue((PropertyValues)pvs, SERVICE_INSTANCE_PROP));
        }
        Object value = MetadataUtils.getValue((PropertyValues)pvs, INTERFACES_PROP);
        if (value != null) {
            ArrayList<String> intfs = new ArrayList<String>(4);
            if (value instanceof String) {
                intfs.add((String)value);
            } else if (value instanceof Collection) {
                Collection values = (Collection)value;
                for (TypedStringValue tsv : values) {
                    intfs.add(tsv.getValue());
                }
            }
            this.interfaces = Collections.unmodifiableList(intfs);
        } else {
            this.interfaces = Collections.emptyList();
        }
        if (pvs.contains(SERVICE_PROPERTIES_PROP)) {
            Map props = (Map)MetadataUtils.getValue((PropertyValues)pvs, SERVICE_PROPERTIES_PROP);
            this.serviceProperties = ValueFactory.getEntries(props);
        } else {
            this.serviceProperties = Collections.emptyList();
        }
        ArrayList<SimpleRegistrationListener> foundListeners = new ArrayList<SimpleRegistrationListener>(4);
        List listenerDefinitions = (List)MetadataUtils.getValue((PropertyValues)pvs, LISTENERS_PROP);
        if (listenerDefinitions != null) {
            for (AbstractBeanDefinition beanDef : listenerDefinitions) {
                foundListeners.add(new SimpleRegistrationListener(beanDef));
            }
        }
        this.listeners = Collections.unmodifiableCollection(foundListeners);
        Boolean bool = (Boolean)MetadataUtils.getValue((PropertyValues)pvs, LAZY_LISTENERS);
        this.activation = bool != null ? (bool.booleanValue() ? 2 : 1) : super.getActivation();
    }

    @Override
    public int getAutoExport() {
        return this.autoExport;
    }

    @Override
    public List<String> getInterfaces() {
        return this.interfaces;
    }

    @Override
    public int getRanking() {
        return this.ranking;
    }

    @Override
    public Collection<RegistrationListener> getRegistrationListeners() {
        return this.listeners;
    }

    @Override
    public Target getServiceComponent() {
        return this.component;
    }

    @Override
    public List<MapEntry> getServiceProperties() {
        return this.serviceProperties;
    }

    @Override
    public int getActivation() {
        return this.activation;
    }
}

