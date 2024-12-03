/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.OverridingClassLoader
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.instrument.classloading.websphere;

import java.lang.instrument.ClassFileTransformer;
import org.springframework.core.OverridingClassLoader;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.instrument.classloading.websphere.WebSphereClassLoaderAdapter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class WebSphereLoadTimeWeaver
implements LoadTimeWeaver {
    private final WebSphereClassLoaderAdapter classLoader;

    public WebSphereLoadTimeWeaver() {
        this(ClassUtils.getDefaultClassLoader());
    }

    public WebSphereLoadTimeWeaver(@Nullable ClassLoader classLoader) {
        Assert.notNull((Object)classLoader, (String)"ClassLoader must not be null");
        this.classLoader = new WebSphereClassLoaderAdapter(classLoader);
    }

    @Override
    public void addTransformer(ClassFileTransformer transformer) {
        this.classLoader.addTransformer(transformer);
    }

    @Override
    public ClassLoader getInstrumentableClassLoader() {
        return this.classLoader.getClassLoader();
    }

    @Override
    public ClassLoader getThrowawayClassLoader() {
        return new OverridingClassLoader(this.classLoader.getClassLoader(), this.classLoader.getThrowawayClassLoader());
    }
}

