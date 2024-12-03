/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.util.Iterator;
import java.util.Stack;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FailFast;
import org.apache.tools.ant.types.resources.LazyResourceCollectionWrapper;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.types.resources.selectors.ResourceSelectorContainer;

public class Restrict
extends ResourceSelectorContainer
implements ResourceCollection {
    private LazyResourceCollectionWrapper w = new LazyResourceCollectionWrapper(){

        @Override
        protected boolean filterResource(Resource r) {
            return Restrict.this.getResourceSelectors().stream().anyMatch(rsel -> !rsel.isSelected(r));
        }
    };

    public synchronized void add(ResourceCollection c) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (c == null) {
            return;
        }
        this.w.add(c);
        this.setChecked(false);
    }

    public synchronized void setCache(boolean b) {
        this.w.setCache(b);
    }

    public synchronized boolean isCache() {
        return this.w.isCache();
    }

    @Override
    public synchronized void add(ResourceSelector s) {
        if (s == null) {
            return;
        }
        super.add(s);
        FailFast.invalidate(this);
    }

    @Override
    public final synchronized Iterator<Resource> iterator() {
        if (this.isReference()) {
            return this.getRef().iterator();
        }
        this.dieOnCircularReference();
        return this.w.iterator();
    }

    @Override
    public synchronized int size() {
        if (this.isReference()) {
            return this.getRef().size();
        }
        this.dieOnCircularReference();
        return this.w.size();
    }

    @Override
    public synchronized boolean isFilesystemOnly() {
        if (this.isReference()) {
            return this.getRef().isFilesystemOnly();
        }
        this.dieOnCircularReference();
        return this.w.isFilesystemOnly();
    }

    @Override
    public synchronized String toString() {
        if (this.isReference()) {
            return this.getRef().toString();
        }
        this.dieOnCircularReference();
        return this.w.toString();
    }

    @Override
    protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p) {
        if (this.isChecked()) {
            return;
        }
        super.dieOnCircularReference(stk, p);
        if (!this.isReference()) {
            Restrict.pushAndInvokeCircularReferenceCheck(this.w, stk, p);
            this.setChecked(true);
        }
    }

    private Restrict getRef() {
        return this.getCheckedRef(Restrict.class);
    }
}

