/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.jlink;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.taskdefs.optional.jlink.jlink;
import org.apache.tools.ant.types.Path;

public class JlinkTask
extends MatchingTask {
    private File outfile = null;
    private Path mergefiles = null;
    private Path addfiles = null;
    private boolean compress = false;

    public void setOutfile(File outfile) {
        this.outfile = outfile;
    }

    public Path createMergefiles() {
        if (this.mergefiles == null) {
            this.mergefiles = new Path(this.getProject());
        }
        return this.mergefiles.createPath();
    }

    public void setMergefiles(Path mergefiles) {
        if (this.mergefiles == null) {
            this.mergefiles = mergefiles;
        } else {
            this.mergefiles.append(mergefiles);
        }
    }

    public Path createAddfiles() {
        if (this.addfiles == null) {
            this.addfiles = new Path(this.getProject());
        }
        return this.addfiles.createPath();
    }

    public void setAddfiles(Path addfiles) {
        if (this.addfiles == null) {
            this.addfiles = addfiles;
        } else {
            this.addfiles.append(addfiles);
        }
    }

    public void setCompress(boolean compress) {
        this.compress = compress;
    }

    @Override
    public void execute() throws BuildException {
        if (this.outfile == null) {
            throw new BuildException("outfile attribute is required! Please set.");
        }
        if (!this.haveAddFiles() && !this.haveMergeFiles()) {
            throw new BuildException("addfiles or mergefiles required! Please set.");
        }
        this.log("linking:     " + this.outfile.getPath());
        this.log("compression: " + this.compress, 3);
        jlink linker = new jlink();
        linker.setOutfile(this.outfile.getPath());
        linker.setCompression(this.compress);
        if (this.haveMergeFiles()) {
            this.log("merge files: " + this.mergefiles.toString(), 3);
            linker.addMergeFiles(this.mergefiles.list());
        }
        if (this.haveAddFiles()) {
            this.log("add files: " + this.addfiles.toString(), 3);
            linker.addAddFiles(this.addfiles.list());
        }
        try {
            linker.link();
        }
        catch (Exception ex) {
            throw new BuildException(ex, this.getLocation());
        }
    }

    private boolean haveAddFiles() {
        return this.haveEntries(this.addfiles);
    }

    private boolean haveMergeFiles() {
        return this.haveEntries(this.mergefiles);
    }

    private boolean haveEntries(Path p) {
        return p != null && !p.isEmpty();
    }
}

