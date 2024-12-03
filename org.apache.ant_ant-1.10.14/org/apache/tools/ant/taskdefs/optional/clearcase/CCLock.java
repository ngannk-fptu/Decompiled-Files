/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.clearcase;

import java.util.Optional;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.optional.clearcase.ClearCase;
import org.apache.tools.ant.types.Commandline;

public class CCLock
extends ClearCase {
    public static final String FLAG_REPLACE = "-replace";
    public static final String FLAG_NUSERS = "-nusers";
    public static final String FLAG_OBSOLETE = "-obsolete";
    public static final String FLAG_COMMENT = "-comment";
    public static final String FLAG_PNAME = "-pname";
    private boolean mReplace = false;
    private boolean mObsolete = false;
    private String mComment = null;
    private String mNusers = null;
    private String mPname = null;
    private String mObjselect = null;

    @Override
    public void execute() throws BuildException {
        int result;
        Commandline commandLine = new Commandline();
        Project aProj = this.getProject();
        if (this.getViewPath() == null) {
            this.setViewPath(aProj.getBaseDir().getPath());
        }
        commandLine.setExecutable(this.getClearToolCommand());
        commandLine.createArgument().setValue("lock");
        this.checkOptions(commandLine);
        if (!this.getFailOnErr()) {
            this.getProject().log("Ignoring any errors that occur for: " + this.getOpType(), 3);
        }
        if (Execute.isFailure(result = this.run(commandLine)) && this.getFailOnErr()) {
            throw new BuildException("Failed executing: " + commandLine, this.getLocation());
        }
    }

    private void checkOptions(Commandline cmd) {
        if (this.getReplace()) {
            cmd.createArgument().setValue(FLAG_REPLACE);
        }
        if (this.getObsolete()) {
            cmd.createArgument().setValue(FLAG_OBSOLETE);
        } else {
            this.getNusersCommand(cmd);
        }
        this.getCommentCommand(cmd);
        if (this.getObjselect() == null && this.getPname() == null) {
            throw new BuildException("Should select either an element (pname) or an object (objselect)");
        }
        this.getPnameCommand(cmd);
        if (this.getObjselect() != null) {
            cmd.createArgument().setValue(this.getObjselect());
        }
    }

    public void setReplace(boolean replace) {
        this.mReplace = replace;
    }

    public boolean getReplace() {
        return this.mReplace;
    }

    public void setObsolete(boolean obsolete) {
        this.mObsolete = obsolete;
    }

    public boolean getObsolete() {
        return this.mObsolete;
    }

    public void setNusers(String nusers) {
        this.mNusers = nusers;
    }

    public String getNusers() {
        return this.mNusers;
    }

    public void setComment(String comment) {
        this.mComment = comment;
    }

    public String getComment() {
        return this.mComment;
    }

    public void setPname(String pname) {
        this.mPname = pname;
    }

    public String getPname() {
        return this.mPname;
    }

    public void setObjSel(String objsel) {
        this.mObjselect = objsel;
    }

    public void setObjselect(String objselect) {
        this.mObjselect = objselect;
    }

    public String getObjselect() {
        return this.mObjselect;
    }

    private void getNusersCommand(Commandline cmd) {
        if (this.getNusers() == null) {
            return;
        }
        cmd.createArgument().setValue(FLAG_NUSERS);
        cmd.createArgument().setValue(this.getNusers());
    }

    private void getCommentCommand(Commandline cmd) {
        if (this.getComment() == null) {
            return;
        }
        cmd.createArgument().setValue(FLAG_COMMENT);
        cmd.createArgument().setValue(this.getComment());
    }

    private void getPnameCommand(Commandline cmd) {
        if (this.getPname() == null) {
            return;
        }
        cmd.createArgument().setValue(FLAG_PNAME);
        cmd.createArgument().setValue(this.getPname());
    }

    private String getOpType() {
        return Optional.ofNullable(this.getPname()).orElseGet(this::getObjselect);
    }
}

