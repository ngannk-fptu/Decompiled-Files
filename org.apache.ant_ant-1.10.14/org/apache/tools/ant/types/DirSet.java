/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.util.Iterator;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.AbstractFileSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResourceIterator;

public class DirSet
extends AbstractFileSet
implements ResourceCollection {
    public DirSet() {
    }

    protected DirSet(DirSet dirset) {
        super(dirset);
    }

    @Override
    public Object clone() {
        if (this.isReference()) {
            return this.getRef().clone();
        }
        return super.clone();
    }

    @Override
    public Iterator<Resource> iterator() {
        if (this.isReference()) {
            return this.getRef().iterator();
        }
        return new FileResourceIterator(this.getProject(), this.getDir(this.getProject()), this.getDirectoryScanner().getIncludedDirectories());
    }

    @Override
    public int size() {
        if (this.isReference()) {
            return this.getRef().size();
        }
        return this.getDirectoryScanner().getIncludedDirsCount();
    }

    @Override
    public boolean isFilesystemOnly() {
        return true;
    }

    @Override
    public String toString() {
        return String.join((CharSequence)";", this.getDirectoryScanner().getIncludedDirectories());
    }

    @Override
    protected AbstractFileSet getRef(Project p) {
        return this.getCheckedRef(DirSet.class, this.getDataTypeName(), p);
    }

    private DirSet getRef() {
        return this.getCheckedRef(DirSet.class);
    }
}

