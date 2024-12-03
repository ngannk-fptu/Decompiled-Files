/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple.entity;

import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
import org.hibernate.bytecode.spi.ReflectionOptimizer;
import org.hibernate.engine.internal.ManagedTypeHelper;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tuple.PojoInstantiator;
import org.hibernate.tuple.entity.EntityMetamodel;

public class PojoEntityInstantiator
extends PojoInstantiator {
    private final EntityMetamodel entityMetamodel;
    private final Class proxyInterface;
    private final boolean applyBytecodeInterception;

    public PojoEntityInstantiator(EntityMetamodel entityMetamodel, PersistentClass persistentClass, ReflectionOptimizer.InstantiationOptimizer optimizer) {
        super(persistentClass.getMappedClass(), optimizer, persistentClass.hasEmbeddedIdentifier());
        this.entityMetamodel = entityMetamodel;
        this.proxyInterface = persistentClass.getProxyInterface();
        this.applyBytecodeInterception = ManagedTypeHelper.isPersistentAttributeInterceptableType(persistentClass.getMappedClass());
    }

    @Override
    protected Object applyInterception(Object entity) {
        if (!this.applyBytecodeInterception) {
            return entity;
        }
        LazyAttributeLoadingInterceptor interceptor = new LazyAttributeLoadingInterceptor(this.entityMetamodel.getName(), null, this.entityMetamodel.getBytecodeEnhancementMetadata().getLazyAttributesMetadata().getLazyAttributeNames(), null);
        ManagedTypeHelper.asPersistentAttributeInterceptable(entity).$$_hibernate_setInterceptor(interceptor);
        return entity;
    }

    @Override
    public boolean isInstance(Object object) {
        return super.isInstance(object) || this.proxyInterface != null && this.proxyInterface.isInstance(object);
    }
}

