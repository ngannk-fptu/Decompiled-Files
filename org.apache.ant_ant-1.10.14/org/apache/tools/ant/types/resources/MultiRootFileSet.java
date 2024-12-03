/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.AbstractFileSet;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.FileResourceIterator;
import org.apache.tools.ant.types.resources.Union;

public class MultiRootFileSet
extends AbstractFileSet
implements ResourceCollection {
    private SetType type = SetType.file;
    private boolean cache = true;
    private List<File> baseDirs = new ArrayList<File>();
    private Union union;

    @Override
    public void setDir(File dir) {
        throw new BuildException(this.getDataTypeName() + " doesn't support the dir attribute");
    }

    public void setType(SetType type) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.type = type;
    }

    public synchronized void setCache(boolean b) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.cache = b;
    }

    public void setBaseDirs(String dirs) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        if (dirs != null && !dirs.isEmpty()) {
            for (String d : dirs.split(",")) {
                this.baseDirs.add(this.getProject().resolveFile(d));
            }
        }
    }

    public void addConfiguredBaseDir(FileResource r) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.baseDirs.add(r.getFile());
    }

    @Override
    public void setRefid(Reference r) {
        if (!this.baseDirs.isEmpty()) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }

    @Override
    public Object clone() {
        if (this.isReference()) {
            return this.getRef().clone();
        }
        MultiRootFileSet fs = (MultiRootFileSet)super.clone();
        fs.baseDirs = new ArrayList<File>(this.baseDirs);
        fs.union = null;
        return fs;
    }

    @Override
    public Iterator<Resource> iterator() {
        if (this.isReference()) {
            return this.getRef().iterator();
        }
        return this.merge().iterator();
    }

    @Override
    public int size() {
        if (this.isReference()) {
            return this.getRef().size();
        }
        return this.merge().size();
    }

    @Override
    public boolean isFilesystemOnly() {
        return true;
    }

    @Override
    public String toString() {
        if (this.isReference()) {
            return this.getRef().toString();
        }
        return this.merge().toString();
    }

    private MultiRootFileSet getRef() {
        return this.getCheckedRef(MultiRootFileSet.class);
    }

    private synchronized Union merge() {
        if (this.cache && this.union != null) {
            return this.union;
        }
        Union u = new Union();
        this.setup(u);
        if (this.cache) {
            this.union = u;
        }
        return u;
    }

    private void setup(Union u) {
        for (File d : this.baseDirs) {
            u.add(new Worker(this, this.type, d));
        }
    }

    public static enum SetType {
        file,
        dir,
        both;

    }

    private static class Worker
    extends AbstractFileSet
    implements ResourceCollection {
        private final SetType type;

        private Worker(MultiRootFileSet fs, SetType type, File dir) {
            super(fs);
            this.type = type;
            this.setDir(dir);
        }

        @Override
        public boolean isFilesystemOnly() {
            return true;
        }

        @Override
        public Iterator<Resource> iterator() {
            String[] names;
            DirectoryScanner ds = this.getDirectoryScanner();
            String[] stringArray = names = this.type == SetType.file ? ds.getIncludedFiles() : ds.getIncludedDirectories();
            if (this.type == SetType.both) {
                String[] files = ds.getIncludedFiles();
                String[] merged = new String[names.length + files.length];
                System.arraycopy(names, 0, merged, 0, names.length);
                System.arraycopy(files, 0, merged, names.length, files.length);
                names = merged;
            }
            return new FileResourceIterator(this.getProject(), this.getDir(this.getProject()), names);
        }

        @Override
        public int size() {
            int count;
            DirectoryScanner ds = this.getDirectoryScanner();
            int n = count = this.type == SetType.file ? ds.getIncludedFilesCount() : ds.getIncludedDirsCount();
            if (this.type == SetType.both) {
                count += ds.getIncludedFilesCount();
            }
            return count;
        }
    }
}

