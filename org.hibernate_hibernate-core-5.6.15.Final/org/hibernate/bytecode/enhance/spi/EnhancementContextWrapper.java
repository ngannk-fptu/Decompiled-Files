/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.enhance.spi;

import org.hibernate.bytecode.enhance.spi.EnhancementContext;
import org.hibernate.bytecode.enhance.spi.UnloadedClass;
import org.hibernate.bytecode.enhance.spi.UnloadedField;

public class EnhancementContextWrapper
implements EnhancementContext {
    private final ClassLoader loadingClassloader;
    private final EnhancementContext wrappedContext;

    public EnhancementContextWrapper(EnhancementContext wrappedContext, ClassLoader loadingClassloader) {
        this.wrappedContext = wrappedContext;
        this.loadingClassloader = loadingClassloader;
    }

    @Override
    public ClassLoader getLoadingClassLoader() {
        return this.loadingClassloader;
    }

    @Override
    public boolean isEntityClass(UnloadedClass classDescriptor) {
        return this.wrappedContext.isEntityClass(classDescriptor);
    }

    @Override
    public boolean isCompositeClass(UnloadedClass classDescriptor) {
        return this.wrappedContext.isCompositeClass(classDescriptor);
    }

    @Override
    public boolean isMappedSuperclassClass(UnloadedClass classDescriptor) {
        return this.wrappedContext.isMappedSuperclassClass(classDescriptor);
    }

    @Override
    public boolean doBiDirectionalAssociationManagement(UnloadedField field) {
        return this.wrappedContext.doBiDirectionalAssociationManagement(field);
    }

    @Override
    public boolean doDirtyCheckingInline(UnloadedClass classDescriptor) {
        return this.wrappedContext.doDirtyCheckingInline(classDescriptor);
    }

    @Override
    public boolean doExtendedEnhancement(UnloadedClass classDescriptor) {
        return this.wrappedContext.doExtendedEnhancement(classDescriptor);
    }

    @Override
    public boolean hasLazyLoadableAttributes(UnloadedClass classDescriptor) {
        return this.wrappedContext.hasLazyLoadableAttributes(classDescriptor);
    }

    @Override
    public boolean isPersistentField(UnloadedField ctField) {
        return this.wrappedContext.isPersistentField(ctField);
    }

    @Override
    public UnloadedField[] order(UnloadedField[] persistentFields) {
        return this.wrappedContext.order(persistentFields);
    }

    @Override
    public boolean isLazyLoadable(UnloadedField field) {
        return this.wrappedContext.isLazyLoadable(field);
    }

    @Override
    public boolean isMappedCollection(UnloadedField field) {
        return this.wrappedContext.isMappedCollection(field);
    }
}

