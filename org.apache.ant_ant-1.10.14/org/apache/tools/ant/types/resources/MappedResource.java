/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.ResourceDecorator;
import org.apache.tools.ant.util.FileNameMapper;

public class MappedResource
extends ResourceDecorator {
    private final FileNameMapper mapper;

    public MappedResource(Resource r, FileNameMapper m) {
        super(r);
        this.mapper = m;
    }

    @Override
    public String getName() {
        String name = this.getResource().getName();
        if (this.isReference()) {
            return name;
        }
        String[] mapped = this.mapper.mapFileName(name);
        return mapped != null && mapped.length > 0 ? mapped[0] : null;
    }

    @Override
    public void setRefid(Reference r) {
        if (this.mapper != null) {
            throw this.noChildrenAllowed();
        }
        super.setRefid(r);
    }

    @Override
    public <T> T as(Class<T> clazz) {
        return FileProvider.class.isAssignableFrom(clazz) ? null : (T)this.getResource().as(clazz);
    }

    @Override
    public int hashCode() {
        String n = this.getName();
        return n == null ? super.hashCode() : n.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        MappedResource m = (MappedResource)other;
        String myName = this.getName();
        String otherName = m.getName();
        return (myName == null ? otherName == null : myName.equals(otherName)) && this.getResource().equals(m.getResource());
    }

    @Override
    public String toString() {
        if (this.isReference()) {
            return this.getRef().toString();
        }
        return this.getName();
    }
}

