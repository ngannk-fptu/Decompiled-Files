/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery.tools;

import org.apache.commons.discovery.ResourceClass;
import org.apache.commons.discovery.ResourceClassIterator;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.resource.classes.DiscoverClasses;
import org.apache.commons.discovery.tools.SPInterface;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DefaultClassHolder<T> {
    private Class<? extends T> defaultClass;
    private final String defaultName;

    public <S extends T> DefaultClassHolder(Class<S> defaultClass) {
        this.defaultClass = defaultClass;
        this.defaultName = defaultClass.getName();
    }

    public DefaultClassHolder(String defaultName) {
        this.defaultClass = null;
        this.defaultName = defaultName;
    }

    public <S extends T> Class<S> getDefaultClass(SPInterface<T> spi, ClassLoaders loaders) {
        DiscoverClasses classDiscovery;
        ResourceClassIterator classes;
        if (this.defaultClass == null && (classes = (classDiscovery = new DiscoverClasses(loaders)).findResourceClasses(this.getDefaultName())).hasNext()) {
            ResourceClass info = classes.nextResourceClass();
            try {
                this.defaultClass = info.loadClass();
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        if (this.defaultClass != null) {
            spi.verifyAncestory(this.defaultClass);
        }
        Class<? extends T> returned = this.defaultClass;
        return returned;
    }

    public String getDefaultName() {
        return this.defaultName;
    }
}

