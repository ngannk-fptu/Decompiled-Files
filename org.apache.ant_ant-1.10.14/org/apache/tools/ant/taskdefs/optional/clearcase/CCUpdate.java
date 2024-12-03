/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.optional.clearcase.ClearCase;
import org.apache.tools.ant.types.Commandline;

public class CCUpdate
extends ClearCase {
    public static final String FLAG_GRAPHICAL = "-graphical";
    public static final String FLAG_LOG = "-log";
    public static final String FLAG_OVERWRITE = "-overwrite";
    public static final String FLAG_NOVERWRITE = "-noverwrite";
    public static final String FLAG_RENAME = "-rename";
    public static final String FLAG_CURRENTTIME = "-ctime";
    public static final String FLAG_PRESERVETIME = "-ptime";
    private boolean mGraphical = false;
    private boolean mOverwrite = false;
    private boolean mRename = false;
    private boolean mCtime = false;
    private boolean mPtime = false;
    private String mLog = null;

    @Override
    public void execute() throws BuildException {
        int result;
        Commandline commandLine = new Commandline();
        Project aProj = this.getProject();
        if (this.getViewPath() == null) {
            this.setViewPath(aProj.getBaseDir().getPath());
        }
        commandLine.setExecutable(this.getClearToolCommand());
        commandLine.createArgument().setValue("update");
        this.checkOptions(commandLine);
        this.getProject().log(commandLine.toString(), 4);
        if (!this.getFailOnErr()) {
            this.getProject().log("Ignoring any errors that occur for: " + this.getViewPathBasename(), 3);
        }
        if (Execute.isFailure(result = this.run(commandLine)) && this.getFailOnErr()) {
            throw new BuildException("Failed executing: " + commandLine, this.getLocation());
        }
    }

    private void checkOptions(Commandline cmd) {
        if (this.getGraphical()) {
            cmd.createArgument().setValue(FLAG_GRAPHICAL);
        } else {
            if (this.getOverwrite()) {
                cmd.createArgument().setValue(FLAG_OVERWRITE);
            } else if (this.getRename()) {
                cmd.createArgument().setValue(FLAG_RENAME);
            } else {
                cmd.createArgument().setValue(FLAG_NOVERWRITE);
            }
            if (this.getCurrentTime()) {
                cmd.createArgument().setValue(FLAG_CURRENTTIME);
            } else if (this.getPreserveTime()) {
                cmd.createArgument().setValue(FLAG_PRESERVETIME);
            }
            this.getLogCommand(cmd);
        }
        cmd.createArgument().setValue(this.getViewPath());
    }

    public void setGraphical(boolean graphical) {
        this.mGraphical = graphical;
    }

    public boolean getGraphical() {
        return this.mGraphical;
    }

    public void setOverwrite(boolean ow) {
        this.mOverwrite = ow;
    }

    public boolean getOverwrite() {
        return this.mOverwrite;
    }

    public void setRename(boolean ren) {
        this.mRename = ren;
    }

    public boolean getRename() {
        return this.mRename;
    }

    public void setCurrentTime(boolean ct) {
        this.mCtime = ct;
    }

    public boolean getCurrentTime() {
        return this.mCtime;
    }

    public void setPreserveTime(boolean pt) {
        this.mPtime = pt;
    }

    public boolean getPreserveTime() {
        return this.mPtime;
    }

    public void setLog(String log) {
        this.mLog = log;
    }

    public String getLog() {
        return this.mLog;
    }

    private void getLogCommand(Commandline cmd) {
        if (this.getLog() == null) {
            return;
        }
        cmd.createArgument().setValue(FLAG_LOG);
        cmd.createArgument().setValue(this.getLog());
    }
}

