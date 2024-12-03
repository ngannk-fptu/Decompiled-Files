/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.optional.clearcase.ClearCase;
import org.apache.tools.ant.types.Commandline;

public class CCRmtype
extends ClearCase {
    public static final String FLAG_IGNORE = "-ignore";
    public static final String FLAG_RMALL = "-rmall";
    public static final String FLAG_FORCE = "-force";
    public static final String FLAG_COMMENT = "-c";
    public static final String FLAG_COMMENTFILE = "-cfile";
    public static final String FLAG_NOCOMMENT = "-nc";
    private String mTypeKind = null;
    private String mTypeName = null;
    private String mVOB = null;
    private String mComment = null;
    private String mCfile = null;
    private boolean mRmall = false;
    private boolean mIgnore = false;

    @Override
    public void execute() throws BuildException {
        int result;
        Commandline commandLine = new Commandline();
        if (this.getTypeKind() == null) {
            throw new BuildException("Required attribute TypeKind not specified");
        }
        if (this.getTypeName() == null) {
            throw new BuildException("Required attribute TypeName not specified");
        }
        commandLine.setExecutable(this.getClearToolCommand());
        commandLine.createArgument().setValue("rmtype");
        this.checkOptions(commandLine);
        if (!this.getFailOnErr()) {
            this.getProject().log("Ignoring any errors that occur for: " + this.getTypeSpecifier(), 3);
        }
        if (Execute.isFailure(result = this.run(commandLine)) && this.getFailOnErr()) {
            throw new BuildException("Failed executing: " + commandLine, this.getLocation());
        }
    }

    private void checkOptions(Commandline cmd) {
        if (this.getIgnore()) {
            cmd.createArgument().setValue(FLAG_IGNORE);
        }
        if (this.getRmAll()) {
            cmd.createArgument().setValue(FLAG_RMALL);
            cmd.createArgument().setValue(FLAG_FORCE);
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

    public void setIgnore(boolean ignore) {
        this.mIgnore = ignore;
    }

    public boolean getIgnore() {
        return this.mIgnore;
    }

    public void setRmAll(boolean rmall) {
        this.mRmall = rmall;
    }

    public boolean getRmAll() {
        return this.mRmall;
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

    public void setTypeKind(String tk) {
        this.mTypeKind = tk;
    }

    public String getTypeKind() {
        return this.mTypeKind;
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

    private String getTypeSpecifier() {
        String tkind = this.getTypeKind();
        String tname = this.getTypeName();
        String typeSpec = tkind + ":" + tname;
        if (this.getVOB() != null) {
            typeSpec = typeSpec + "@" + this.getVOB();
        }
        return typeSpec;
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

