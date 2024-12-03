/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

@Deprecated
public class Copyfile
extends Task {
    private File srcFile;
    private File destFile;
    private boolean filtering = false;
    private boolean forceOverwrite = false;

    public void setSrc(File src) {
        this.srcFile = src;
    }

    public void setForceoverwrite(boolean force) {
        this.forceOverwrite = force;
    }

    public void setDest(File dest) {
        this.destFile = dest;
    }

    public void setFiltering(String filter) {
        this.filtering = Project.toBoolean(filter);
    }

    @Override
    public void execute() throws BuildException {
        this.log("DEPRECATED - The copyfile task is deprecated.  Use copy instead.");
        if (this.srcFile == null) {
            throw new BuildException("The src attribute must be present.", this.getLocation());
        }
        if (!this.srcFile.exists()) {
            throw new BuildException("src " + this.srcFile.toString() + " does not exist.", this.getLocation());
        }
        if (this.destFile == null) {
            throw new BuildException("The dest attribute must be present.", this.getLocation());
        }
        if (this.srcFile.equals(this.destFile)) {
            this.log("Warning: src == dest", 1);
        }
        if (this.forceOverwrite || this.srcFile.lastModified() > this.destFile.lastModified()) {
            try {
                this.getProject().copyFile(this.srcFile, this.destFile, this.filtering, this.forceOverwrite);
            }
            catch (IOException ioe) {
                throw new BuildException("Error copying file: " + this.srcFile.getAbsolutePath() + " due to " + ioe.getMessage());
            }
        }
    }
}

