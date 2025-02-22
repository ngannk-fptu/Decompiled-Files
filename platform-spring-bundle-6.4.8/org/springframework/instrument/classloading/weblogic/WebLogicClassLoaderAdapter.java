/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.instrument.classloading.weblogic;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.springframework.instrument.classloading.weblogic.WebLogicClassPreProcessorAdapter;
import org.springframework.util.Assert;

class WebLogicClassLoaderAdapter {
    private static final String GENERIC_CLASS_LOADER_NAME = "weblogic.utils.classloaders.GenericClassLoader";
    private static final String CLASS_PRE_PROCESSOR_NAME = "weblogic.utils.classloaders.ClassPreProcessor";
    private final ClassLoader classLoader;
    private final Class<?> wlPreProcessorClass;
    private final Method addPreProcessorMethod;
    private final Method getClassFinderMethod;
    private final Method getParentMethod;
    private final Constructor<?> wlGenericClassLoaderConstructor;

    public WebLogicClassLoaderAdapter(ClassLoader classLoader) {
        Class<?> wlGenericClassLoaderClass;
        try {
            wlGenericClassLoaderClass = classLoader.loadClass(GENERIC_CLASS_LOADER_NAME);
            this.wlPreProcessorClass = classLoader.loadClass(CLASS_PRE_PROCESSOR_NAME);
            this.addPreProcessorMethod = classLoader.getClass().getMethod("addInstanceClassPreProcessor", this.wlPreProcessorClass);
            this.getClassFinderMethod = classLoader.getClass().getMethod("getClassFinder", new Class[0]);
            this.getParentMethod = classLoader.getClass().getMethod("getParent", new Class[0]);
            this.wlGenericClassLoaderConstructor = wlGenericClassLoaderClass.getConstructor(this.getClassFinderMethod.getReturnType(), ClassLoader.class);
        }
        catch (Throwable ex) {
            throw new IllegalStateException("Could not initialize WebLogic LoadTimeWeaver because WebLogic 10 API classes are not available", ex);
        }
        if (!wlGenericClassLoaderClass.isInstance(classLoader)) {
            throw new IllegalArgumentException("ClassLoader must be an instance of [" + wlGenericClassLoaderClass.getName() + "]: " + classLoader);
        }
        this.classLoader = classLoader;
    }

    public void addTransformer(ClassFileTransformer transformer) {
        Assert.notNull((Object)transformer, "ClassFileTransformer must not be null");
        try {
            WebLogicClassPreProcessorAdapter adapter = new WebLogicClassPreProcessorAdapter(transformer, this.classLoader);
            Object adapterInstance = Proxy.newProxyInstance(this.wlPreProcessorClass.getClassLoader(), new Class[]{this.wlPreProcessorClass}, (InvocationHandler)adapter);
            this.addPreProcessorMethod.invoke((Object)this.classLoader, adapterInstance);
        }
        catch (InvocationTargetException ex) {
            throw new IllegalStateException("WebLogic addInstanceClassPreProcessor method threw exception", ex.getCause());
        }
        catch (Throwable ex) {
            throw new IllegalStateException("Could not invoke WebLogic addInstanceClassPreProcessor method", ex);
        }
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public ClassLoader getThrowawayClassLoader() {
        try {
            Object classFinder = this.getClassFinderMethod.invoke((Object)this.classLoader, new Object[0]);
            Object parent = this.getParentMethod.invoke((Object)this.classLoader, new Object[0]);
            return (ClassLoader)this.wlGenericClassLoaderConstructor.newInstance(classFinder, parent);
        }
        catch (InvocationTargetException ex) {
            throw new IllegalStateException("WebLogic GenericClassLoader constructor failed", ex.getCause());
        }
        catch (Throwable ex) {
            throw new IllegalStateException("Could not construct WebLogic GenericClassLoader", ex);
        }
    }
}

