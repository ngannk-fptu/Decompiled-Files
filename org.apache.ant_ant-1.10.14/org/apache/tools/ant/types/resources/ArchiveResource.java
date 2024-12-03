/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.File;
import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;

public abstract class ArchiveResource
extends Resource {
    private static final int NULL_ARCHIVE = Resource.getMagicNumber("null archive".getBytes());
    private Resource archive;
    private boolean haveEntry = false;
    private boolean modeSet = false;
    private int mode = 0;

    protected ArchiveResource() {
    }

    protected ArchiveResource(File a) {
        this(a, false);
    }

    protected ArchiveResource(File a, boolean withEntry) {
        this.setArchive(a);
        this.haveEntry = withEntry;
    }

    protected ArchiveResource(Resource a, boolean withEntry) {
        this.addConfigured(a);
        this.haveEntry = withEntry;
    }

    public void setArchive(File a) {
        this.checkAttributesAllowed();
        this.archive = new FileResource(a);
    }

    public void setMode(int mode) {
        this.checkAttributesAllowed();
        this.mode = mode;
        this.modeSet = true;
    }

    public void addConfigured(ResourceCollection a) {
        this.checkChildrenAllowed();
        if (this.archive != null) {
            throw new BuildException("you must not specify more than one archive");
        }
        if (a.size() != 1) {
            throw new BuildException("only single argument resource collections are supported as archives");
        }
        this.archive = (Resource)a.iterator().next();
    }

    public Resource getArchive() {
        return this.isReference() ? this.getRef().getArchive() : this.archive;
    }

    @Override
    public long getLastModified() {
        if (this.isReference()) {
            return this.getRef().getLastModified();
        }
        this.checkEntry();
        return super.getLastModified();
    }

    @Override
    public long getSize() {
        if (this.isReference()) {
            return this.getRef().getSize();
        }
        this.checkEntry();
        return super.getSize();
    }

    @Override
    public boolean isDirectory() {
        if (this.isReference()) {
            return this.getRef().isDirectory();
        }
        this.checkEntry();
        return super.isDirectory();
    }

    @Override
    public boolean isExists() {
        if (this.isReference()) {
            return this.getRef().isExists();
        }
        this.checkEntry();
        return super.isExists();
    }

    public int getMode() {
        if (this.isReference()) {
            return this.getRef().getMode();
        }
        this.checkEntry();
        return this.mode;
    }

    @Override
    public void setRefid(Reference r) {
        if (this.archive != null || this.modeSet) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }

    @Override
    public int compareTo(Resource another) {
        return this.equals(another) ? 0 : super.compareTo(another);
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (this.isReference()) {
            return this.getRef().equals(another);
        }
        if (another == null || !another.getClass().equals(this.getClass())) {
            return false;
        }
        ArchiveResource r = (ArchiveResource)another;
        return this.getArchive().equals(r.getArchive()) && this.getName().equals(r.getName());
    }

    @Override
    public int hashCode() {
        return super.hashCode() * (this.getArchive() == null ? NULL_ARCHIVE : this.getArchive().hashCode());
    }

    @Override
    public String toString() {
        return this.isReference() ? this.getRef().toString() : this.getArchive().toString() + ':' + this.getName();
    }

    protected final synchronized void checkEntry() throws BuildException {
        this.dieOnCircularReference();
        if (this.haveEntry) {
            return;
        }
        String name = this.getName();
        if (name == null) {
            throw new BuildException("entry name not set");
        }
        Resource r = this.getArchive();
        if (r == null) {
            throw new BuildException("archive attribute not set");
        }
        if (!r.isExists()) {
            throw new BuildException("%s does not exist.", r);
        }
        if (r.isDirectory()) {
            throw new BuildException("%s denotes a directory.", r);
        }
        this.fetchEntry();
        this.haveEntry = true;
    }

    protected abstract void fetchEntry();

    @Override
    protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p) {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        } else {
            if (this.archive != null) {
                ArchiveResource.pushAndInvokeCircularReferenceCheck(this.archive, stk, p);
            }
            this.setChecked(true);
        }
    }

    @Override
    protected ArchiveResource getRef() {
        return this.getCheckedRef(ArchiveResource.class);
    }
}

