/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.instrument.classloading.weblogic;

import java.lang.instrument.ClassFileTransformer;
import org.springframework.core.OverridingClassLoader;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.instrument.classloading.weblogic.WebLogicClassLoaderAdapter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class WebLogicLoadTimeWeaver
implements LoadTimeWeaver {
    private final WebLogicClassLoaderAdapter classLoader;

    public WebLogicLoadTimeWeaver() {
        this(ClassUtils.getDefaultClassLoader());
    }

    public WebLogicLoadTimeWeaver(@Nullable ClassLoader classLoader) {
        Assert.notNull((Object)classLoader, "ClassLoader must not be null");
        this.classLoader = new WebLogicClassLoaderAdapter(classLoader);
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

