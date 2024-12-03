/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery.resource.classes;

import org.apache.commons.discovery.ResourceClass;
import org.apache.commons.discovery.ResourceClassDiscover;
import org.apache.commons.discovery.ResourceClassIterator;
import org.apache.commons.discovery.ResourceIterator;
import org.apache.commons.discovery.ResourceNameIterator;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.resource.ResourceDiscoverImpl;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class ResourceClassDiscoverImpl<T>
extends ResourceDiscoverImpl
implements ResourceClassDiscover<T> {
    public ResourceClassDiscoverImpl() {
    }

    public ResourceClassDiscoverImpl(ClassLoaders classLoaders) {
        super(classLoaders);
    }

    @Override
    public ResourceNameIterator findResourceNames(String resourceName) {
        return this.findResourceClasses(resourceName);
    }

    @Override
    public ResourceNameIterator findResourceNames(ResourceNameIterator resourceNames) {
        return this.findResourceClasses(resourceNames);
    }

    @Override
    public ResourceIterator findResources(String resourceName) {
        return this.findResourceClasses(resourceName);
    }

    @Override
    public ResourceIterator findResources(ResourceNameIterator resourceNames) {
        return this.findResourceClasses(resourceNames);
    }

    @Override
    public abstract ResourceClassIterator<T> findResourceClasses(String var1);

    @Override
    public ResourceClassIterator<T> findResourceClasses(final ResourceNameIterator inputNames) {
        return new ResourceClassIterator<T>(){
            private ResourceClassIterator<T> classes = null;
            private ResourceClass<T> resource = null;

            @Override
            public boolean hasNext() {
                if (this.resource == null) {
                    this.resource = this.getNextResource();
                }
                return this.resource != null;
            }

            @Override
            public ResourceClass<T> nextResourceClass() {
                ResourceClass rsrc = this.resource;
                this.resource = null;
                return rsrc;
            }

            private ResourceClass<T> getNextResource() {
                while (inputNames.hasNext() && (this.classes == null || !this.classes.hasNext())) {
                    this.classes = ResourceClassDiscoverImpl.this.findResourceClasses(inputNames.nextResourceName());
                }
                return this.classes != null && this.classes.hasNext() ? this.classes.nextResourceClass() : null;
            }
        };
    }
}

