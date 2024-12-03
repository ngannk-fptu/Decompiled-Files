/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.AppendableResourceCollection;
import org.apache.tools.ant.types.resources.FailFast;
import org.apache.tools.ant.types.resources.FileProvider;

public abstract class BaseResourceCollectionContainer
extends DataType
implements AppendableResourceCollection,
Cloneable {
    private List<ResourceCollection> rc = new ArrayList<ResourceCollection>();
    private Collection<Resource> coll = null;
    private boolean cache = true;

    public BaseResourceCollectionContainer() {
    }

    public BaseResourceCollectionContainer(Project project) {
        this.setProject(project);
    }

    public synchronized void setCache(boolean b) {
        this.cache = b;
    }

    public synchronized boolean isCache() {
        return this.cache;
    }

    public synchronized void clear() throws BuildException {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.rc.clear();
        FailFast.invalidate(this);
        this.coll = null;
        this.setChecked(false);
    }

    @Override
    public synchronized void add(ResourceCollection c) throws BuildException {
        Project p;
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (c == null) {
            return;
        }
        if (Project.getProject(c) == null && (p = this.getProject()) != null) {
            p.setProjectReference(c);
        }
        this.rc.add(c);
        FailFast.invalidate(this);
        this.coll = null;
        this.setChecked(false);
    }

    public synchronized void addAll(Collection<? extends ResourceCollection> c) throws BuildException {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        try {
            c.forEach(this::add);
        }
        catch (ClassCastException e) {
            throw new BuildException(e);
        }
    }

    @Override
    public final synchronized Iterator<Resource> iterator() {
        if (this.isReference()) {
            return this.getRef().iterator();
        }
        this.dieOnCircularReference();
        return new FailFast(this, this.cacheCollection().iterator());
    }

    @Override
    public synchronized int size() {
        if (this.isReference()) {
            return this.getRef().size();
        }
        this.dieOnCircularReference();
        return this.cacheCollection().size();
    }

    @Override
    public synchronized boolean isFilesystemOnly() {
        if (this.isReference()) {
            return this.getRef().isFilesystemOnly();
        }
        this.dieOnCircularReference();
        if (this.rc.stream().allMatch(ResourceCollection::isFilesystemOnly)) {
            return true;
        }
        return this.cacheCollection().stream().allMatch(r -> r.asOptional(FileProvider.class).isPresent());
    }

    @Override
    protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        } else {
            for (ResourceCollection resourceCollection : this.rc) {
                if (!(resourceCollection instanceof DataType)) continue;
                BaseResourceCollectionContainer.pushAndInvokeCircularReferenceCheck((DataType)((Object)resourceCollection), stk, p);
            }
            this.setChecked(true);
        }
    }

    public final synchronized List<ResourceCollection> getResourceCollections() {
        this.dieOnCircularReference();
        return Collections.unmodifiableList(this.rc);
    }

    protected abstract Collection<Resource> getCollection();

    @Override
    public Object clone() {
        try {
            BaseResourceCollectionContainer c = (BaseResourceCollectionContainer)super.clone();
            c.rc = new ArrayList<ResourceCollection>(this.rc);
            c.coll = null;
            return c;
        }
        catch (CloneNotSupportedException e) {
            throw new BuildException(e);
        }
    }

    @Override
    public synchronized String toString() {
        if (this.isReference()) {
            return this.getRef().toString();
        }
        if (this.cacheCollection().isEmpty()) {
            return "";
        }
        return this.coll.stream().map(Object::toString).collect(Collectors.joining(File.pathSeparator));
    }

    private BaseResourceCollectionContainer getRef() {
        return this.getCheckedRef(BaseResourceCollectionContainer.class);
    }

    private synchronized Collection<Resource> cacheCollection() {
        if (this.coll == null || !this.isCache()) {
            this.coll = this.getCollection();
        }
        return this.coll;
    }
}

