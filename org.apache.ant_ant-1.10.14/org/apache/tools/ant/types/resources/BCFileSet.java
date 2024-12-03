/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.util.Iterator;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResourceIterator;

public class BCFileSet
extends FileSet {
    public BCFileSet() {
    }

    public BCFileSet(FileSet fs) {
        super(fs);
    }

    @Override
    public Iterator<Resource> iterator() {
        if (this.isReference()) {
            return this.getRef().iterator();
        }
        FileResourceIterator result = new FileResourceIterator(this.getProject(), this.getDir());
        result.addFiles(this.getDirectoryScanner().getIncludedFiles());
        result.addFiles(this.getDirectoryScanner().getIncludedDirectories());
        return result;
    }

    @Override
    public int size() {
        if (this.isReference()) {
            return this.getRef().size();
        }
        return this.getDirectoryScanner().getIncludedFilesCount() + this.getDirectoryScanner().getIncludedDirsCount();
    }

    private FileSet getRef() {
        return this.getCheckedRef(FileSet.class);
    }
}

