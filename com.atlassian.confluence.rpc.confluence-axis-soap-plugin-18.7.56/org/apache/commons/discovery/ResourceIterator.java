/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery;

import org.apache.commons.discovery.Resource;
import org.apache.commons.discovery.ResourceNameIterator;

public abstract class ResourceIterator
implements ResourceNameIterator {
    public abstract Resource nextResource();

    public String nextResourceName() {
        return this.nextResource().getName();
    }
}

