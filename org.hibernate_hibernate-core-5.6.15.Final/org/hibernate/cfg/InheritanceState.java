/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Access
 *  javax.persistence.EmbeddedId
 *  javax.persistence.Entity
 *  javax.persistence.Id
 *  javax.persistence.IdClass
 *  javax.persistence.Inheritance
 *  javax.persistence.InheritanceType
 *  javax.persistence.MappedSuperclass
 *  org.hibernate.annotations.common.reflection.XClass
 *  org.hibernate.annotations.common.reflection.XProperty
 */
package org.hibernate.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.Access;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import org.hibernate.AnnotationException;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.AccessType;
import org.hibernate.cfg.AnnotationBinder;
import org.hibernate.cfg.PropertyContainer;
import org.hibernate.cfg.PropertyData;
import org.hibernate.cfg.annotations.EntityBinder;
import org.hibernate.mapping.PersistentClass;

public class InheritanceState {
    private XClass clazz;
    private boolean hasSiblings = false;
    private boolean hasParents = false;
    private InheritanceType type;
    private boolean isEmbeddableSuperclass = false;
    private Map<XClass, InheritanceState> inheritanceStatePerClass;
    private List<XClass> classesToProcessForMappedSuperclass = new ArrayList<XClass>();
    private MetadataBuildingContext buildingContext;
    private AccessType accessType;
    private ElementsToProcess elementsToProcess;
    private Boolean hasIdClassOrEmbeddedId;

    public InheritanceState(XClass clazz, Map<XClass, InheritanceState> inheritanceStatePerClass, MetadataBuildingContext buildingContext) {
        this.setClazz(clazz);
        this.buildingContext = buildingContext;
        this.inheritanceStatePerClass = inheritanceStatePerClass;
        this.extractInheritanceType();
    }

    private void extractInheritanceType() {
        XClass element = this.getClazz();
        Inheritance inhAnn = (Inheritance)element.getAnnotation(Inheritance.class);
        MappedSuperclass mappedSuperClass = (MappedSuperclass)element.getAnnotation(MappedSuperclass.class);
        if (mappedSuperClass != null) {
            this.setEmbeddableSuperclass(true);
            this.setType(inhAnn == null ? null : inhAnn.strategy());
        } else {
            this.setType(inhAnn == null ? InheritanceType.SINGLE_TABLE : inhAnn.strategy());
        }
    }

    boolean hasTable() {
        return !this.hasParents() || !InheritanceType.SINGLE_TABLE.equals((Object)this.getType());
    }

    boolean hasDenormalizedTable() {
        return this.hasParents() && InheritanceType.TABLE_PER_CLASS.equals((Object)this.getType());
    }

    public static InheritanceState getInheritanceStateOfSuperEntity(XClass clazz, Map<XClass, InheritanceState> states) {
        XClass superclass = clazz;
        do {
            InheritanceState currentState;
            if ((currentState = states.get(superclass = superclass.getSuperclass())) == null || currentState.isEmbeddableSuperclass()) continue;
            return currentState;
        } while (superclass != null && !Object.class.getName().equals(superclass.getName()));
        return null;
    }

    public static InheritanceState getSuperclassInheritanceState(XClass clazz, Map<XClass, InheritanceState> states) {
        XClass superclass = clazz;
        do {
            InheritanceState currentState;
            if ((currentState = states.get(superclass = superclass.getSuperclass())) == null) continue;
            return currentState;
        } while (superclass != null && !Object.class.getName().equals(superclass.getName()));
        return null;
    }

    public XClass getClazz() {
        return this.clazz;
    }

    public void setClazz(XClass clazz) {
        this.clazz = clazz;
    }

    public boolean hasSiblings() {
        return this.hasSiblings;
    }

    public void setHasSiblings(boolean hasSiblings) {
        this.hasSiblings = hasSiblings;
    }

    public boolean hasParents() {
        return this.hasParents;
    }

    public void setHasParents(boolean hasParents) {
        this.hasParents = hasParents;
    }

    public InheritanceType getType() {
        return this.type;
    }

    public void setType(InheritanceType type) {
        this.type = type;
    }

    public boolean isEmbeddableSuperclass() {
        return this.isEmbeddableSuperclass;
    }

    public void setEmbeddableSuperclass(boolean embeddableSuperclass) {
        this.isEmbeddableSuperclass = embeddableSuperclass;
    }

    void postProcess(PersistentClass persistenceClass, EntityBinder entityBinder) {
        this.getElementsToProcess();
        this.addMappedSuperClassInMetadata(persistenceClass);
        entityBinder.setPropertyAccessType(this.accessType);
    }

    public XClass getClassWithIdClass(boolean evenIfSubclass) {
        if (!evenIfSubclass && this.hasParents()) {
            return null;
        }
        if (this.clazz.isAnnotationPresent(IdClass.class)) {
            return this.clazz;
        }
        InheritanceState state = InheritanceState.getSuperclassInheritanceState(this.clazz, this.inheritanceStatePerClass);
        if (state != null) {
            return state.getClassWithIdClass(true);
        }
        return null;
    }

    public Boolean hasIdClassOrEmbeddedId() {
        if (this.hasIdClassOrEmbeddedId == null) {
            this.hasIdClassOrEmbeddedId = false;
            if (this.getClassWithIdClass(true) != null) {
                this.hasIdClassOrEmbeddedId = true;
            } else {
                ElementsToProcess process = this.getElementsToProcess();
                for (PropertyData property : process.getElements()) {
                    if (!property.getProperty().isAnnotationPresent(EmbeddedId.class)) continue;
                    this.hasIdClassOrEmbeddedId = true;
                    break;
                }
            }
        }
        return this.hasIdClassOrEmbeddedId;
    }

    public ElementsToProcess getElementsToProcess() {
        if (this.elementsToProcess == null) {
            InheritanceState inheritanceState = this.inheritanceStatePerClass.get(this.clazz);
            assert (!inheritanceState.isEmbeddableSuperclass());
            this.getMappedSuperclassesTillNextEntityOrdered();
            this.accessType = this.determineDefaultAccessType();
            ArrayList<PropertyData> elements = new ArrayList<PropertyData>();
            int idPropertyCount = 0;
            for (XClass classToProcessForMappedSuperclass : this.classesToProcessForMappedSuperclass) {
                PropertyContainer propertyContainer = new PropertyContainer(classToProcessForMappedSuperclass, this.clazz, this.accessType);
                int currentIdPropertyCount = AnnotationBinder.addElementsOfClass(elements, propertyContainer, this.buildingContext);
                idPropertyCount += currentIdPropertyCount;
            }
            if (idPropertyCount == 0 && !inheritanceState.hasParents()) {
                throw new AnnotationException("No identifier specified for entity: " + this.clazz.getName());
            }
            elements.trimToSize();
            this.elementsToProcess = new ElementsToProcess(elements, idPropertyCount);
        }
        return this.elementsToProcess;
    }

    private AccessType determineDefaultAccessType() {
        XClass xclass;
        for (xclass = this.clazz; xclass != null; xclass = xclass.getSuperclass()) {
            if (xclass.getSuperclass() != null && !Object.class.getName().equals(xclass.getSuperclass().getName()) || !xclass.isAnnotationPresent(Entity.class) && !xclass.isAnnotationPresent(MappedSuperclass.class) || !xclass.isAnnotationPresent(Access.class)) continue;
            return AccessType.getAccessStrategy(((Access)xclass.getAnnotation(Access.class)).value());
        }
        for (xclass = this.clazz; xclass != null && !Object.class.getName().equals(xclass.getName()); xclass = xclass.getSuperclass()) {
            boolean isEmbeddedId;
            if (!xclass.isAnnotationPresent(Entity.class) && !xclass.isAnnotationPresent(MappedSuperclass.class)) continue;
            for (XProperty prop : xclass.getDeclaredProperties(AccessType.PROPERTY.getType())) {
                isEmbeddedId = prop.isAnnotationPresent(EmbeddedId.class);
                if (!prop.isAnnotationPresent(Id.class) && !isEmbeddedId) continue;
                return AccessType.PROPERTY;
            }
            for (XProperty prop : xclass.getDeclaredProperties(AccessType.FIELD.getType())) {
                isEmbeddedId = prop.isAnnotationPresent(EmbeddedId.class);
                if (!prop.isAnnotationPresent(Id.class) && !isEmbeddedId) continue;
                return AccessType.FIELD;
            }
        }
        throw new AnnotationException("No identifier specified for entity: " + this.clazz);
    }

    private void getMappedSuperclassesTillNextEntityOrdered() {
        InheritanceState superclassState;
        XClass currentClassInHierarchy = this.clazz;
        do {
            this.classesToProcessForMappedSuperclass.add(0, currentClassInHierarchy);
            XClass superClass = currentClassInHierarchy;
            do {
                superClass = superClass.getSuperclass();
                superclassState = this.inheritanceStatePerClass.get(superClass);
            } while (superClass != null && !this.buildingContext.getBootstrapContext().getReflectionManager().equals(superClass, Object.class) && superclassState == null);
            currentClassInHierarchy = superClass;
        } while (superclassState != null && superclassState.isEmbeddableSuperclass());
    }

    private void addMappedSuperClassInMetadata(PersistentClass persistentClass) {
        org.hibernate.mapping.MappedSuperclass mappedSuperclass = null;
        InheritanceState superEntityState = InheritanceState.getInheritanceStateOfSuperEntity(this.clazz, this.inheritanceStatePerClass);
        PersistentClass superEntity = superEntityState != null ? this.buildingContext.getMetadataCollector().getEntityBinding(superEntityState.getClazz().getName()) : null;
        int lastMappedSuperclass = this.classesToProcessForMappedSuperclass.size() - 1;
        for (int index = 0; index < lastMappedSuperclass; ++index) {
            org.hibernate.mapping.MappedSuperclass parentSuperclass = mappedSuperclass;
            Class type = this.buildingContext.getBootstrapContext().getReflectionManager().toClass(this.classesToProcessForMappedSuperclass.get(index));
            mappedSuperclass = this.buildingContext.getMetadataCollector().getMappedSuperclass(type);
            if (mappedSuperclass != null) continue;
            mappedSuperclass = new org.hibernate.mapping.MappedSuperclass(parentSuperclass, superEntity);
            mappedSuperclass.setMappedClass(type);
            this.buildingContext.getMetadataCollector().addMappedSuperclass(type, mappedSuperclass);
        }
        if (mappedSuperclass != null) {
            persistentClass.setSuperMappedSuperclass(mappedSuperclass);
        }
    }

    static final class ElementsToProcess {
        private final List<PropertyData> properties;
        private final int idPropertyCount;

        public List<PropertyData> getElements() {
            return this.properties;
        }

        public int getIdPropertyCount() {
            return this.idPropertyCount;
        }

        private ElementsToProcess(List<PropertyData> properties, int idPropertyCount) {
            this.properties = properties;
            this.idPropertyCount = idPropertyCount;
        }
    }
}

