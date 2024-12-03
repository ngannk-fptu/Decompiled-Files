/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.io.File;
import java.util.Iterator;
import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.AbstractFileSet;
import org.apache.tools.ant.types.ArchiveScanner;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;

public abstract class ArchiveFileSet
extends FileSet {
    private static final int BASE_OCTAL = 8;
    public static final int DEFAULT_DIR_MODE = 16877;
    public static final int DEFAULT_FILE_MODE = 33188;
    private Resource src = null;
    private String prefix = "";
    private String fullpath = "";
    private boolean hasDir = false;
    private int fileMode = 33188;
    private int dirMode = 16877;
    private boolean fileModeHasBeenSet = false;
    private boolean dirModeHasBeenSet = false;
    private static final String ERROR_DIR_AND_SRC_ATTRIBUTES = "Cannot set both dir and src attributes";
    private static final String ERROR_PATH_AND_PREFIX = "Cannot set both fullpath and prefix attributes";
    private boolean errorOnMissingArchive = true;
    private String encoding = null;

    public ArchiveFileSet() {
    }

    protected ArchiveFileSet(FileSet fileset) {
        super(fileset);
    }

    protected ArchiveFileSet(ArchiveFileSet fileset) {
        super(fileset);
        this.src = fileset.src;
        this.prefix = fileset.prefix;
        this.fullpath = fileset.fullpath;
        this.hasDir = fileset.hasDir;
        this.fileMode = fileset.fileMode;
        this.dirMode = fileset.dirMode;
        this.fileModeHasBeenSet = fileset.fileModeHasBeenSet;
        this.dirModeHasBeenSet = fileset.dirModeHasBeenSet;
        this.errorOnMissingArchive = fileset.errorOnMissingArchive;
        this.encoding = fileset.encoding;
    }

    @Override
    public void setDir(File dir) throws BuildException {
        this.checkAttributesAllowed();
        if (this.src != null) {
            throw new BuildException(ERROR_DIR_AND_SRC_ATTRIBUTES);
        }
        super.setDir(dir);
        this.hasDir = true;
    }

    public void addConfigured(ResourceCollection a) {
        this.checkChildrenAllowed();
        if (a.size() != 1) {
            throw new BuildException("only single argument resource collections are supported as archives");
        }
        this.setSrcResource((Resource)a.iterator().next());
    }

    public void setSrc(File srcFile) {
        this.setSrcResource(new FileResource(srcFile));
    }

    public void setSrcResource(Resource src) {
        this.checkArchiveAttributesAllowed();
        if (this.hasDir) {
            throw new BuildException(ERROR_DIR_AND_SRC_ATTRIBUTES);
        }
        this.src = src;
        this.setChecked(false);
    }

    public File getSrc(Project p) {
        if (this.isReference()) {
            return ((ArchiveFileSet)this.getRef(p)).getSrc(p);
        }
        return this.getSrc();
    }

    public void setErrorOnMissingArchive(boolean errorOnMissingArchive) {
        this.checkAttributesAllowed();
        this.errorOnMissingArchive = errorOnMissingArchive;
    }

    public File getSrc() {
        if (this.isReference()) {
            return this.getCheckedRef(ArchiveFileSet.class).getSrc();
        }
        this.dieOnCircularReference();
        if (this.src == null) {
            return null;
        }
        return this.src.asOptional(FileProvider.class).map(FileProvider::getFile).orElse(null);
    }

    protected AbstractFileSet getRef() {
        return this.getCheckedRef(AbstractFileSet.class);
    }

    public void setPrefix(String prefix) {
        this.checkArchiveAttributesAllowed();
        if (!prefix.isEmpty() && !this.fullpath.isEmpty()) {
            throw new BuildException(ERROR_PATH_AND_PREFIX);
        }
        this.prefix = prefix;
    }

    public String getPrefix(Project p) {
        if (this.isReference()) {
            return ((ArchiveFileSet)this.getRef(p)).getPrefix(p);
        }
        this.dieOnCircularReference(p);
        return this.prefix;
    }

    public void setFullpath(String fullpath) {
        this.checkArchiveAttributesAllowed();
        if (!this.prefix.isEmpty() && !fullpath.isEmpty()) {
            throw new BuildException(ERROR_PATH_AND_PREFIX);
        }
        this.fullpath = fullpath;
    }

    public String getFullpath(Project p) {
        if (this.isReference()) {
            return ((ArchiveFileSet)this.getRef(p)).getFullpath(p);
        }
        this.dieOnCircularReference(p);
        return this.fullpath;
    }

    public void setEncoding(String enc) {
        this.checkAttributesAllowed();
        this.encoding = enc;
    }

    public String getEncoding() {
        if (this.isReference()) {
            AbstractFileSet ref = this.getRef();
            return ref instanceof ArchiveFileSet ? ((ArchiveFileSet)ref).getEncoding() : null;
        }
        return this.encoding;
    }

    protected abstract ArchiveScanner newArchiveScanner();

    @Override
    public DirectoryScanner getDirectoryScanner(Project p) {
        if (this.isReference()) {
            return this.getRef(p).getDirectoryScanner(p);
        }
        this.dieOnCircularReference();
        if (this.src == null) {
            return super.getDirectoryScanner(p);
        }
        if (!this.src.isExists() && this.errorOnMissingArchive) {
            throw new BuildException("The archive " + this.src.getName() + " doesn't exist");
        }
        if (this.src.isDirectory()) {
            throw new BuildException("The archive " + this.src.getName() + " can't be a directory");
        }
        ArchiveScanner as = this.newArchiveScanner();
        as.setErrorOnMissingArchive(this.errorOnMissingArchive);
        as.setSrc(this.src);
        super.setDir(p.getBaseDir());
        this.setupDirectoryScanner(as, p);
        as.init();
        return as;
    }

    @Override
    public Iterator<Resource> iterator() {
        if (this.isReference()) {
            return ((ResourceCollection)((Object)this.getRef())).iterator();
        }
        if (this.src == null) {
            return super.iterator();
        }
        return ((ArchiveScanner)this.getDirectoryScanner()).getResourceFiles(this.getProject());
    }

    @Override
    public int size() {
        if (this.isReference()) {
            return ((ResourceCollection)((Object)this.getRef())).size();
        }
        if (this.src == null) {
            return super.size();
        }
        return this.getDirectoryScanner().getIncludedFilesCount();
    }

    @Override
    public boolean isFilesystemOnly() {
        if (this.isReference()) {
            return ((ArchiveFileSet)this.getRef()).isFilesystemOnly();
        }
        this.dieOnCircularReference();
        return this.src == null;
    }

    public void setFileMode(String octalString) {
        this.checkArchiveAttributesAllowed();
        this.integerSetFileMode(Integer.parseInt(octalString, 8));
    }

    public void integerSetFileMode(int mode) {
        this.fileModeHasBeenSet = true;
        this.fileMode = 0x8000 | mode;
    }

    public int getFileMode(Project p) {
        if (this.isReference()) {
            return ((ArchiveFileSet)this.getRef(p)).getFileMode(p);
        }
        this.dieOnCircularReference();
        return this.fileMode;
    }

    public boolean hasFileModeBeenSet() {
        if (this.isReference()) {
            return ((ArchiveFileSet)this.getRef()).hasFileModeBeenSet();
        }
        this.dieOnCircularReference();
        return this.fileModeHasBeenSet;
    }

    public void setDirMode(String octalString) {
        this.checkArchiveAttributesAllowed();
        this.integerSetDirMode(Integer.parseInt(octalString, 8));
    }

    public void integerSetDirMode(int mode) {
        this.dirModeHasBeenSet = true;
        this.dirMode = 0x4000 | mode;
    }

    public int getDirMode(Project p) {
        if (this.isReference()) {
            return ((ArchiveFileSet)this.getRef(p)).getDirMode(p);
        }
        this.dieOnCircularReference();
        return this.dirMode;
    }

    public boolean hasDirModeBeenSet() {
        if (this.isReference()) {
            return ((ArchiveFileSet)this.getRef()).hasDirModeBeenSet();
        }
        this.dieOnCircularReference();
        return this.dirModeHasBeenSet;
    }

    protected void configureFileSet(ArchiveFileSet zfs) {
        zfs.setPrefix(this.prefix);
        zfs.setFullpath(this.fullpath);
        zfs.fileModeHasBeenSet = this.fileModeHasBeenSet;
        zfs.fileMode = this.fileMode;
        zfs.dirModeHasBeenSet = this.dirModeHasBeenSet;
        zfs.dirMode = this.dirMode;
    }

    @Override
    public Object clone() {
        if (this.isReference()) {
            return this.getCheckedRef(ArchiveFileSet.class).clone();
        }
        return super.clone();
    }

    @Override
    public String toString() {
        if (this.hasDir && this.getProject() != null) {
            return super.toString();
        }
        return this.src == null ? null : this.src.getName();
    }

    @Deprecated
    public String getPrefix() {
        return this.prefix;
    }

    @Deprecated
    public String getFullpath() {
        return this.fullpath;
    }

    @Deprecated
    public int getFileMode() {
        return this.fileMode;
    }

    @Deprecated
    public int getDirMode() {
        return this.dirMode;
    }

    private void checkArchiveAttributesAllowed() {
        if (this.getProject() == null || this.isReference() && this.getRefid().getReferencedObject(this.getProject()) instanceof ArchiveFileSet) {
            this.checkAttributesAllowed();
        }
    }

    @Override
    protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        super.dieOnCircularReference(stk, p);
        if (!this.isReference()) {
            if (this.src != null) {
                ArchiveFileSet.pushAndInvokeCircularReferenceCheck(this.src, stk, p);
            }
            this.setChecked(true);
        }
    }
}

