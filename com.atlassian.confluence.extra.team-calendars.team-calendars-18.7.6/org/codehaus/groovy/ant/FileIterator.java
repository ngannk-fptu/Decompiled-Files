/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.DirectoryScanner
 *  org.apache.tools.ant.Project
 *  org.apache.tools.ant.types.FileSet
 */
package org.codehaus.groovy.ant;

import java.io.File;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

public class FileIterator
implements Iterator {
    private Iterator<FileSet> fileSetIterator;
    private Project project;
    private DirectoryScanner ds;
    private String[] files;
    private int fileIndex = -1;
    private File nextFile;
    private boolean nextObjectSet = false;
    private boolean iterateDirectories = false;

    public FileIterator(Project project, Iterator<FileSet> fileSetIterator) {
        this(project, fileSetIterator, false);
    }

    public FileIterator(Project project, Iterator<FileSet> fileSetIterator, boolean iterateDirectories) {
        this.project = project;
        this.fileSetIterator = fileSetIterator;
        this.iterateDirectories = iterateDirectories;
    }

    @Override
    public boolean hasNext() {
        if (this.nextObjectSet) {
            return true;
        }
        return this.setNextObject();
    }

    public Object next() {
        if (!this.nextObjectSet && !this.setNextObject()) {
            throw new NoSuchElementException();
        }
        this.nextObjectSet = false;
        return this.nextFile;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private boolean setNextObject() {
        while (true) {
            if (this.ds == null) {
                if (!this.fileSetIterator.hasNext()) {
                    return false;
                }
                FileSet fs = this.fileSetIterator.next();
                this.ds = fs.getDirectoryScanner(this.project);
                this.ds.scan();
                this.files = this.iterateDirectories ? this.ds.getIncludedDirectories() : this.ds.getIncludedFiles();
                if (this.files.length > 0) {
                    this.fileIndex = -1;
                } else {
                    this.ds = null;
                    continue;
                }
            }
            if (this.ds == null || this.files == null) continue;
            if (++this.fileIndex < this.files.length) {
                this.nextFile = new File(this.ds.getBasedir(), this.files[this.fileIndex]);
                this.nextObjectSet = true;
                return true;
            }
            this.ds = null;
        }
    }
}

