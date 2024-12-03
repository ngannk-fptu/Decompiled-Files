/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.extension;

import java.io.File;
import java.util.List;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.optional.extension.LibraryDisplayer;
import org.apache.tools.ant.types.FileSet;

public class JarLibDisplayTask
extends Task {
    private File libraryFile;
    private final List<FileSet> libraryFileSets = new Vector<FileSet>();

    public void setFile(File file) {
        this.libraryFile = file;
    }

    public void addFileset(FileSet fileSet) {
        this.libraryFileSets.add(fileSet);
    }

    @Override
    public void execute() throws BuildException {
        this.validate();
        LibraryDisplayer displayer = new LibraryDisplayer();
        if (this.libraryFileSets.isEmpty()) {
            displayer.displayLibrary(this.libraryFile);
        } else {
            for (FileSet fileSet : this.libraryFileSets) {
                DirectoryScanner scanner = fileSet.getDirectoryScanner(this.getProject());
                File basedir = scanner.getBasedir();
                for (String filename : scanner.getIncludedFiles()) {
                    displayer.displayLibrary(new File(basedir, filename));
                }
            }
        }
    }

    private void validate() throws BuildException {
        if (null == this.libraryFile) {
            if (this.libraryFileSets.isEmpty()) {
                throw new BuildException("File attribute not specified.");
            }
        } else {
            if (!this.libraryFile.exists()) {
                throw new BuildException("File '%s' does not exist.", this.libraryFile);
            }
            if (!this.libraryFile.isFile()) {
                throw new BuildException("'%s' is not a file.", this.libraryFile);
            }
        }
    }
}

