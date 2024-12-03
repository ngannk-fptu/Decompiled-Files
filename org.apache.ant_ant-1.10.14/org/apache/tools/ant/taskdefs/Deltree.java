/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

@Deprecated
public class Deltree
extends Task {
    private File dir;

    public void setDir(File dir) {
        this.dir = dir;
    }

    @Override
    public void execute() throws BuildException {
        this.log("DEPRECATED - The deltree task is deprecated.  Use delete instead.");
        if (this.dir == null) {
            throw new BuildException("dir attribute must be set!", this.getLocation());
        }
        if (this.dir.exists()) {
            if (!this.dir.isDirectory()) {
                if (!this.dir.delete()) {
                    throw new BuildException("Unable to delete directory " + this.dir.getAbsolutePath(), this.getLocation());
                }
                return;
            }
            this.log("Deleting: " + this.dir.getAbsolutePath());
            this.removeDir(this.dir);
        }
    }

    private void removeDir(File dir) {
        for (String s : dir.list()) {
            File f = new File(dir, s);
            if (f.isDirectory()) {
                this.removeDir(f);
                continue;
            }
            if (f.delete()) continue;
            throw new BuildException("Unable to delete file " + f.getAbsolutePath());
        }
        if (!dir.delete()) {
            throw new BuildException("Unable to delete directory " + dir.getAbsolutePath());
        }
    }
}

