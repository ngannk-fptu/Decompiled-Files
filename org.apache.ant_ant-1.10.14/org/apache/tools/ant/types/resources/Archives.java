/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import java.util.stream.Stream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.ArchiveFileSet;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.TarFileSet;
import org.apache.tools.ant.types.ZipFileSet;
import org.apache.tools.ant.types.resources.Union;

public class Archives
extends DataType
implements ResourceCollection,
Cloneable {
    private Union zips = new Union();
    private Union tars = new Union();

    public Union createZips() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        return this.zips;
    }

    public Union createTars() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        return this.tars;
    }

    @Override
    public int size() {
        if (this.isReference()) {
            return this.getRef().size();
        }
        this.dieOnCircularReference();
        return this.streamArchives().mapToInt(ArchiveFileSet::size).sum();
    }

    @Override
    public Iterator<Resource> iterator() {
        if (this.isReference()) {
            return this.getRef().iterator();
        }
        this.dieOnCircularReference();
        return this.streamArchives().flatMap(ResourceCollection::stream).map(Resource.class::cast).iterator();
    }

    @Override
    public boolean isFilesystemOnly() {
        if (this.isReference()) {
            return this.getRef().isFilesystemOnly();
        }
        this.dieOnCircularReference();
        return false;
    }

    @Override
    public void setRefid(Reference r) {
        if (!this.zips.getResourceCollections().isEmpty() || !this.tars.getResourceCollections().isEmpty()) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }

    @Override
    public Object clone() {
        try {
            Archives a = (Archives)super.clone();
            a.zips = (Union)this.zips.clone();
            a.tars = (Union)this.tars.clone();
            return a;
        }
        catch (CloneNotSupportedException e) {
            throw new BuildException(e);
        }
    }

    protected Iterator<ArchiveFileSet> grabArchives() {
        return this.streamArchives().iterator();
    }

    private Stream<ArchiveFileSet> streamArchives() {
        LinkedList<ArchiveFileSet> l = new LinkedList<ArchiveFileSet>();
        for (Resource r : this.zips) {
            l.add(this.configureArchive(new ZipFileSet(), r));
        }
        for (Resource r : this.tars) {
            l.add(this.configureArchive(new TarFileSet(), r));
        }
        return l.stream();
    }

    protected ArchiveFileSet configureArchive(ArchiveFileSet afs, Resource src) {
        afs.setProject(this.getProject());
        afs.setSrcResource(src);
        return afs;
    }

    @Override
    protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        } else {
            Archives.pushAndInvokeCircularReferenceCheck(this.zips, stk, p);
            Archives.pushAndInvokeCircularReferenceCheck(this.tars, stk, p);
            this.setChecked(true);
        }
    }

    private Archives getRef() {
        return this.getCheckedRef(Archives.class);
    }
}

