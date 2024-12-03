/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.File;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;

public class FileResourceIterator
implements Iterator<Resource> {
    private Project project;
    private File basedir;
    private String[] files;
    private int pos = 0;

    @Deprecated
    public FileResourceIterator() {
    }

    public FileResourceIterator(Project project) {
        this.project = project;
    }

    @Deprecated
    public FileResourceIterator(File basedir) {
        this(null, basedir);
    }

    public FileResourceIterator(Project project, File basedir) {
        this(project);
        this.basedir = basedir;
    }

    @Deprecated
    public FileResourceIterator(File basedir, String[] filenames) {
        this(null, basedir, filenames);
    }

    public FileResourceIterator(Project project, File basedir, String[] filenames) {
        this(project, basedir);
        this.addFiles(filenames);
    }

    public void addFiles(String[] s) {
        int start = this.files == null ? 0 : this.files.length;
        String[] newfiles = new String[start + s.length];
        if (start > 0) {
            System.arraycopy(this.files, 0, newfiles, 0, start);
        }
        this.files = newfiles;
        System.arraycopy(s, 0, this.files, start, s.length);
    }

    @Override
    public boolean hasNext() {
        return this.pos < this.files.length;
    }

    @Override
    public Resource next() {
        return this.nextResource();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public FileResource nextResource() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        FileResource result = new FileResource(this.basedir, this.files[this.pos++]);
        result.setProject(this.project);
        return result;
    }
}

