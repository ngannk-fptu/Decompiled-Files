/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.optional.clearcase.ClearCase;
import org.apache.tools.ant.types.Commandline;

public class CCMkbl
extends ClearCase {
    public static final String FLAG_COMMENT = "-c";
    public static final String FLAG_COMMENTFILE = "-cfile";
    public static final String FLAG_NOCOMMENT = "-nc";
    public static final String FLAG_IDENTICAL = "-identical";
    public static final String FLAG_INCREMENTAL = "-incremental";
    public static final String FLAG_FULL = "-full";
    public static final String FLAG_NLABEL = "-nlabel";
    private String mComment = null;
    private String mCfile = null;
    private String mBaselineRootName = null;
    private boolean mNwarn = false;
    private boolean mIdentical = true;
    private boolean mFull = false;
    private boolean mNlabel = false;

    @Override
    public void execute() throws BuildException {
        int result;
        Commandline commandLine = new Commandline();
        Project aProj = this.getProject();
        if (this.getViewPath() == null) {
            this.setViewPath(aProj.getBaseDir().getPath());
        }
        commandLine.setExecutable(this.getClearToolCommand());
        commandLine.createArgument().setValue("mkbl");
        this.checkOptions(commandLine);
        if (!this.getFailOnErr()) {
            this.getProject().log("Ignoring any errors that occur for: " + this.getBaselineRootName(), 3);
        }
        if (Execute.isFailure(result = this.run(commandLine)) && this.getFailOnErr()) {
            throw new BuildException("Failed executing: " + commandLine, this.getLocation());
        }
    }

    private void checkOptions(Commandline cmd) {
        if (this.getComment() != null) {
            this.getCommentCommand(cmd);
        } else if (this.getCommentFile() != null) {
            this.getCommentFileCommand(cmd);
        } else {
            cmd.createArgument().setValue(FLAG_NOCOMMENT);
        }
        if (this.getIdentical()) {
            cmd.createArgument().setValue(FLAG_IDENTICAL);
        }
        if (this.getFull()) {
            cmd.createArgument().setValue(FLAG_FULL);
        } else {
            cmd.createArgument().setValue(FLAG_INCREMENTAL);
        }
        if (this.getNlabel()) {
            cmd.createArgument().setValue(FLAG_NLABEL);
        }
        cmd.createArgument().setValue(this.getBaselineRootName());
    }

    public void setComment(String comment) {
        this.mComment = comment;
    }

    public String getComment() {
        return this.mComment;
    }

    public void setCommentFile(String cfile) {
        this.mCfile = cfile;
    }

    public String getCommentFile() {
        return this.mCfile;
    }

    public void setBaselineRootName(String baselineRootName) {
        this.mBaselineRootName = baselineRootName;
    }

    public String getBaselineRootName() {
        return this.mBaselineRootName;
    }

    public void setNoWarn(boolean nwarn) {
        this.mNwarn = nwarn;
    }

    public boolean getNoWarn() {
        return this.mNwarn;
    }

    public void setIdentical(boolean identical) {
        this.mIdentical = identical;
    }

    public boolean getIdentical() {
        return this.mIdentical;
    }

    public void setFull(boolean full) {
        this.mFull = full;
    }

    public boolean getFull() {
        return this.mFull;
    }

    public void setNlabel(boolean nlabel) {
        this.mNlabel = nlabel;
    }

    public boolean getNlabel() {
        return this.mNlabel;
    }

    private void getCommentCommand(Commandline cmd) {
        if (this.getComment() != null) {
            cmd.createArgument().setValue(FLAG_COMMENT);
            cmd.createArgument().setValue(this.getComment());
        }
    }

    private void getCommentFileCommand(Commandline cmd) {
        if (this.getCommentFile() != null) {
            cmd.createArgument().setValue(FLAG_COMMENTFILE);
            cmd.createArgument().setValue(this.getCommentFile());
        }
    }
}

