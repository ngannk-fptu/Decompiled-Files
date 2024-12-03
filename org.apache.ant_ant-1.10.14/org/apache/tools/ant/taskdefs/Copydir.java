/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;

@Deprecated
public class Copydir
extends MatchingTask {
    private File srcDir;
    private File destDir;
    private boolean filtering = false;
    private boolean flatten = false;
    private boolean forceOverwrite = false;
    private Map<String, String> filecopyList = new Hashtable<String, String>();

    public void setSrc(File src) {
        this.srcDir = src;
    }

    public void setDest(File dest) {
        this.destDir = dest;
    }

    public void setFiltering(boolean filter) {
        this.filtering = filter;
    }

    public void setFlatten(boolean flatten) {
        this.flatten = flatten;
    }

    public void setForceoverwrite(boolean force) {
        this.forceOverwrite = force;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void execute() throws BuildException {
        this.log("DEPRECATED - The copydir task is deprecated.  Use copy instead.");
        if (this.srcDir == null) {
            throw new BuildException("src attribute must be set!", this.getLocation());
        }
        if (!this.srcDir.exists()) {
            throw new BuildException("srcdir " + this.srcDir.toString() + " does not exist!", this.getLocation());
        }
        if (this.destDir == null) {
            throw new BuildException("The dest attribute must be set.", this.getLocation());
        }
        if (this.srcDir.equals(this.destDir)) {
            this.log("Warning: src == dest", 1);
        }
        DirectoryScanner ds = super.getDirectoryScanner(this.srcDir);
        try {
            this.scanDir(this.srcDir, this.destDir, ds.getIncludedFiles());
            if (this.filecopyList.size() <= 0) return;
            this.log("Copying " + this.filecopyList.size() + " file" + (this.filecopyList.size() == 1 ? "" : "s") + " to " + this.destDir.getAbsolutePath());
            for (Map.Entry<String, String> e : this.filecopyList.entrySet()) {
                String fromFile = e.getKey();
                String toFile = e.getValue();
                try {
                    this.getProject().copyFile(fromFile, toFile, this.filtering, this.forceOverwrite);
                }
                catch (IOException ioe) {
                    String msg = "Failed to copy " + fromFile + " to " + toFile + " due to " + ioe.getMessage();
                    throw new BuildException(msg, ioe, this.getLocation());
                    return;
                }
            }
        }
        finally {
            this.filecopyList.clear();
        }
    }

    private void scanDir(File from, File to, String[] files) {
        for (String filename : files) {
            File srcFile = new File(from, filename);
            File destFile = this.flatten ? new File(to, new File(filename).getName()) : new File(to, filename);
            if (!this.forceOverwrite && srcFile.lastModified() <= destFile.lastModified()) continue;
            this.filecopyList.put(srcFile.getAbsolutePath(), destFile.getAbsolutePath());
        }
    }
}

