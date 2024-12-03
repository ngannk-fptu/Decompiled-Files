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

public class FileSet
extends AbstractFileSet
implements ResourceCollection {
    public FileSet() {
    }

    protected FileSet(FileSet fileset) {
        super(fileset);
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
        return new FileResourceIterator(this.getProject(), this.getDir(this.getProject()), this.getDirectoryScanner().getIncludedFiles());
    }

    @Override
    public int size() {
        if (this.isReference()) {
            return this.getRef().size();
        }
        return this.getDirectoryScanner().getIncludedFilesCount();
    }

    @Override
    public boolean isFilesystemOnly() {
        return true;
    }

    @Override
    protected AbstractFileSet getRef(Project p) {
        return this.getCheckedRef(FileSet.class, this.getDataTypeName(), p);
    }

    private FileSet getRef() {
        return this.getCheckedRef(FileSet.class);
    }
}

