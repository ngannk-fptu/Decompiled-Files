/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResourceIterator;

public class FileList
extends DataType
implements ResourceCollection {
    private List<String> filenames = new ArrayList<String>();
    private File dir;

    public FileList() {
    }

    protected FileList(FileList filelist) {
        this.dir = filelist.dir;
        this.filenames = filelist.filenames;
        this.setProject(filelist.getProject());
    }

    @Override
    public void setRefid(Reference r) throws BuildException {
        if (this.dir != null || !this.filenames.isEmpty()) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }

    public void setDir(File dir) throws BuildException {
        this.checkAttributesAllowed();
        this.dir = dir;
    }

    public File getDir(Project p) {
        if (this.isReference()) {
            return this.getRef(p).getDir(p);
        }
        return this.dir;
    }

    public void setFiles(String filenames) {
        this.checkAttributesAllowed();
        if (filenames != null && !filenames.isEmpty()) {
            StringTokenizer tok = new StringTokenizer(filenames, ", \t\n\r\f", false);
            while (tok.hasMoreTokens()) {
                this.filenames.add(tok.nextToken());
            }
        }
    }

    public String[] getFiles(Project p) {
        if (this.isReference()) {
            return this.getRef(p).getFiles(p);
        }
        if (this.dir == null) {
            throw new BuildException("No directory specified for filelist.");
        }
        if (this.filenames.isEmpty()) {
            throw new BuildException("No files specified for filelist.");
        }
        return this.filenames.toArray(new String[0]);
    }

    public void addConfiguredFile(FileName name) {
        if (name.getName() == null) {
            throw new BuildException("No name specified in nested file element");
        }
        this.filenames.add(name.getName());
    }

    @Override
    public Iterator<Resource> iterator() {
        if (this.isReference()) {
            return this.getRef().iterator();
        }
        return new FileResourceIterator(this.getProject(), this.dir, this.filenames.toArray(new String[0]));
    }

    @Override
    public int size() {
        if (this.isReference()) {
            return this.getRef().size();
        }
        return this.filenames.size();
    }

    @Override
    public boolean isFilesystemOnly() {
        return true;
    }

    private FileList getRef() {
        return this.getCheckedRef(FileList.class);
    }

    private FileList getRef(Project p) {
        return this.getCheckedRef(FileList.class, this.getDataTypeName(), p);
    }

    public static class FileName {
        private String name;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}

