/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery;

import org.apache.commons.discovery.Resource;
import org.apache.commons.discovery.ResourceClass;
import org.apache.commons.discovery.ResourceIterator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class ResourceClassIterator<T>
extends ResourceIterator {
    public abstract <S extends T> ResourceClass<S> nextResourceClass();

    @Override
    public Resource nextResource() {
        return this.nextResourceClass();
    }

    @Override
    public String nextResourceName() {
        return this.nextResourceClass().getName();
    }
}

