/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.atlassian.crowd.model.InternalEntity
 *  com.atlassian.crowd.model.InternalEntityTemplate
 */
package com.atlassian.crowd.model;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.InternalEntity;
import com.atlassian.crowd.model.InternalEntityAttribute;
import com.atlassian.crowd.model.InternalEntityTemplate;
import java.util.HashSet;
import java.util.Set;

public abstract class InternalDirectoryEntity<T extends InternalEntityAttribute>
extends InternalEntity
implements DirectoryEntity {
    protected Directory directory;
    protected Set<T> attributes = new HashSet<T>();

    protected InternalDirectoryEntity() {
    }

    protected InternalDirectoryEntity(InternalEntityTemplate template, Directory directory) {
        super(template);
        this.directory = directory;
    }

    public long getDirectoryId() {
        return this.getDirectory() != null ? this.getDirectory().getId() : -1L;
    }

    public Directory getDirectory() {
        return this.directory;
    }

    private void setDirectory(Directory directory) {
        this.directory = directory;
    }

    public Set<T> getAttributes() {
        return this.attributes;
    }

    private void setAttributes(Set<T> attributes) {
        this.attributes = attributes;
    }
}

