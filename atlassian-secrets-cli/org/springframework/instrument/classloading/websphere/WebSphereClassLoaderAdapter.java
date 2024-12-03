/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.instrument.classloading.websphere;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import org.springframework.instrument.classloading.websphere.WebSphereClassPreDefinePlugin;
import org.springframework.util.Assert;

class WebSphereClassLoaderAdapter {
    private static final String COMPOUND_CLASS_LOADER_NAME = "com.ibm.ws.classloader.CompoundClassLoader";
    private static final String CLASS_PRE_PROCESSOR_NAME = "com.ibm.websphere.classloader.ClassLoaderInstancePreDefinePlugin";
    private static final String PLUGINS_FIELD = "preDefinePlugins";
    private ClassLoader classLoader;
    private Class<?> wsPreProcessorClass;
    private Method addPreDefinePlugin;
    private Constructor<? extends ClassLoader> cloneConstructor;
    private Field transformerList;

    public WebSphereClassLoaderAdapter(ClassLoader classLoader) {
        Class<?> wsCompoundClassLoaderClass;
        try {
            wsCompoundClassLoaderClass = classLoader.loadClass(COMPOUND_CLASS_LOADER_NAME);
            this.cloneConstructor = classLoader.getClass().getDeclaredConstructor(wsCompoundClassLoaderClass);
            this.cloneConstructor.setAccessible(true);
            this.wsPreProcessorClass = classLoader.loadClass(CLASS_PRE_PROCESSOR_NAME);
            this.addPreDefinePlugin = classLoader.getClass().getMethod("addPreDefinePlugin", this.wsPreProcessorClass);
            this.transformerList = wsCompoundClassLoaderClass.getDeclaredField(PLUGINS_FIELD);
            this.transformerList.setAccessible(true);
        }
        catch (Throwable ex) {
            throw new IllegalStateException("Could not initialize WebSphere LoadTimeWeaver because WebSphere API classes are not available", ex);
        }
        if (!wsCompoundClassLoaderClass.isInstance(classLoader)) {
            throw new IllegalArgumentException("ClassLoader must be an instance of [com.ibm.ws.classloader.CompoundClassLoader]: " + classLoader);
        }
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public void addTransformer(ClassFileTransformer transformer) {
        Assert.notNull((Object)transformer, "ClassFileTransformer must not be null");
        try {
            WebSphereClassPreDefinePlugin adapter = new WebSphereClassPreDefinePlugin(transformer);
            Object adapterInstance = Proxy.newProxyInstance(this.wsPreProcessorClass.getClassLoader(), new Class[]{this.wsPreProcessorClass}, (InvocationHandler)adapter);
            this.addPreDefinePlugin.invoke((Object)this.classLoader, adapterInstance);
        }
        catch (InvocationTargetException ex) {
            throw new IllegalStateException("WebSphere addPreDefinePlugin method threw exception", ex.getCause());
        }
        catch (Throwable ex) {
            throw new IllegalStateException("Could not invoke WebSphere addPreDefinePlugin method", ex);
        }
    }

    public ClassLoader getThrowawayClassLoader() {
        try {
            ClassLoader loader = this.cloneConstructor.newInstance(this.getClassLoader());
            List list = (List)this.transformerList.get(loader);
            list.clear();
            return loader;
        }
        catch (InvocationTargetException ex) {
            throw new IllegalStateException("WebSphere CompoundClassLoader constructor failed", ex.getCause());
        }
        catch (Throwable ex) {
            throw new IllegalStateException("Could not construct WebSphere CompoundClassLoader", ex);
        }
    }
}

