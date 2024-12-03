/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.File;
import java.util.Iterator;
import java.util.Stack;
import java.util.stream.Collectors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FailFast;
import org.apache.tools.ant.types.resources.FileProvider;

public abstract class AbstractResourceCollectionWrapper
extends DataType
implements ResourceCollection,
Cloneable {
    private static final String ONE_NESTED_MESSAGE = " expects exactly one nested resource collection.";
    private ResourceCollection rc;
    private boolean cache = true;

    public synchronized void setCache(boolean b) {
        this.cache = b;
    }

    public synchronized boolean isCache() {
        return this.cache;
    }

    public synchronized void add(ResourceCollection c) throws BuildException {
        Project p;
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (c == null) {
            return;
        }
        if (this.rc != null) {
            throw this.oneNested();
        }
        this.rc = c;
        if (Project.getProject(this.rc) == null && (p = this.getProject()) != null) {
            p.setProjectReference(this.rc);
        }
        this.setChecked(false);
    }

    @Override
    public final synchronized Iterator<Resource> iterator() {
        if (this.isReference()) {
            return this.getRef().iterator();
        }
        this.dieOnCircularReference();
        return new FailFast(this, this.createIterator());
    }

    protected abstract Iterator<Resource> createIterator();

    @Override
    public synchronized int size() {
        if (this.isReference()) {
            return this.getRef().size();
        }
        this.dieOnCircularReference();
        return this.getSize();
    }

    protected abstract int getSize();

    @Override
    public synchronized boolean isFilesystemOnly() {
        if (this.isReference()) {
            return this.getRef().isFilesystemOnly();
        }
        this.dieOnCircularReference();
        if (this.rc == null || this.rc.isFilesystemOnly()) {
            return true;
        }
        for (Resource r : this) {
            if (r.as(FileProvider.class) != null) continue;
            return false;
        }
        return true;
    }

    @Override
    protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        } else {
            if (this.rc instanceof DataType) {
                AbstractResourceCollectionWrapper.pushAndInvokeCircularReferenceCheck((DataType)((Object)this.rc), stk, p);
            }
            this.setChecked(true);
        }
    }

    protected final synchronized ResourceCollection getResourceCollection() {
        this.dieOnCircularReference();
        if (this.rc == null) {
            throw this.oneNested();
        }
        return this.rc;
    }

    @Override
    public synchronized String toString() {
        if (this.isReference()) {
            return this.getRef().toString();
        }
        if (this.isEmpty()) {
            return "";
        }
        return this.stream().map(Object::toString).collect(Collectors.joining(File.pathSeparator));
    }

    private AbstractResourceCollectionWrapper getRef() {
        return this.getCheckedRef(AbstractResourceCollectionWrapper.class);
    }

    private BuildException oneNested() {
        return new BuildException(super.toString() + ONE_NESTED_MESSAGE);
    }
}

