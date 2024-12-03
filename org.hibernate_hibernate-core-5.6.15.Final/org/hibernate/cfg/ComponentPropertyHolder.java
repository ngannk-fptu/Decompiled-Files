/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Convert
 *  javax.persistence.Converts
 *  javax.persistence.EmbeddedId
 *  javax.persistence.Id
 *  javax.persistence.JoinColumn
 *  javax.persistence.JoinTable
 *  org.hibernate.annotations.common.reflection.XAnnotatedElement
 *  org.hibernate.annotations.common.reflection.XClass
 *  org.hibernate.annotations.common.reflection.XProperty
 */
package org.hibernate.cfg;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converts;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import org.hibernate.AnnotationException;
import org.hibernate.annotations.common.reflection.XAnnotatedElement;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.AbstractPropertyHolder;
import org.hibernate.cfg.AttributeConversionInfo;
import org.hibernate.cfg.CollectionPropertyHolder;
import org.hibernate.cfg.Ejb3Column;
import org.hibernate.cfg.PropertyData;
import org.hibernate.cfg.PropertyHolder;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;

public class ComponentPropertyHolder
extends AbstractPropertyHolder {
    private Component component;
    private boolean isOrWithinEmbeddedId;
    private boolean isWithinElementCollection;
    private String embeddedAttributeName;
    private Map<String, AttributeConversionInfo> attributeConversionInfoMap;

    public ComponentPropertyHolder(Component component, String path, PropertyData inferredData, PropertyHolder parent, MetadataBuildingContext context) {
        super(path, parent, inferredData.getPropertyClass(), context);
        XProperty embeddedXProperty = inferredData.getProperty();
        this.setCurrentProperty(embeddedXProperty);
        this.component = component;
        this.isOrWithinEmbeddedId = parent.isOrWithinEmbeddedId() || embeddedXProperty != null && (embeddedXProperty.isAnnotationPresent(Id.class) || embeddedXProperty.isAnnotationPresent(EmbeddedId.class));
        boolean bl = this.isWithinElementCollection = parent.isWithinElementCollection() || parent instanceof CollectionPropertyHolder;
        if (embeddedXProperty != null) {
            this.embeddedAttributeName = embeddedXProperty.getName();
            this.attributeConversionInfoMap = this.processAttributeConversions(embeddedXProperty);
        } else {
            this.embeddedAttributeName = "";
            this.attributeConversionInfoMap = this.processAttributeConversions(inferredData.getClassOrElement());
        }
    }

    private Map<String, AttributeConversionInfo> processAttributeConversions(XProperty embeddedXProperty) {
        Converts convertsAnnotation;
        HashMap<String, AttributeConversionInfo> infoMap = new HashMap<String, AttributeConversionInfo>();
        XClass embeddableXClass = embeddedXProperty.getType();
        this.processAttributeConversions(embeddableXClass, infoMap);
        Convert convertAnnotation = (Convert)embeddedXProperty.getAnnotation(Convert.class);
        if (convertAnnotation != null) {
            AttributeConversionInfo info = new AttributeConversionInfo(convertAnnotation, (XAnnotatedElement)embeddableXClass);
            if (StringHelper.isEmpty(info.getAttributeName())) {
                throw new IllegalStateException("Convert placed on Embedded attribute must define (sub)attributeName");
            }
            infoMap.put(info.getAttributeName(), info);
        }
        if ((convertsAnnotation = (Converts)embeddedXProperty.getAnnotation(Converts.class)) != null) {
            for (Convert convertAnnotation2 : convertsAnnotation.value()) {
                AttributeConversionInfo info = new AttributeConversionInfo(convertAnnotation2, (XAnnotatedElement)embeddableXClass);
                if (StringHelper.isEmpty(info.getAttributeName())) {
                    throw new IllegalStateException("Convert placed on Embedded attribute must define (sub)attributeName");
                }
                infoMap.put(info.getAttributeName(), info);
            }
        }
        return infoMap;
    }

    private void processAttributeConversions(XClass embeddableXClass, Map<String, AttributeConversionInfo> infoMap) {
        Converts convertsAnnotation;
        Convert convertAnnotation = (Convert)embeddableXClass.getAnnotation(Convert.class);
        if (convertAnnotation != null) {
            AttributeConversionInfo info = new AttributeConversionInfo(convertAnnotation, (XAnnotatedElement)embeddableXClass);
            if (StringHelper.isEmpty(info.getAttributeName())) {
                throw new IllegalStateException("@Convert placed on @Embeddable must define attributeName");
            }
            infoMap.put(info.getAttributeName(), info);
        }
        if ((convertsAnnotation = (Converts)embeddableXClass.getAnnotation(Converts.class)) != null) {
            for (Convert convertAnnotation2 : convertsAnnotation.value()) {
                AttributeConversionInfo info = new AttributeConversionInfo(convertAnnotation2, (XAnnotatedElement)embeddableXClass);
                if (StringHelper.isEmpty(info.getAttributeName())) {
                    throw new IllegalStateException("@Converts placed on @Embeddable must define attributeName");
                }
                infoMap.put(info.getAttributeName(), info);
            }
        }
    }

    private Map<String, AttributeConversionInfo> processAttributeConversions(XClass embeddableXClass) {
        HashMap<String, AttributeConversionInfo> infoMap = new HashMap<String, AttributeConversionInfo>();
        this.processAttributeConversions(embeddableXClass, infoMap);
        return infoMap;
    }

    @Override
    protected String normalizeCompositePath(String attributeName) {
        return this.embeddedAttributeName + '.' + attributeName;
    }

    @Override
    protected String normalizeCompositePathForLogging(String attributeName) {
        return this.normalizeCompositePath(attributeName);
    }

    @Override
    public void startingProperty(XProperty property) {
        Converts convertsAnnotation;
        if (property == null) {
            return;
        }
        String path = this.embeddedAttributeName + '.' + property.getName();
        if (this.attributeConversionInfoMap.containsKey(path)) {
            return;
        }
        Convert convertAnnotation = (Convert)property.getAnnotation(Convert.class);
        if (convertAnnotation != null) {
            AttributeConversionInfo info = new AttributeConversionInfo(convertAnnotation, (XAnnotatedElement)property);
            this.attributeConversionInfoMap.put(property.getName(), info);
        }
        if ((convertsAnnotation = (Converts)property.getAnnotation(Converts.class)) != null) {
            for (Convert convertAnnotation2 : convertsAnnotation.value()) {
                AttributeConversionInfo info = new AttributeConversionInfo(convertAnnotation2, (XAnnotatedElement)property);
                this.attributeConversionInfoMap.put(property.getName(), info);
            }
        }
    }

    @Override
    protected AttributeConversionInfo locateAttributeConversionInfo(XProperty property) {
        String propertyName = property.getName();
        AttributeConversionInfo conversion = this.locateAttributeConversionInfo(propertyName);
        if (conversion != null) {
            return conversion;
        }
        return null;
    }

    @Override
    protected AttributeConversionInfo locateAttributeConversionInfo(String path) {
        String embeddedPath = StringHelper.qualifyConditionally(this.embeddedAttributeName, path);
        AttributeConversionInfo fromParent = this.parent.locateAttributeConversionInfo(embeddedPath);
        if (fromParent != null) {
            return fromParent;
        }
        AttributeConversionInfo fromEmbedded = this.attributeConversionInfoMap.get(embeddedPath);
        if (fromEmbedded != null) {
            return fromEmbedded;
        }
        return this.attributeConversionInfoMap.get(path);
    }

    @Override
    public String getEntityName() {
        return this.component.getComponentClassName();
    }

    @Override
    public void addProperty(Property prop, Ejb3Column[] columns, XClass declaringClass) {
        Table table;
        if (columns != null && !(table = columns[0].getTable()).equals(this.component.getTable())) {
            if (this.component.getPropertySpan() == 0) {
                this.component.setTable(table);
            } else {
                throw new AnnotationException("A component cannot hold properties split into 2 different tables: " + this.getPath());
            }
        }
        this.addProperty(prop, declaringClass);
    }

    @Override
    public Join addJoin(JoinTable joinTableAnn, boolean noDelayInPkColumnCreation) {
        return this.parent.addJoin(joinTableAnn, noDelayInPkColumnCreation);
    }

    @Override
    public String getClassName() {
        return this.component.getComponentClassName();
    }

    @Override
    public String getEntityOwnerClassName() {
        return this.component.getOwner().getClassName();
    }

    @Override
    public Table getTable() {
        return this.component.getTable();
    }

    @Override
    public void addProperty(Property prop, XClass declaringClass) {
        this.component.addProperty(prop);
    }

    @Override
    public KeyValue getIdentifier() {
        return this.component.getOwner().getIdentifier();
    }

    @Override
    public boolean isOrWithinEmbeddedId() {
        return this.isOrWithinEmbeddedId;
    }

    @Override
    public boolean isWithinElementCollection() {
        return this.isWithinElementCollection;
    }

    @Override
    public PersistentClass getPersistentClass() {
        return this.component.getOwner();
    }

    @Override
    public boolean isComponent() {
        return true;
    }

    @Override
    public boolean isEntity() {
        return false;
    }

    @Override
    public void setParentProperty(String parentProperty) {
        this.component.setParentProperty(parentProperty);
    }

    @Override
    public Column[] getOverriddenColumn(String propertyName) {
        String userPropertyName;
        Column[] result = super.getOverriddenColumn(propertyName);
        if (result == null && (userPropertyName = this.extractUserPropertyName("id", propertyName)) != null) {
            result = super.getOverriddenColumn(userPropertyName);
        }
        if (result == null && (userPropertyName = this.extractUserPropertyName("_identifierMapper", propertyName)) != null) {
            result = super.getOverriddenColumn(userPropertyName);
        }
        return result;
    }

    private String extractUserPropertyName(String redundantString, String propertyName) {
        String result = null;
        String className = this.component.getOwner().getClassName();
        if (propertyName.startsWith(className) && propertyName.length() > className.length() + 2 + redundantString.length() && propertyName.substring(className.length() + 1, className.length() + 1 + redundantString.length()).equals(redundantString)) {
            result = className + propertyName.substring(className.length() + 1 + redundantString.length());
        }
        return result;
    }

    @Override
    public JoinColumn[] getOverriddenJoinColumn(String propertyName) {
        return super.getOverriddenJoinColumn(propertyName);
    }

    public String toString() {
        return super.toString() + "(" + this.parent.normalizeCompositePathForLogging(this.embeddedAttributeName) + ")";
    }
}

