/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.types.Commandline;

public class Patch
extends Task {
    private File originalFile;
    private File directory;
    private boolean havePatchfile = false;
    private Commandline cmd = new Commandline();
    private boolean failOnError = false;
    private static final String PATCH = "patch";

    public void setOriginalfile(File file) {
        this.originalFile = file;
    }

    public void setDestfile(File file) {
        if (file != null) {
            this.cmd.createArgument().setValue("-o");
            this.cmd.createArgument().setFile(file);
        }
    }

    public void setPatchfile(File file) {
        if (!file.exists()) {
            throw new BuildException("patchfile " + file + " doesn't exist", this.getLocation());
        }
        this.cmd.createArgument().setValue("-i");
        this.cmd.createArgument().setFile(file);
        this.havePatchfile = true;
    }

    public void setBackups(boolean backups) {
        if (backups) {
            this.cmd.createArgument().setValue("-b");
        }
    }

    public void setIgnorewhitespace(boolean ignore) {
        if (ignore) {
            this.cmd.createArgument().setValue("-l");
        }
    }

    public void setStrip(int num) throws BuildException {
        if (num < 0) {
            throw new BuildException("strip has to be >= 0", this.getLocation());
        }
        this.cmd.createArgument().setValue("-p" + num);
    }

    public void setQuiet(boolean q) {
        if (q) {
            this.cmd.createArgument().setValue("-s");
        }
    }

    public void setReverse(boolean r) {
        if (r) {
            this.cmd.createArgument().setValue("-R");
        }
    }

    public void setDir(File directory) {
        this.directory = directory;
    }

    public void setFailOnError(boolean value) {
        this.failOnError = value;
    }

    @Override
    public void execute() throws BuildException {
        if (!this.havePatchfile) {
            throw new BuildException("patchfile argument is required", this.getLocation());
        }
        Commandline toExecute = (Commandline)this.cmd.clone();
        toExecute.setExecutable(PATCH);
        if (this.originalFile != null) {
            toExecute.createArgument().setFile(this.originalFile);
        }
        Execute exe = new Execute(new LogStreamHandler(this, 2, 1), null);
        exe.setCommandline(toExecute.getCommandline());
        if (this.directory == null) {
            exe.setWorkingDirectory(this.getProject().getBaseDir());
        } else {
            if (!this.directory.isDirectory()) {
                throw new BuildException(this.directory + " is not a directory.", this.getLocation());
            }
            exe.setWorkingDirectory(this.directory);
        }
        this.log(toExecute.describeCommand(), 3);
        try {
            int returncode = exe.execute();
            if (Execute.isFailure(returncode)) {
                String msg = "'patch' failed with exit code " + returncode;
                if (this.failOnError) {
                    throw new BuildException(msg);
                }
                this.log(msg, 0);
            }
        }
        catch (IOException e) {
            throw new BuildException(e, this.getLocation());
        }
    }
}

