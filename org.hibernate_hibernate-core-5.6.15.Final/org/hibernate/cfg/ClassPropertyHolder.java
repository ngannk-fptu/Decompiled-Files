/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Convert
 *  javax.persistence.Converts
 *  javax.persistence.Embeddable
 *  javax.persistence.Entity
 *  javax.persistence.JoinTable
 *  javax.persistence.MappedSuperclass
 *  org.hibernate.annotations.common.reflection.XAnnotatedElement
 *  org.hibernate.annotations.common.reflection.XClass
 *  org.hibernate.annotations.common.reflection.XProperty
 */
package org.hibernate.cfg;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.Convert;
import javax.persistence.Converts;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.MappedSuperclass;
import org.hibernate.AssertionFailure;
import org.hibernate.annotations.common.reflection.XAnnotatedElement;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.AbstractPropertyHolder;
import org.hibernate.cfg.AttributeConversionInfo;
import org.hibernate.cfg.Ejb3Column;
import org.hibernate.cfg.InheritanceState;
import org.hibernate.cfg.annotations.EntityBinder;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;

public class ClassPropertyHolder
extends AbstractPropertyHolder {
    private PersistentClass persistentClass;
    private Map<String, Join> joins;
    private transient Map<String, Join> joinsPerRealTableName;
    private EntityBinder entityBinder;
    private final Map<XClass, InheritanceState> inheritanceStatePerClass;
    private Map<String, AttributeConversionInfo> attributeConversionInfoMap;

    public ClassPropertyHolder(PersistentClass persistentClass, XClass entityXClass, Map<String, Join> joins, MetadataBuildingContext context, Map<XClass, InheritanceState> inheritanceStatePerClass) {
        super(persistentClass.getEntityName(), null, entityXClass, context);
        this.persistentClass = persistentClass;
        this.joins = joins;
        this.inheritanceStatePerClass = inheritanceStatePerClass;
        this.attributeConversionInfoMap = this.buildAttributeConversionInfoMap(entityXClass);
    }

    public ClassPropertyHolder(PersistentClass persistentClass, XClass entityXClass, EntityBinder entityBinder, MetadataBuildingContext context, Map<XClass, InheritanceState> inheritanceStatePerClass) {
        this(persistentClass, entityXClass, entityBinder.getSecondaryTables(), context, inheritanceStatePerClass);
        this.entityBinder = entityBinder;
    }

    @Override
    protected String normalizeCompositePath(String attributeName) {
        return attributeName;
    }

    @Override
    protected String normalizeCompositePathForLogging(String attributeName) {
        return this.getEntityName() + '.' + attributeName;
    }

    protected Map<String, AttributeConversionInfo> buildAttributeConversionInfoMap(XClass entityXClass) {
        HashMap<String, AttributeConversionInfo> map = new HashMap<String, AttributeConversionInfo>();
        this.collectAttributeConversionInfo(map, entityXClass);
        return map;
    }

    private void collectAttributeConversionInfo(Map<String, AttributeConversionInfo> infoMap, XClass xClass) {
        Converts convertsAnnotation;
        boolean canContainConvert;
        if (xClass == null) {
            return;
        }
        this.collectAttributeConversionInfo(infoMap, xClass.getSuperclass());
        boolean bl = canContainConvert = xClass.isAnnotationPresent(Entity.class) || xClass.isAnnotationPresent(MappedSuperclass.class) || xClass.isAnnotationPresent(Embeddable.class);
        if (!canContainConvert) {
            return;
        }
        Convert convertAnnotation = (Convert)xClass.getAnnotation(Convert.class);
        if (convertAnnotation != null) {
            AttributeConversionInfo info = new AttributeConversionInfo(convertAnnotation, (XAnnotatedElement)xClass);
            if (StringHelper.isEmpty(info.getAttributeName())) {
                throw new IllegalStateException("@Convert placed on @Entity/@MappedSuperclass must define attributeName");
            }
            infoMap.put(info.getAttributeName(), info);
        }
        if ((convertsAnnotation = (Converts)xClass.getAnnotation(Converts.class)) != null) {
            for (Convert convertAnnotation2 : convertsAnnotation.value()) {
                AttributeConversionInfo info = new AttributeConversionInfo(convertAnnotation2, (XAnnotatedElement)xClass);
                if (StringHelper.isEmpty(info.getAttributeName())) {
                    throw new IllegalStateException("@Converts placed on @Entity/@MappedSuperclass must define attributeName");
                }
                infoMap.put(info.getAttributeName(), info);
            }
        }
    }

    @Override
    public void startingProperty(XProperty property) {
        Converts convertsAnnotation;
        if (property == null) {
            return;
        }
        String propertyName = property.getName();
        if (this.attributeConversionInfoMap.containsKey(propertyName)) {
            return;
        }
        Convert convertAnnotation = (Convert)property.getAnnotation(Convert.class);
        if (convertAnnotation != null) {
            AttributeConversionInfo info = new AttributeConversionInfo(convertAnnotation, (XAnnotatedElement)property);
            if (StringHelper.isEmpty(info.getAttributeName())) {
                this.attributeConversionInfoMap.put(propertyName, info);
            } else {
                this.attributeConversionInfoMap.put(propertyName + '.' + info.getAttributeName(), info);
            }
        }
        if ((convertsAnnotation = (Converts)property.getAnnotation(Converts.class)) != null) {
            for (Convert convertAnnotation2 : convertsAnnotation.value()) {
                AttributeConversionInfo info = new AttributeConversionInfo(convertAnnotation2, (XAnnotatedElement)property);
                if (StringHelper.isEmpty(info.getAttributeName())) {
                    this.attributeConversionInfoMap.put(propertyName, info);
                    continue;
                }
                this.attributeConversionInfoMap.put(propertyName + '.' + info.getAttributeName(), info);
            }
        }
    }

    @Override
    protected AttributeConversionInfo locateAttributeConversionInfo(XProperty property) {
        return this.locateAttributeConversionInfo(property.getName());
    }

    @Override
    protected AttributeConversionInfo locateAttributeConversionInfo(String path) {
        return this.attributeConversionInfoMap.get(path);
    }

    @Override
    public String getEntityName() {
        return this.persistentClass.getEntityName();
    }

    @Override
    public void addProperty(Property prop, Ejb3Column[] columns, XClass declaringClass) {
        if (columns != null && columns[0].isSecondary()) {
            Join join = columns[0].getJoin();
            this.addPropertyToJoin(prop, declaringClass, join);
        } else {
            this.addProperty(prop, declaringClass);
        }
    }

    @Override
    public void addProperty(Property prop, XClass declaringClass) {
        if (prop.getValue() instanceof Component) {
            String tableName = prop.getValue().getTable().getName();
            if (this.getJoinsPerRealTableName().containsKey(tableName)) {
                Join join = this.getJoinsPerRealTableName().get(tableName);
                this.addPropertyToJoin(prop, declaringClass, join);
            } else {
                this.addPropertyToPersistentClass(prop, declaringClass);
            }
        } else {
            this.addPropertyToPersistentClass(prop, declaringClass);
        }
    }

    @Override
    public Join addJoin(JoinTable joinTableAnn, boolean noDelayInPkColumnCreation) {
        Join join = this.entityBinder.addJoin(joinTableAnn, this, noDelayInPkColumnCreation);
        this.joins = this.entityBinder.getSecondaryTables();
        return join;
    }

    private void addPropertyToPersistentClass(Property prop, XClass declaringClass) {
        if (declaringClass != null) {
            InheritanceState inheritanceState = this.inheritanceStatePerClass.get(declaringClass);
            if (inheritanceState == null) {
                throw new AssertionFailure("Declaring class is not found in the inheritance state hierarchy: " + declaringClass);
            }
            if (inheritanceState.isEmbeddableSuperclass()) {
                this.persistentClass.addMappedsuperclassProperty(prop);
                this.addPropertyToMappedSuperclass(prop, declaringClass);
            } else {
                this.persistentClass.addProperty(prop);
            }
        } else {
            this.persistentClass.addProperty(prop);
        }
    }

    private void addPropertyToMappedSuperclass(Property prop, XClass declaringClass) {
        Class type = this.getContext().getBootstrapContext().getReflectionManager().toClass(declaringClass);
        org.hibernate.mapping.MappedSuperclass superclass = this.getContext().getMetadataCollector().getMappedSuperclass(type);
        superclass.addDeclaredProperty(prop);
    }

    private void addPropertyToJoin(Property prop, XClass declaringClass, Join join) {
        if (declaringClass != null) {
            InheritanceState inheritanceState = this.inheritanceStatePerClass.get(declaringClass);
            if (inheritanceState == null) {
                throw new AssertionFailure("Declaring class is not found in the inheritance state hierarchy: " + declaringClass);
            }
            if (inheritanceState.isEmbeddableSuperclass()) {
                join.addMappedsuperclassProperty(prop);
                this.addPropertyToMappedSuperclass(prop, declaringClass);
            } else {
                join.addProperty(prop);
            }
        } else {
            join.addProperty(prop);
        }
    }

    private Map<String, Join> getJoinsPerRealTableName() {
        if (this.joinsPerRealTableName == null) {
            this.joinsPerRealTableName = new HashMap<String, Join>(this.joins.size());
            for (Join join : this.joins.values()) {
                this.joinsPerRealTableName.put(join.getTable().getName(), join);
            }
        }
        return this.joinsPerRealTableName;
    }

    @Override
    public String getClassName() {
        return this.persistentClass.getClassName();
    }

    @Override
    public String getEntityOwnerClassName() {
        return this.getClassName();
    }

    @Override
    public Table getTable() {
        return this.persistentClass.getTable();
    }

    @Override
    public boolean isComponent() {
        return false;
    }

    @Override
    public boolean isEntity() {
        return true;
    }

    @Override
    public PersistentClass getPersistentClass() {
        return this.persistentClass;
    }

    @Override
    public KeyValue getIdentifier() {
        return this.persistentClass.getIdentifier();
    }

    @Override
    public boolean isOrWithinEmbeddedId() {
        return false;
    }

    @Override
    public boolean isWithinElementCollection() {
        return false;
    }

    public String toString() {
        return super.toString() + "(" + this.getEntityName() + ")";
    }
}

