/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.optional.clearcase.ClearCase;
import org.apache.tools.ant.types.Commandline;

public class CCMkelem
extends ClearCase {
    public static final String FLAG_COMMENT = "-c";
    public static final String FLAG_COMMENTFILE = "-cfile";
    public static final String FLAG_NOCOMMENT = "-nc";
    public static final String FLAG_NOWARN = "-nwarn";
    public static final String FLAG_PRESERVETIME = "-ptime";
    public static final String FLAG_NOCHECKOUT = "-nco";
    public static final String FLAG_CHECKIN = "-ci";
    public static final String FLAG_MASTER = "-master";
    public static final String FLAG_ELTYPE = "-eltype";
    private String mComment = null;
    private String mCfile = null;
    private boolean mNwarn = false;
    private boolean mPtime = false;
    private boolean mNoco = false;
    private boolean mCheckin = false;
    private boolean mMaster = false;
    private String mEltype = null;

    @Override
    public void execute() throws BuildException {
        int result;
        Commandline commandLine = new Commandline();
        Project aProj = this.getProject();
        if (this.getViewPath() == null) {
            this.setViewPath(aProj.getBaseDir().getPath());
        }
        commandLine.setExecutable(this.getClearToolCommand());
        commandLine.createArgument().setValue("mkelem");
        this.checkOptions(commandLine);
        if (!this.getFailOnErr()) {
            this.getProject().log("Ignoring any errors that occur for: " + this.getViewPathBasename(), 3);
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
        if (this.getNoWarn()) {
            cmd.createArgument().setValue(FLAG_NOWARN);
        }
        if (this.getNoCheckout() && this.getCheckin()) {
            throw new BuildException("Should choose either [nocheckout | checkin]");
        }
        if (this.getNoCheckout()) {
            cmd.createArgument().setValue(FLAG_NOCHECKOUT);
        }
        if (this.getCheckin()) {
            cmd.createArgument().setValue(FLAG_CHECKIN);
            if (this.getPreserveTime()) {
                cmd.createArgument().setValue(FLAG_PRESERVETIME);
            }
        }
        if (this.getMaster()) {
            cmd.createArgument().setValue(FLAG_MASTER);
        }
        if (this.getEltype() != null) {
            this.getEltypeCommand(cmd);
        }
        cmd.createArgument().setValue(this.getViewPath());
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

    public void setNoWarn(boolean nwarn) {
        this.mNwarn = nwarn;
    }

    public boolean getNoWarn() {
        return this.mNwarn;
    }

    public void setPreserveTime(boolean ptime) {
        this.mPtime = ptime;
    }

    public boolean getPreserveTime() {
        return this.mPtime;
    }

    public void setNoCheckout(boolean co) {
        this.mNoco = co;
    }

    public boolean getNoCheckout() {
        return this.mNoco;
    }

    public void setCheckin(boolean ci) {
        this.mCheckin = ci;
    }

    public boolean getCheckin() {
        return this.mCheckin;
    }

    public void setMaster(boolean master) {
        this.mMaster = master;
    }

    public boolean getMaster() {
        return this.mMaster;
    }

    public void setEltype(String eltype) {
        this.mEltype = eltype;
    }

    public String getEltype() {
        return this.mEltype;
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

    private void getEltypeCommand(Commandline cmd) {
        if (this.getEltype() != null) {
            cmd.createArgument().setValue(FLAG_ELTYPE);
            cmd.createArgument().setValue(this.getEltype());
        }
    }
}

