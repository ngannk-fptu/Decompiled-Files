/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.AbstractFileSet;
import org.apache.tools.ant.types.ArchiveFileSet;
import org.apache.tools.ant.types.ArchiveScanner;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.ZipScanner;

public class ZipFileSet
extends ArchiveFileSet {
    public ZipFileSet() {
    }

    protected ZipFileSet(FileSet fileset) {
        super(fileset);
    }

    protected ZipFileSet(ZipFileSet fileset) {
        super(fileset);
    }

    @Override
    protected ArchiveScanner newArchiveScanner() {
        ZipScanner zs = new ZipScanner();
        zs.setEncoding(this.getEncoding());
        return zs;
    }

    @Override
    protected AbstractFileSet getRef(Project p) {
        this.dieOnCircularReference(p);
        Object o = this.getRefid().getReferencedObject(p);
        if (o instanceof ZipFileSet) {
            return (AbstractFileSet)o;
        }
        if (o instanceof FileSet) {
            ZipFileSet zfs = new ZipFileSet((FileSet)o);
            this.configureFileSet(zfs);
            return zfs;
        }
        String msg = this.getRefid().getRefId() + " doesn't denote a zipfileset or a fileset";
        throw new BuildException(msg);
    }

    @Override
    protected AbstractFileSet getRef() {
        return this.getRef(this.getProject());
    }

    @Override
    public Object clone() {
        if (this.isReference()) {
            return this.getRef().clone();
        }
        return super.clone();
    }
}

