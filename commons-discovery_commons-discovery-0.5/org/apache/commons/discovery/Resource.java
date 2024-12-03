/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import org.apache.commons.discovery.ResourceIterator;

public class Resource {
    protected final String name;
    protected final URL resource;
    protected final ClassLoader loader;

    public Resource(String resourceName, URL resource, ClassLoader loader) {
        this.name = resourceName;
        this.resource = resource;
        this.loader = loader;
    }

    public String getName() {
        return this.name;
    }

    public URL getResource() {
        return this.resource;
    }

    public InputStream getResourceAsStream() {
        try {
            return this.resource.openStream();
        }
        catch (IOException e) {
            return null;
        }
    }

    public ClassLoader getClassLoader() {
        return this.loader;
    }

    public String toString() {
        return "Resource[" + this.getName() + ", " + this.getResource() + ", " + this.getClassLoader() + "]";
    }

    public static Resource[] toArray(ResourceIterator iterator) {
        LinkedList<Resource> resourceList = new LinkedList<Resource>();
        while (iterator.hasNext()) {
            resourceList.add(iterator.nextResource());
        }
        Resource[] resources = new Resource[resourceList.size()];
        resourceList.toArray(resources);
        return resources;
    }
}

