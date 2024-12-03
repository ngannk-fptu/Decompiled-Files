/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.util.Collection;
import java.util.Stack;
import java.util.stream.Collectors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.BaseResourceCollectionWrapper;
import org.apache.tools.ant.types.resources.FailFast;
import org.apache.tools.ant.types.resources.comparators.DelegatedResourceComparator;
import org.apache.tools.ant.types.resources.comparators.ResourceComparator;

public class Sort
extends BaseResourceCollectionWrapper {
    private DelegatedResourceComparator comp = new DelegatedResourceComparator();

    @Override
    protected synchronized Collection<Resource> getCollection() {
        return this.getResourceCollection().stream().map(Resource.class::cast).sorted(this.comp).collect(Collectors.toList());
    }

    public synchronized void add(ResourceComparator c) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.comp.add(c);
        FailFast.invalidate(this);
        this.setChecked(false);
    }

    @Override
    protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        super.dieOnCircularReference(stk, p);
        if (!this.isReference()) {
            DataType.pushAndInvokeCircularReferenceCheck(this.comp, stk, p);
            this.setChecked(true);
        }
    }
}

