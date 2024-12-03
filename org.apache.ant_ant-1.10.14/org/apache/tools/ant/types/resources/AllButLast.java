/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.SizeLimitCollection;

public class AllButLast
extends SizeLimitCollection {
    @Override
    protected Collection<Resource> getCollection() {
        ResourceCollection nested;
        int ct = this.getValidCount();
        if (ct > (nested = this.getResourceCollection()).size()) {
            return Collections.emptyList();
        }
        return nested.stream().limit((long)nested.size() - (long)ct).collect(Collectors.toList());
    }

    @Override
    public synchronized int size() {
        return Math.max(this.getResourceCollection().size() - this.getValidCount(), 0);
    }
}

