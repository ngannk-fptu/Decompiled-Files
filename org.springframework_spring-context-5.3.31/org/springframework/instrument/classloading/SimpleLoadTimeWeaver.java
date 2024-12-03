/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.instrument.classloading;

import java.lang.instrument.ClassFileTransformer;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.instrument.classloading.SimpleInstrumentableClassLoader;
import org.springframework.instrument.classloading.SimpleThrowawayClassLoader;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class SimpleLoadTimeWeaver
implements LoadTimeWeaver {
    private final SimpleInstrumentableClassLoader classLoader;

    public SimpleLoadTimeWeaver() {
        this.classLoader = new SimpleInstrumentableClassLoader(ClassUtils.getDefaultClassLoader());
    }

    public SimpleLoadTimeWeaver(SimpleInstrumentableClassLoader classLoader) {
        Assert.notNull((Object)((Object)classLoader), (String)"ClassLoader must not be null");
        this.classLoader = classLoader;
    }

    @Override
    public void addTransformer(ClassFileTransformer transformer) {
        this.classLoader.addTransformer(transformer);
    }

    @Override
    public ClassLoader getInstrumentableClassLoader() {
        return this.classLoader;
    }

    @Override
    public ClassLoader getThrowawayClassLoader() {
        return new SimpleThrowawayClassLoader(this.getInstrumentableClassLoader());
    }
}

