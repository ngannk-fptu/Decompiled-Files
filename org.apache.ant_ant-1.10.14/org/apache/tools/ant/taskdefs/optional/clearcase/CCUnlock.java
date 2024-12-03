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

public class CCUnlock
extends ClearCase {
    public static final String FLAG_COMMENT = "-comment";
    public static final String FLAG_PNAME = "-pname";
    private String mComment = null;
    private String mPname = null;

    @Override
    public void execute() throws BuildException {
        int result;
        Commandline commandLine = new Commandline();
        Project aProj = this.getProject();
        if (this.getViewPath() == null) {
            this.setViewPath(aProj.getBaseDir().getPath());
        }
        commandLine.setExecutable(this.getClearToolCommand());
        commandLine.createArgument().setValue("unlock");
        this.checkOptions(commandLine);
        if (!this.getFailOnErr()) {
            this.getProject().log("Ignoring any errors that occur for: " + this.getOpType(), 3);
        }
        if (Execute.isFailure(result = this.run(commandLine)) && this.getFailOnErr()) {
            throw new BuildException("Failed executing: " + commandLine, this.getLocation());
        }
    }

    private void checkOptions(Commandline cmd) {
        this.getCommentCommand(cmd);
        if (this.getObjSelect() == null && this.getPname() == null) {
            throw new BuildException("Should select either an element (pname) or an object (objselect)");
        }
        this.getPnameCommand(cmd);
        if (this.getObjSelect() != null) {
            cmd.createArgument().setValue(this.getObjSelect());
        }
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

    public void setObjselect(String objselect) {
        this.setObjSelect(objselect);
    }

    public void setObjSel(String objsel) {
        this.setObjSelect(objsel);
    }

    public String getObjselect() {
        return this.getObjSelect();
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
        return Optional.ofNullable(this.getPname()).orElseGet(this::getObjSelect);
    }
}

