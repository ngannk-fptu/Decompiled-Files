/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 *  org.apache.tools.ant.Task
 *  org.apache.tools.ant.types.FileSet
 */
package org.jboss.jandex;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.jboss.jandex.Indexer;
import org.jboss.jandex.JarIndexer;

public class JandexAntTask
extends Task {
    private final List<FileSet> filesets = new ArrayList<FileSet>();
    private boolean modify = false;
    private boolean newJar = false;
    private boolean verbose = false;
    private boolean run = true;

    public void execute() throws BuildException {
        if (!this.run) {
            return;
        }
        if (this.modify && this.newJar) {
            throw new BuildException("Specifying both modify and newJar does not make sense.");
        }
        Indexer indexer = new Indexer();
        for (FileSet fileset : this.filesets) {
            String[] files;
            for (String file : files = fileset.getDirectoryScanner(this.getProject()).getIncludedFiles()) {
                if (!file.endsWith(".jar")) continue;
                try {
                    JarIndexer.createJarIndex(new File(fileset.getDir().getAbsolutePath() + "/" + file), indexer, this.modify, this.newJar, this.verbose);
                }
                catch (IOException e) {
                    throw new BuildException((Throwable)e);
                }
            }
        }
    }

    public void addFileset(FileSet fileset) {
        this.filesets.add(fileset);
    }

    public boolean isModify() {
        return this.modify;
    }

    public void setModify(boolean modify) {
        this.modify = modify;
    }

    public boolean isVerbose() {
        return this.verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isRun() {
        return this.run;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    public boolean isNewJar() {
        return this.newJar;
    }

    public void setNewJar(boolean newJar) {
        this.newJar = newJar;
    }
}

