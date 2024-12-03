/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources.comparators;

import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.comparators.ResourceComparator;

public class DelegatedResourceComparator
extends ResourceComparator {
    private List<ResourceComparator> resourceComparators = null;

    public synchronized void add(ResourceComparator c) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (c == null) {
            return;
        }
        this.resourceComparators = this.resourceComparators == null ? new Vector() : this.resourceComparators;
        this.resourceComparators.add(c);
        this.setChecked(false);
    }

    @Override
    public synchronized boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (this.isReference()) {
            return this.getRef().equals(o);
        }
        if (o instanceof DelegatedResourceComparator) {
            List<ResourceComparator> ov = ((DelegatedResourceComparator)o).resourceComparators;
            return this.resourceComparators == null ? ov == null : this.resourceComparators.equals(ov);
        }
        return false;
    }

    @Override
    public synchronized int hashCode() {
        if (this.isReference()) {
            return this.getRef().hashCode();
        }
        return this.resourceComparators == null ? 0 : this.resourceComparators.hashCode();
    }

    @Override
    protected synchronized int resourceCompare(Resource foo, Resource bar) {
        return DelegatedResourceComparator.composite(this.resourceComparators).compare(foo, bar);
    }

    @Override
    protected void dieOnCircularReference(Stack<Object> stk, Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        } else {
            if (this.resourceComparators != null && !this.resourceComparators.isEmpty()) {
                for (ResourceComparator resourceComparator : this.resourceComparators) {
                    if (!(resourceComparator instanceof DataType)) continue;
                    DelegatedResourceComparator.pushAndInvokeCircularReferenceCheck(resourceComparator, stk, p);
                }
            }
            this.setChecked(true);
        }
    }

    private DelegatedResourceComparator getRef() {
        return this.getCheckedRef(DelegatedResourceComparator.class);
    }

    private static Comparator<Resource> composite(List<? extends Comparator<Resource>> foo) {
        Comparator<Resource> result = null;
        if (foo != null) {
            for (Comparator<Resource> comparator : foo) {
                if (result == null) {
                    result = comparator;
                    continue;
                }
                result = result.thenComparing(comparator);
            }
        }
        return result == null ? Comparator.naturalOrder() : result;
    }
}

