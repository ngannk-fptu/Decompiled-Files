/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.taskdefs.optional.clearcase.ClearCase;
import org.apache.tools.ant.types.Commandline;

public class CCMkattr
extends ClearCase {
    public static final String FLAG_REPLACE = "-replace";
    public static final String FLAG_RECURSE = "-recurse";
    public static final String FLAG_VERSION = "-version";
    public static final String FLAG_COMMENT = "-c";
    public static final String FLAG_COMMENTFILE = "-cfile";
    public static final String FLAG_NOCOMMENT = "-nc";
    private boolean mReplace = false;
    private boolean mRecurse = false;
    private String mVersion = null;
    private String mTypeName = null;
    private String mTypeValue = null;
    private String mComment = null;
    private String mCfile = null;

    @Override
    public void execute() throws BuildException {
        int result;
        Commandline commandLine = new Commandline();
        Project aProj = this.getProject();
        if (this.getTypeName() == null) {
            throw new BuildException("Required attribute TypeName not specified");
        }
        if (this.getTypeValue() == null) {
            throw new BuildException("Required attribute TypeValue not specified");
        }
        if (this.getViewPath() == null) {
            this.setViewPath(aProj.getBaseDir().getPath());
        }
        commandLine.setExecutable(this.getClearToolCommand());
        commandLine.createArgument().setValue("mkattr");
        this.checkOptions(commandLine);
        if (!this.getFailOnErr()) {
            this.getProject().log("Ignoring any errors that occur for: " + this.getViewPathBasename(), 3);
        }
        if (Execute.isFailure(result = this.run(commandLine)) && this.getFailOnErr()) {
            throw new BuildException("Failed executing: " + commandLine, this.getLocation());
        }
    }

    private void checkOptions(Commandline cmd) {
        if (this.getReplace()) {
            cmd.createArgument().setValue(FLAG_REPLACE);
        }
        if (this.getRecurse()) {
            cmd.createArgument().setValue(FLAG_RECURSE);
        }
        if (this.getVersion() != null) {
            this.getVersionCommand(cmd);
        }
        if (this.getComment() != null) {
            this.getCommentCommand(cmd);
        } else if (this.getCommentFile() != null) {
            this.getCommentFileCommand(cmd);
        } else {
            cmd.createArgument().setValue(FLAG_NOCOMMENT);
        }
        if (this.getTypeName() != null) {
            this.getTypeCommand(cmd);
        }
        if (this.getTypeValue() != null) {
            this.getTypeValueCommand(cmd);
        }
        cmd.createArgument().setValue(this.getViewPath());
    }

    public void setReplace(boolean replace) {
        this.mReplace = replace;
    }

    public boolean getReplace() {
        return this.mReplace;
    }

    public void setRecurse(boolean recurse) {
        this.mRecurse = recurse;
    }

    public boolean getRecurse() {
        return this.mRecurse;
    }

    public void setVersion(String version) {
        this.mVersion = version;
    }

    public String getVersion() {
        return this.mVersion;
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

    public void setTypeName(String tn) {
        this.mTypeName = tn;
    }

    public String getTypeName() {
        return this.mTypeName;
    }

    public void setTypeValue(String tv) {
        this.mTypeValue = tv;
    }

    public String getTypeValue() {
        return this.mTypeValue;
    }

    private void getVersionCommand(Commandline cmd) {
        if (this.getVersion() != null) {
            cmd.createArgument().setValue(FLAG_VERSION);
            cmd.createArgument().setValue(this.getVersion());
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

    private void getTypeCommand(Commandline cmd) {
        String typenm = this.getTypeName();
        if (typenm != null) {
            cmd.createArgument().setValue(typenm);
        }
    }

    private void getTypeValueCommand(Commandline cmd) {
        String typevl = this.getTypeValue();
        if (typevl != null) {
            typevl = Os.isFamily("windows") ? "\\\"" + typevl + "\\\"" : "\"" + typevl + "\"";
            cmd.createArgument().setValue(typevl);
        }
    }
}

