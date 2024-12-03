/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.aspectj.annotation;

import java.io.Serializable;
import org.springframework.aop.aspectj.annotation.AspectMetadata;
import org.springframework.aop.aspectj.annotation.MetadataAwareAspectInstanceFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class LazySingletonAspectInstanceFactoryDecorator
implements MetadataAwareAspectInstanceFactory,
Serializable {
    private final MetadataAwareAspectInstanceFactory maaif;
    @Nullable
    private volatile Object materialized;

    public LazySingletonAspectInstanceFactoryDecorator(MetadataAwareAspectInstanceFactory maaif) {
        Assert.notNull((Object)maaif, "AspectInstanceFactory must not be null");
        this.maaif = maaif;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getAspectInstance() {
        Object aspectInstance = this.materialized;
        if (aspectInstance == null) {
            Object mutex = this.maaif.getAspectCreationMutex();
            if (mutex == null) {
                this.materialized = aspectInstance = this.maaif.getAspectInstance();
            } else {
                Object object = mutex;
                synchronized (object) {
                    aspectInstance = this.materialized;
                    if (aspectInstance == null) {
                        this.materialized = aspectInstance = this.maaif.getAspectInstance();
                    }
                }
            }
        }
        return aspectInstance;
    }

    public boolean isMaterialized() {
        return this.materialized != null;
    }

    @Override
    @Nullable
    public ClassLoader getAspectClassLoader() {
        return this.maaif.getAspectClassLoader();
    }

    @Override
    public AspectMetadata getAspectMetadata() {
        return this.maaif.getAspectMetadata();
    }

    @Override
    @Nullable
    public Object getAspectCreationMutex() {
        return this.maaif.getAspectCreationMutex();
    }

    @Override
    public int getOrder() {
        return this.maaif.getOrder();
    }

    public String toString() {
        return "LazySingletonAspectInstanceFactoryDecorator: decorating " + this.maaif;
    }
}

