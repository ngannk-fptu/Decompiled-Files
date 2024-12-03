/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery.resource;

import org.apache.commons.discovery.Resource;
import org.apache.commons.discovery.ResourceDiscover;
import org.apache.commons.discovery.ResourceIterator;
import org.apache.commons.discovery.ResourceNameIterator;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.resource.names.ResourceNameDiscoverImpl;

public abstract class ResourceDiscoverImpl
extends ResourceNameDiscoverImpl
implements ResourceDiscover {
    private ClassLoaders classLoaders;

    public ResourceDiscoverImpl() {
    }

    public ResourceDiscoverImpl(ClassLoaders classLoaders) {
        this.setClassLoaders(classLoaders);
    }

    public void setClassLoaders(ClassLoaders loaders) {
        this.classLoaders = loaders;
    }

    public void addClassLoader(ClassLoader loader) {
        this.getClassLoaders().put(loader);
    }

    protected ClassLoaders getClassLoaders() {
        if (this.classLoaders == null) {
            this.classLoaders = ClassLoaders.getLibLoaders(this.getClass(), null, true);
        }
        return this.classLoaders;
    }

    public ResourceNameIterator findResourceNames(String resourceName) {
        return this.findResources(resourceName);
    }

    public ResourceNameIterator findResourceNames(ResourceNameIterator resourceNames) {
        return this.findResources(resourceNames);
    }

    public abstract ResourceIterator findResources(String var1);

    public ResourceIterator findResources(final ResourceNameIterator inputNames) {
        return new ResourceIterator(){
            private ResourceIterator resources = null;
            private Resource resource = null;

            public boolean hasNext() {
                if (this.resource == null) {
                    this.resource = this.getNextResource();
                }
                return this.resource != null;
            }

            public Resource nextResource() {
                Resource rsrc = this.resource;
                this.resource = null;
                return rsrc;
            }

            private Resource getNextResource() {
                while (inputNames.hasNext() && (this.resources == null || !this.resources.hasNext())) {
                    this.resources = ResourceDiscoverImpl.this.findResources(inputNames.nextResourceName());
                }
                return this.resources != null && this.resources.hasNext() ? this.resources.nextResource() : null;
            }
        };
    }
}

