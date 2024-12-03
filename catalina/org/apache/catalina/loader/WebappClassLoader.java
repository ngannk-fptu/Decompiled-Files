/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.loader;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.loader.WebappClassLoaderBase;

public class WebappClassLoader
extends WebappClassLoaderBase {
    public WebappClassLoader() {
    }

    public WebappClassLoader(ClassLoader parent) {
        super(parent);
    }

    public WebappClassLoader copyWithoutTransformers() {
        WebappClassLoader result = new WebappClassLoader(this.getParent());
        super.copyStateWithoutTransformers(result);
        try {
            result.start();
        }
        catch (LifecycleException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }

    @Override
    protected Object getClassLoadingLock(String className) {
        return this;
    }
}

