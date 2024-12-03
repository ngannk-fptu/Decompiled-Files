/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.optional.clearcase.ClearCase;
import org.apache.tools.ant.types.Commandline;

public class CCCheckout
extends ClearCase {
    public static final String FLAG_RESERVED = "-reserved";
    public static final String FLAG_UNRESERVED = "-unreserved";
    public static final String FLAG_OUT = "-out";
    public static final String FLAG_NODATA = "-ndata";
    public static final String FLAG_BRANCH = "-branch";
    public static final String FLAG_VERSION = "-version";
    public static final String FLAG_NOWARN = "-nwarn";
    public static final String FLAG_COMMENT = "-c";
    public static final String FLAG_COMMENTFILE = "-cfile";
    public static final String FLAG_NOCOMMENT = "-nc";
    private boolean mReserved = true;
    private String mOut = null;
    private boolean mNdata = false;
    private String mBranch = null;
    private boolean mVersion = false;
    private boolean mNwarn = false;
    private String mComment = null;
    private String mCfile = null;
    private boolean mNotco = true;

    @Override
    public void execute() throws BuildException {
        int result;
        Commandline commandLine = new Commandline();
        Project aProj = this.getProject();
        if (this.getViewPath() == null) {
            this.setViewPath(aProj.getBaseDir().getPath());
        }
        commandLine.setExecutable(this.getClearToolCommand());
        commandLine.createArgument().setValue("checkout");
        this.checkOptions(commandLine);
        if (!this.getNotco() && this.lsCheckout()) {
            this.getProject().log("Already checked out in this view: " + this.getViewPathBasename(), 3);
            return;
        }
        if (!this.getFailOnErr()) {
            this.getProject().log("Ignoring any errors that occur for: " + this.getViewPathBasename(), 3);
        }
        if (Execute.isFailure(result = this.run(commandLine)) && this.getFailOnErr()) {
            throw new BuildException("Failed executing: " + commandLine, this.getLocation());
        }
    }

    private boolean lsCheckout() {
        Commandline cmdl = new Commandline();
        cmdl.setExecutable(this.getClearToolCommand());
        cmdl.createArgument().setValue("lsco");
        cmdl.createArgument().setValue("-cview");
        cmdl.createArgument().setValue("-short");
        cmdl.createArgument().setValue("-d");
        cmdl.createArgument().setValue(this.getViewPath());
        String result = this.runS(cmdl, this.getFailOnErr());
        return result != null && !result.isEmpty();
    }

    private void checkOptions(Commandline cmd) {
        if (this.getReserved()) {
            cmd.createArgument().setValue(FLAG_RESERVED);
        } else {
            cmd.createArgument().setValue(FLAG_UNRESERVED);
        }
        if (this.getOut() != null) {
            this.getOutCommand(cmd);
        } else if (this.getNoData()) {
            cmd.createArgument().setValue(FLAG_NODATA);
        }
        if (this.getBranch() != null) {
            this.getBranchCommand(cmd);
        } else if (this.getVersion()) {
            cmd.createArgument().setValue(FLAG_VERSION);
        }
        if (this.getNoWarn()) {
            cmd.createArgument().setValue(FLAG_NOWARN);
        }
        if (this.getComment() != null) {
            this.getCommentCommand(cmd);
        } else if (this.getCommentFile() != null) {
            this.getCommentFileCommand(cmd);
        } else {
            cmd.createArgument().setValue(FLAG_NOCOMMENT);
        }
        cmd.createArgument().setValue(this.getViewPath());
    }

    public void setReserved(boolean reserved) {
        this.mReserved = reserved;
    }

    public boolean getReserved() {
        return this.mReserved;
    }

    public void setNotco(boolean notco) {
        this.mNotco = notco;
    }

    public boolean getNotco() {
        return this.mNotco;
    }

    public void setOut(String outf) {
        this.mOut = outf;
    }

    public String getOut() {
        return this.mOut;
    }

    public void setNoData(boolean ndata) {
        this.mNdata = ndata;
    }

    public boolean getNoData() {
        return this.mNdata;
    }

    public void setBranch(String branch) {
        this.mBranch = branch;
    }

    public String getBranch() {
        return this.mBranch;
    }

    public void setVersion(boolean version) {
        this.mVersion = version;
    }

    public boolean getVersion() {
        return this.mVersion;
    }

    public void setNoWarn(boolean nwarn) {
        this.mNwarn = nwarn;
    }

    public boolean getNoWarn() {
        return this.mNwarn;
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

    private void getOutCommand(Commandline cmd) {
        if (this.getOut() != null) {
            cmd.createArgument().setValue(FLAG_OUT);
            cmd.createArgument().setValue(this.getOut());
        }
    }

    private void getBranchCommand(Commandline cmd) {
        if (this.getBranch() != null) {
            cmd.createArgument().setValue(FLAG_BRANCH);
            cmd.createArgument().setValue(this.getBranch());
        }
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

