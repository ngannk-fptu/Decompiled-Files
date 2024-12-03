/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources.comparators;

import java.util.Comparator;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Resource;

public abstract class ResourceComparator
extends DataType
implements Comparator<Resource> {
    @Override
    public final int compare(Resource foo, Resource bar) {
        this.dieOnCircularReference();
        ResourceComparator c = this.isReference() ? this.getRef() : this;
        return c.resourceCompare(foo, bar);
    }

    @Override
    public boolean equals(Object o) {
        if (this.isReference()) {
            return this.getRef().equals(o);
        }
        return o != null && (o == this || o.getClass().equals(this.getClass()));
    }

    public synchronized int hashCode() {
        if (this.isReference()) {
            return this.getRef().hashCode();
        }
        return this.getClass().hashCode();
    }

    protected abstract int resourceCompare(Resource var1, Resource var2);

    private ResourceComparator getRef() {
        return this.getCheckedRef(ResourceComparator.class);
    }
}

