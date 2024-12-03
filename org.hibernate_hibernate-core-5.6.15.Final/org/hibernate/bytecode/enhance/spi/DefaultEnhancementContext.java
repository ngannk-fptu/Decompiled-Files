/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Basic
 *  javax.persistence.ElementCollection
 *  javax.persistence.Embeddable
 *  javax.persistence.Entity
 *  javax.persistence.ManyToMany
 *  javax.persistence.MappedSuperclass
 *  javax.persistence.OneToMany
 *  javax.persistence.Transient
 */
package org.hibernate.bytecode.enhance.spi;

import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import org.hibernate.bytecode.enhance.spi.EnhancementContext;
import org.hibernate.bytecode.enhance.spi.UnloadedClass;
import org.hibernate.bytecode.enhance.spi.UnloadedField;

public class DefaultEnhancementContext
implements EnhancementContext {
    @Override
    public ClassLoader getLoadingClassLoader() {
        return this.getClass().getClassLoader();
    }

    @Override
    public boolean isEntityClass(UnloadedClass classDescriptor) {
        return classDescriptor.hasAnnotation(Entity.class);
    }

    @Override
    public boolean isCompositeClass(UnloadedClass classDescriptor) {
        return classDescriptor.hasAnnotation(Embeddable.class);
    }

    @Override
    public boolean isMappedSuperclassClass(UnloadedClass classDescriptor) {
        return classDescriptor.hasAnnotation(MappedSuperclass.class);
    }

    @Override
    public boolean doBiDirectionalAssociationManagement(UnloadedField field) {
        return true;
    }

    @Override
    public boolean doDirtyCheckingInline(UnloadedClass classDescriptor) {
        return true;
    }

    @Override
    public boolean doExtendedEnhancement(UnloadedClass classDescriptor) {
        return false;
    }

    @Override
    public boolean hasLazyLoadableAttributes(UnloadedClass classDescriptor) {
        return true;
    }

    @Override
    public boolean isLazyLoadable(UnloadedField field) {
        return true;
    }

    @Override
    public boolean isPersistentField(UnloadedField ctField) {
        return !ctField.hasAnnotation(Transient.class);
    }

    @Override
    public boolean isMappedCollection(UnloadedField field) {
        if (field.hasAnnotation(OneToMany.class) || field.hasAnnotation(ManyToMany.class) || field.hasAnnotation(ElementCollection.class)) {
            return true;
        }
        return !field.hasAnnotation(Basic.class);
    }

    @Override
    public UnloadedField[] order(UnloadedField[] persistentFields) {
        return persistentFields;
    }
}

