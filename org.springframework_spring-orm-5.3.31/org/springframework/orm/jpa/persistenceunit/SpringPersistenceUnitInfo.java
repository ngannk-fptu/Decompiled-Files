/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.spi.ClassTransformer
 *  org.springframework.core.DecoratingClassLoader
 *  org.springframework.instrument.classloading.LoadTimeWeaver
 *  org.springframework.instrument.classloading.SimpleThrowawayClassLoader
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.orm.jpa.persistenceunit;

import java.lang.instrument.ClassFileTransformer;
import javax.persistence.spi.ClassTransformer;
import org.springframework.core.DecoratingClassLoader;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.instrument.classloading.SimpleThrowawayClassLoader;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.persistenceunit.ClassFileTransformerAdapter;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.util.Assert;

class SpringPersistenceUnitInfo
extends MutablePersistenceUnitInfo {
    @Nullable
    private LoadTimeWeaver loadTimeWeaver;
    @Nullable
    private ClassLoader classLoader;

    SpringPersistenceUnitInfo() {
    }

    public void init(LoadTimeWeaver loadTimeWeaver) {
        Assert.notNull((Object)loadTimeWeaver, (String)"LoadTimeWeaver must not be null");
        this.loadTimeWeaver = loadTimeWeaver;
        this.classLoader = loadTimeWeaver.getInstrumentableClassLoader();
    }

    public void init(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    @Nullable
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    @Override
    public void addTransformer(ClassTransformer classTransformer) {
        if (this.loadTimeWeaver == null) {
            throw new IllegalStateException("Cannot apply class transformer without LoadTimeWeaver specified");
        }
        this.loadTimeWeaver.addTransformer((ClassFileTransformer)new ClassFileTransformerAdapter(classTransformer));
    }

    @Override
    public ClassLoader getNewTempClassLoader() {
        Object tcl = this.loadTimeWeaver != null ? this.loadTimeWeaver.getThrowawayClassLoader() : new SimpleThrowawayClassLoader(this.classLoader);
        String packageToExclude = this.getPersistenceProviderPackageName();
        if (packageToExclude != null && tcl instanceof DecoratingClassLoader) {
            ((DecoratingClassLoader)tcl).excludePackage(packageToExclude);
        }
        return tcl;
    }
}

