/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.Project
 *  org.apache.tools.ant.Task
 *  org.apache.tools.ant.types.FileSet
 */
package org.codehaus.groovy.ant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.codehaus.groovy.ant.FileIterator;

public class FileScanner
extends Task {
    private List filesets = new ArrayList();

    public FileScanner() {
    }

    public FileScanner(Project project) {
        this.setProject(project);
    }

    public Iterator iterator() {
        return new FileIterator(this.getProject(), this.filesets.iterator());
    }

    public Iterator directories() {
        return new FileIterator(this.getProject(), this.filesets.iterator(), true);
    }

    public boolean hasFiles() {
        return !this.filesets.isEmpty();
    }

    public void clear() {
        this.filesets.clear();
    }

    public void addFileset(FileSet set) {
        this.filesets.add(set);
    }
}

