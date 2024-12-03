/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery;

import org.apache.commons.discovery.ResourceClassIterator;
import org.apache.commons.discovery.ResourceDiscover;
import org.apache.commons.discovery.ResourceNameIterator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ResourceClassDiscover<T>
extends ResourceDiscover {
    public ResourceClassIterator<T> findResourceClasses(String var1);

    public ResourceClassIterator<T> findResourceClasses(ResourceNameIterator var1);
}

