/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.optional.clearcase.ClearCase;
import org.apache.tools.ant.types.Commandline;

public class CCUnCheckout
extends ClearCase {
    public static final String FLAG_KEEPCOPY = "-keep";
    public static final String FLAG_RM = "-rm";
    private boolean mKeep = false;

    @Override
    public void execute() throws BuildException {
        int result;
        Commandline commandLine = new Commandline();
        Project aProj = this.getProject();
        if (this.getViewPath() == null) {
            this.setViewPath(aProj.getBaseDir().getPath());
        }
        commandLine.setExecutable(this.getClearToolCommand());
        commandLine.createArgument().setValue("uncheckout");
        this.checkOptions(commandLine);
        if (!this.getFailOnErr()) {
            this.getProject().log("Ignoring any errors that occur for: " + this.getViewPathBasename(), 3);
        }
        if (Execute.isFailure(result = this.run(commandLine)) && this.getFailOnErr()) {
            throw new BuildException("Failed executing: " + commandLine, this.getLocation());
        }
    }

    private void checkOptions(Commandline cmd) {
        if (this.getKeepCopy()) {
            cmd.createArgument().setValue(FLAG_KEEPCOPY);
        } else {
            cmd.createArgument().setValue(FLAG_RM);
        }
        cmd.createArgument().setValue(this.getViewPath());
    }

    public void setKeepCopy(boolean keep) {
        this.mKeep = keep;
    }

    public boolean getKeepCopy() {
        return this.mKeep;
    }
}

