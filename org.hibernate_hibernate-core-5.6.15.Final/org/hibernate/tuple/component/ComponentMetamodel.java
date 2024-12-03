/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple.component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Property;
import org.hibernate.tuple.PropertyFactory;
import org.hibernate.tuple.StandardProperty;
import org.hibernate.tuple.component.ComponentTuplizer;
import org.hibernate.tuple.component.ComponentTuplizerFactory;

public class ComponentMetamodel
implements Serializable {
    private final String role;
    private final boolean isKey;
    private final StandardProperty[] properties;
    private final EntityMode entityMode;
    private final ComponentTuplizer componentTuplizer;
    private final int propertySpan;
    private final Map propertyIndexes = new HashMap();
    private final boolean createEmptyCompositesEnabled;

    @Deprecated
    public ComponentMetamodel(Component component, MetadataBuildingOptions metadataBuildingOptions) {
        this(component, new ComponentTuplizerFactory(metadataBuildingOptions));
    }

    public ComponentMetamodel(Component component, BootstrapContext bootstrapContext) {
        this(component, new ComponentTuplizerFactory(bootstrapContext));
    }

    private ComponentMetamodel(Component component, ComponentTuplizerFactory componentTuplizerFactory) {
        this.role = component.getRoleName();
        this.isKey = component.isKey();
        this.propertySpan = component.getPropertySpan();
        this.properties = new StandardProperty[this.propertySpan];
        Iterator itr = component.getPropertyIterator();
        int i = 0;
        while (itr.hasNext()) {
            Property property = (Property)itr.next();
            this.properties[i] = PropertyFactory.buildStandardProperty(property, false);
            this.propertyIndexes.put(property.getName(), i);
            ++i;
        }
        this.entityMode = component.hasPojoRepresentation() ? EntityMode.POJO : EntityMode.MAP;
        String tuplizerClassName = component.getTuplizerImplClassName(this.entityMode);
        this.componentTuplizer = tuplizerClassName == null ? componentTuplizerFactory.constructDefaultTuplizer(this.entityMode, component) : componentTuplizerFactory.constructTuplizer(tuplizerClassName, component);
        ConfigurationService cs = component.getMetadata().getMetadataBuildingOptions().getServiceRegistry().getService(ConfigurationService.class);
        this.createEmptyCompositesEnabled = ConfigurationHelper.getBoolean("hibernate.create_empty_composites.enabled", cs.getSettings(), false);
    }

    public boolean isKey() {
        return this.isKey;
    }

    public int getPropertySpan() {
        return this.propertySpan;
    }

    public StandardProperty[] getProperties() {
        return this.properties;
    }

    public StandardProperty getProperty(int index) {
        if (index < 0 || index >= this.propertySpan) {
            throw new IllegalArgumentException("illegal index value for component property access [request=" + index + ", span=" + this.propertySpan + "]");
        }
        return this.properties[index];
    }

    public int getPropertyIndex(String propertyName) {
        Integer index = (Integer)this.propertyIndexes.get(propertyName);
        if (index == null) {
            throw new HibernateException("component does not contain such a property [" + propertyName + "]");
        }
        return index;
    }

    public StandardProperty getProperty(String propertyName) {
        return this.getProperty(this.getPropertyIndex(propertyName));
    }

    public EntityMode getEntityMode() {
        return this.entityMode;
    }

    public ComponentTuplizer getComponentTuplizer() {
        return this.componentTuplizer;
    }

    public boolean isCreateEmptyCompositesEnabled() {
        return this.createEmptyCompositesEnabled;
    }
}

