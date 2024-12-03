/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.util.Collection;
import java.util.stream.Collectors;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.SizeLimitCollection;

public class AllButFirst
extends SizeLimitCollection {
    @Override
    protected Collection<Resource> getCollection() {
        return this.getResourceCollection().stream().skip(this.getValidCount()).collect(Collectors.toList());
    }

    @Override
    public synchronized int size() {
        return Math.max(this.getResourceCollection().size() - this.getValidCount(), 0);
    }
}

