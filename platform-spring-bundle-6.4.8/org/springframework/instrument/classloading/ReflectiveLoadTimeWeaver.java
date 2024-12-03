/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.instrument.classloading;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Method;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.DecoratingClassLoader;
import org.springframework.core.OverridingClassLoader;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.instrument.classloading.SimpleThrowawayClassLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public class ReflectiveLoadTimeWeaver
implements LoadTimeWeaver {
    private static final String ADD_TRANSFORMER_METHOD_NAME = "addTransformer";
    private static final String GET_THROWAWAY_CLASS_LOADER_METHOD_NAME = "getThrowawayClassLoader";
    private static final Log logger = LogFactory.getLog(ReflectiveLoadTimeWeaver.class);
    private final ClassLoader classLoader;
    private final Method addTransformerMethod;
    @Nullable
    private final Method getThrowawayClassLoaderMethod;

    public ReflectiveLoadTimeWeaver() {
        this(ClassUtils.getDefaultClassLoader());
    }

    public ReflectiveLoadTimeWeaver(@Nullable ClassLoader classLoader) {
        Assert.notNull((Object)classLoader, "ClassLoader must not be null");
        this.classLoader = classLoader;
        Method addTransformerMethod = ClassUtils.getMethodIfAvailable(this.classLoader.getClass(), ADD_TRANSFORMER_METHOD_NAME, ClassFileTransformer.class);
        if (addTransformerMethod == null) {
            throw new IllegalStateException("ClassLoader [" + classLoader.getClass().getName() + "] does NOT provide an 'addTransformer(ClassFileTransformer)' method.");
        }
        this.addTransformerMethod = addTransformerMethod;
        Method getThrowawayClassLoaderMethod = ClassUtils.getMethodIfAvailable(this.classLoader.getClass(), GET_THROWAWAY_CLASS_LOADER_METHOD_NAME, new Class[0]);
        if (getThrowawayClassLoaderMethod == null && logger.isDebugEnabled()) {
            logger.debug((Object)("The ClassLoader [" + classLoader.getClass().getName() + "] does NOT provide a 'getThrowawayClassLoader()' method; SimpleThrowawayClassLoader will be used instead."));
        }
        this.getThrowawayClassLoaderMethod = getThrowawayClassLoaderMethod;
    }

    @Override
    public void addTransformer(ClassFileTransformer transformer) {
        Assert.notNull((Object)transformer, "Transformer must not be null");
        ReflectionUtils.invokeMethod(this.addTransformerMethod, this.classLoader, transformer);
    }

    @Override
    public ClassLoader getInstrumentableClassLoader() {
        return this.classLoader;
    }

    @Override
    public ClassLoader getThrowawayClassLoader() {
        if (this.getThrowawayClassLoaderMethod != null) {
            ClassLoader target = (ClassLoader)ReflectionUtils.invokeMethod(this.getThrowawayClassLoaderMethod, this.classLoader);
            return target instanceof DecoratingClassLoader ? target : new OverridingClassLoader(this.classLoader, target);
        }
        return new SimpleThrowawayClassLoader(this.classLoader);
    }
}

