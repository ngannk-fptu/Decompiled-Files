/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.optional.clearcase.ClearCase;
import org.apache.tools.ant.types.Commandline;

public class CCMklbtype
extends ClearCase {
    public static final String FLAG_REPLACE = "-replace";
    public static final String FLAG_GLOBAL = "-global";
    public static final String FLAG_ORDINARY = "-ordinary";
    public static final String FLAG_PBRANCH = "-pbranch";
    public static final String FLAG_SHARED = "-shared";
    public static final String FLAG_COMMENT = "-c";
    public static final String FLAG_COMMENTFILE = "-cfile";
    public static final String FLAG_NOCOMMENT = "-nc";
    private String mTypeName = null;
    private String mVOB = null;
    private String mComment = null;
    private String mCfile = null;
    private boolean mReplace = false;
    private boolean mGlobal = false;
    private boolean mOrdinary = true;
    private boolean mPbranch = false;
    private boolean mShared = false;

    @Override
    public void execute() throws BuildException {
        int result;
        Commandline commandLine = new Commandline();
        if (this.getTypeName() == null) {
            throw new BuildException("Required attribute TypeName not specified");
        }
        commandLine.setExecutable(this.getClearToolCommand());
        commandLine.createArgument().setValue("mklbtype");
        this.checkOptions(commandLine);
        if (!this.getFailOnErr()) {
            this.getProject().log("Ignoring any errors that occur for: " + this.getTypeSpecifier(), 3);
        }
        if (Execute.isFailure(result = this.run(commandLine)) && this.getFailOnErr()) {
            throw new BuildException("Failed executing: " + commandLine, this.getLocation());
        }
    }

    private void checkOptions(Commandline cmd) {
        if (this.getReplace()) {
            cmd.createArgument().setValue(FLAG_REPLACE);
        }
        if (this.getOrdinary()) {
            cmd.createArgument().setValue(FLAG_ORDINARY);
        } else if (this.getGlobal()) {
            cmd.createArgument().setValue(FLAG_GLOBAL);
        }
        if (this.getPbranch()) {
            cmd.createArgument().setValue(FLAG_PBRANCH);
        }
        if (this.getShared()) {
            cmd.createArgument().setValue(FLAG_SHARED);
        }
        if (this.getComment() != null) {
            this.getCommentCommand(cmd);
        } else if (this.getCommentFile() != null) {
            this.getCommentFileCommand(cmd);
        } else {
            cmd.createArgument().setValue(FLAG_NOCOMMENT);
        }
        cmd.createArgument().setValue(this.getTypeSpecifier());
    }

    public void setTypeName(String tn) {
        this.mTypeName = tn;
    }

    public String getTypeName() {
        return this.mTypeName;
    }

    public void setVOB(String vob) {
        this.mVOB = vob;
    }

    public String getVOB() {
        return this.mVOB;
    }

    public void setReplace(boolean repl) {
        this.mReplace = repl;
    }

    public boolean getReplace() {
        return this.mReplace;
    }

    public void setGlobal(boolean glob) {
        this.mGlobal = glob;
    }

    public boolean getGlobal() {
        return this.mGlobal;
    }

    public void setOrdinary(boolean ordinary) {
        this.mOrdinary = ordinary;
    }

    public boolean getOrdinary() {
        return this.mOrdinary;
    }

    public void setPbranch(boolean pbranch) {
        this.mPbranch = pbranch;
    }

    public boolean getPbranch() {
        return this.mPbranch;
    }

    public void setShared(boolean shared) {
        this.mShared = shared;
    }

    public boolean getShared() {
        return this.mShared;
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

    private String getTypeSpecifier() {
        String typenm = this.getTypeName();
        if (this.getVOB() != null) {
            typenm = typenm + "@" + this.getVOB();
        }
        return typenm;
    }
}

