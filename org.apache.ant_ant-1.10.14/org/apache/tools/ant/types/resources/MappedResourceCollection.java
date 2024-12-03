/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.MappedResource;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.IdentityMapper;
import org.apache.tools.ant.util.MergingMapper;

public class MappedResourceCollection
extends DataType
implements ResourceCollection,
Cloneable {
    private ResourceCollection nested = null;
    private Mapper mapper = null;
    private boolean enableMultipleMappings = false;
    private boolean cache = false;
    private Collection<Resource> cachedColl = null;

    public synchronized void add(ResourceCollection c) throws BuildException {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (this.nested != null) {
            throw new BuildException("Only one resource collection can be nested into mappedresources", this.getLocation());
        }
        this.setChecked(false);
        this.cachedColl = null;
        this.nested = c;
    }

    public Mapper createMapper() throws BuildException {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (this.mapper != null) {
            throw new BuildException("Cannot define more than one mapper", this.getLocation());
        }
        this.setChecked(false);
        this.mapper = new Mapper(this.getProject());
        this.cachedColl = null;
        return this.mapper;
    }

    public void add(FileNameMapper fileNameMapper) {
        this.createMapper().add(fileNameMapper);
    }

    public void setEnableMultipleMappings(boolean enableMultipleMappings) {
        this.enableMultipleMappings = enableMultipleMappings;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    @Override
    public boolean isFilesystemOnly() {
        if (this.isReference()) {
            return this.getRef().isFilesystemOnly();
        }
        this.checkInitialized();
        return false;
    }

    @Override
    public int size() {
        if (this.isReference()) {
            return this.getRef().size();
        }
        this.checkInitialized();
        return this.cacheCollection().size();
    }

    @Override
    public Iterator<Resource> iterator() {
        if (this.isReference()) {
            return this.getRef().iterator();
        }
        this.checkInitialized();
        return this.cacheCollection().iterator();
    }

    @Override
    public void setRefid(Reference r) {
        if (this.nested != null || this.mapper != null) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }

    @Override
    public Object clone() {
        try {
            MappedResourceCollection c = (MappedResourceCollection)super.clone();
            c.nested = this.nested;
            c.mapper = this.mapper;
            c.cachedColl = null;
            return c;
        }
        catch (CloneNotSupportedException e) {
            throw new BuildException(e);
        }
    }

    @Override
    protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        } else {
            this.checkInitialized();
            if (this.mapper != null) {
                MappedResourceCollection.pushAndInvokeCircularReferenceCheck(this.mapper, stk, p);
            }
            if (this.nested instanceof DataType) {
                MappedResourceCollection.pushAndInvokeCircularReferenceCheck((DataType)((Object)this.nested), stk, p);
            }
            this.setChecked(true);
        }
    }

    private void checkInitialized() {
        if (this.nested == null) {
            throw new BuildException("A nested resource collection element is required", this.getLocation());
        }
        this.dieOnCircularReference();
    }

    private synchronized Collection<Resource> cacheCollection() {
        if (this.cachedColl == null || !this.cache) {
            this.cachedColl = this.getCollection();
        }
        return this.cachedColl;
    }

    private Collection<Resource> getCollection() {
        FileNameMapper m = this.mapper == null ? new IdentityMapper() : this.mapper.getImplementation();
        Stream<Object> stream = this.enableMultipleMappings ? this.nested.stream().flatMap(r -> Stream.of(m.mapFileName(r.getName())).filter(Objects::nonNull).map(MergingMapper::new).map(mm -> new MappedResource((Resource)r, (FileNameMapper)mm))) : this.nested.stream().map(r -> new MappedResource((Resource)r, m));
        return stream.collect(Collectors.toList());
    }

    @Override
    public String toString() {
        if (this.isReference()) {
            return this.getRef().toString();
        }
        return this.isEmpty() ? "" : this.stream().map(Object::toString).collect(Collectors.joining(File.pathSeparator));
    }

    private MappedResourceCollection getRef() {
        return this.getCheckedRef(MappedResourceCollection.class);
    }
}

