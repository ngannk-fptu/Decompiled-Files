/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.discovery;

import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.apache.commons.discovery.Resource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ResourceClass<T>
extends Resource {
    private static Log log = LogFactory.getLog(ResourceClass.class);
    protected Class<? extends T> resourceClass;

    @Deprecated
    public static void setLog(Log _log) {
        log = _log;
    }

    public <S extends T> ResourceClass(Class<S> resourceClass, URL resource) {
        super(resourceClass.getName(), resource, resourceClass.getClassLoader());
        this.resourceClass = resourceClass;
    }

    public ResourceClass(String resourceName, URL resource, ClassLoader loader) {
        super(resourceName, resource, loader);
    }

    public <S extends T> Class<S> loadClass() {
        if (this.resourceClass == null && this.getClassLoader() != null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("loadClass: Loading class '" + this.getName() + "' with " + this.getClassLoader()));
            }
            this.resourceClass = (Class)AccessController.doPrivileged(new PrivilegedAction<Class<? extends T>>(){

                @Override
                public Class<? extends T> run() {
                    try {
                        Class<?> returned = ResourceClass.this.getClassLoader().loadClass(ResourceClass.this.getName());
                        return returned;
                    }
                    catch (ClassNotFoundException e) {
                        return null;
                    }
                }
            });
        }
        Class<? extends T> returned = this.resourceClass;
        return returned;
    }

    @Override
    public String toString() {
        return "ResourceClass[" + this.getName() + ", " + this.getResource() + ", " + this.getClassLoader() + "]";
    }
}

