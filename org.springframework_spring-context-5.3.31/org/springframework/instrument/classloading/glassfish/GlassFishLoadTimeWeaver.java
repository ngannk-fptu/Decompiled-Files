/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.OverridingClassLoader
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.instrument.classloading.glassfish;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.core.OverridingClassLoader;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class GlassFishLoadTimeWeaver
implements LoadTimeWeaver {
    private static final String INSTRUMENTABLE_LOADER_CLASS_NAME = "org.glassfish.api.deployment.InstrumentableClassLoader";
    private final ClassLoader classLoader;
    private final Method addTransformerMethod;
    private final Method copyMethod;

    public GlassFishLoadTimeWeaver() {
        this(ClassUtils.getDefaultClassLoader());
    }

    public GlassFishLoadTimeWeaver(@Nullable ClassLoader classLoader) {
        Class<?> instrumentableLoaderClass;
        Assert.notNull((Object)classLoader, (String)"ClassLoader must not be null");
        try {
            instrumentableLoaderClass = classLoader.loadClass(INSTRUMENTABLE_LOADER_CLASS_NAME);
            this.addTransformerMethod = instrumentableLoaderClass.getMethod("addTransformer", ClassFileTransformer.class);
            this.copyMethod = instrumentableLoaderClass.getMethod("copy", new Class[0]);
        }
        catch (Throwable ex) {
            throw new IllegalStateException("Could not initialize GlassFishLoadTimeWeaver because GlassFish API classes are not available", ex);
        }
        ClassLoader clazzLoader = null;
        for (ClassLoader cl = classLoader; cl != null && clazzLoader == null; cl = cl.getParent()) {
            if (!instrumentableLoaderClass.isInstance(cl)) continue;
            clazzLoader = cl;
        }
        if (clazzLoader == null) {
            throw new IllegalArgumentException(classLoader + " and its parents are not suitable ClassLoaders: A [" + instrumentableLoaderClass.getName() + "] implementation is required.");
        }
        this.classLoader = clazzLoader;
    }

    @Override
    public void addTransformer(ClassFileTransformer transformer) {
        try {
            this.addTransformerMethod.invoke((Object)this.classLoader, transformer);
        }
        catch (InvocationTargetException ex) {
            throw new IllegalStateException("GlassFish addTransformer method threw exception", ex.getCause());
        }
        catch (Throwable ex) {
            throw new IllegalStateException("Could not invoke GlassFish addTransformer method", ex);
        }
    }

    @Override
    public ClassLoader getInstrumentableClassLoader() {
        return this.classLoader;
    }

    @Override
    public ClassLoader getThrowawayClassLoader() {
        try {
            return new OverridingClassLoader(this.classLoader, (ClassLoader)this.copyMethod.invoke((Object)this.classLoader, new Object[0]));
        }
        catch (InvocationTargetException ex) {
            throw new IllegalStateException("GlassFish copy method threw exception", ex.getCause());
        }
        catch (Throwable ex) {
            throw new IllegalStateException("Could not invoke GlassFish copy method", ex);
        }
    }
}

