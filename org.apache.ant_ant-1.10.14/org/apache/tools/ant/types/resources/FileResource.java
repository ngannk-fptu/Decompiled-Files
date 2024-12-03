/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceFactory;
import org.apache.tools.ant.types.resources.Appendable;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.Touchable;
import org.apache.tools.ant.util.FileUtils;

public class FileResource
extends Resource
implements Touchable,
FileProvider,
ResourceFactory,
Appendable {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final int NULL_FILE = Resource.getMagicNumber("null file".getBytes());
    private File file;
    private File baseDir;

    public FileResource() {
    }

    public FileResource(File b, String name) {
        this.baseDir = b;
        this.file = FILE_UTILS.resolveFile(b, name);
    }

    public FileResource(File f) {
        this.setFile(f);
    }

    public FileResource(Project p, File f) {
        this(f);
        this.setProject(p);
    }

    public FileResource(Project p, String s) {
        this(p, p.resolveFile(s));
    }

    public void setFile(File f) {
        this.checkAttributesAllowed();
        this.file = f;
        if (!(f == null || this.getBaseDir() != null && FILE_UTILS.isLeadingPath(this.getBaseDir(), f))) {
            this.setBaseDir(f.getParentFile());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public File getFile() {
        if (this.isReference()) {
            return this.getRef().getFile();
        }
        this.dieOnCircularReference();
        FileResource fileResource = this;
        synchronized (fileResource) {
            if (this.file == null) {
                File d = this.getBaseDir();
                String n = super.getName();
                if (n != null) {
                    this.setFile(FILE_UTILS.resolveFile(d, n));
                }
            }
        }
        return this.file;
    }

    public void setBaseDir(File b) {
        this.checkAttributesAllowed();
        this.baseDir = b;
    }

    public File getBaseDir() {
        if (this.isReference()) {
            return this.getRef().getBaseDir();
        }
        this.dieOnCircularReference();
        return this.baseDir;
    }

    @Override
    public void setRefid(Reference r) {
        if (this.file != null || this.baseDir != null) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }

    @Override
    public String getName() {
        if (this.isReference()) {
            return this.getRef().getName();
        }
        File b = this.getBaseDir();
        return b == null ? this.getNotNullFile().getName() : FILE_UTILS.removeLeadingPath(b, this.getNotNullFile());
    }

    @Override
    public boolean isExists() {
        return this.isReference() ? this.getRef().isExists() : this.getNotNullFile().exists();
    }

    @Override
    public long getLastModified() {
        return this.isReference() ? this.getRef().getLastModified() : this.getNotNullFile().lastModified();
    }

    @Override
    public boolean isDirectory() {
        return this.isReference() ? this.getRef().isDirectory() : this.getNotNullFile().isDirectory();
    }

    @Override
    public long getSize() {
        return this.isReference() ? this.getRef().getSize() : this.getNotNullFile().length();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.isReference() ? this.getRef().getInputStream() : Files.newInputStream(this.getNotNullFile().toPath(), new OpenOption[0]);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (this.isReference()) {
            return this.getRef().getOutputStream();
        }
        return this.getOutputStream(false);
    }

    @Override
    public OutputStream getAppendOutputStream() throws IOException {
        if (this.isReference()) {
            return this.getRef().getAppendOutputStream();
        }
        return this.getOutputStream(true);
    }

    private OutputStream getOutputStream(boolean append) throws IOException {
        File f = this.getNotNullFile();
        if (f.exists()) {
            if (Files.isSymbolicLink(f.toPath()) && f.isFile() && !append) {
                f.delete();
            }
        } else {
            File p = f.getParentFile();
            if (p != null && !p.exists()) {
                p.mkdirs();
            }
        }
        return FileUtils.newOutputStream(f.toPath(), append);
    }

    @Override
    public int compareTo(Resource another) {
        if (this.isReference()) {
            return this.getRef().compareTo(another);
        }
        if (this.equals(another)) {
            return 0;
        }
        FileProvider otherFP = another.as(FileProvider.class);
        if (otherFP != null) {
            File f = this.getFile();
            if (f == null) {
                return -1;
            }
            File of = otherFP.getFile();
            if (of == null) {
                return 1;
            }
            int compareFiles = f.compareTo(of);
            return compareFiles != 0 ? compareFiles : this.getName().compareTo(another.getName());
        }
        return super.compareTo(another);
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
        FileResource otherfr = (FileResource)another;
        return this.getFile() == null ? otherfr.getFile() == null : this.getFile().equals(otherfr.getFile()) && this.getName().equals(otherfr.getName());
    }

    @Override
    public int hashCode() {
        if (this.isReference()) {
            return this.getRef().hashCode();
        }
        return MAGIC * (this.getFile() == null ? NULL_FILE : this.getFile().hashCode());
    }

    @Override
    public String toString() {
        if (this.isReference()) {
            return this.getRef().toString();
        }
        if (this.file == null) {
            return "(unbound file resource)";
        }
        String absolutePath = this.file.getAbsolutePath();
        return FILE_UTILS.normalize(absolutePath).getAbsolutePath();
    }

    @Override
    public boolean isFilesystemOnly() {
        if (this.isReference()) {
            return this.getRef().isFilesystemOnly();
        }
        this.dieOnCircularReference();
        return true;
    }

    @Override
    public void touch(long modTime) {
        if (this.isReference()) {
            this.getRef().touch(modTime);
            return;
        }
        if (!this.getNotNullFile().setLastModified(modTime)) {
            this.log("Failed to change file modification time", 1);
        }
    }

    protected File getNotNullFile() {
        if (this.getFile() == null) {
            throw new BuildException("file attribute is null!");
        }
        this.dieOnCircularReference();
        return this.getFile();
    }

    @Override
    public Resource getResource(String path) {
        File newfile = FILE_UTILS.resolveFile(this.getFile(), path);
        FileResource fileResource = new FileResource(newfile);
        if (FILE_UTILS.isLeadingPath(this.getBaseDir(), newfile)) {
            fileResource.setBaseDir(this.getBaseDir());
        }
        return fileResource;
    }

    @Override
    protected FileResource getRef() {
        return this.getCheckedRef(FileResource.class);
    }
}

