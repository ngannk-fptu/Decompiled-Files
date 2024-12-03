/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.ccm;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.optional.ccm.Continuus;
import org.apache.tools.ant.types.Commandline;

public class CCMReconfigure
extends Continuus {
    public static final String FLAG_RECURSE = "/recurse";
    public static final String FLAG_VERBOSE = "/verbose";
    public static final String FLAG_PROJECT = "/project";
    private String ccmProject = null;
    private boolean recurse = false;
    private boolean verbose = false;

    public CCMReconfigure() {
        this.setCcmAction("reconfigure");
    }

    @Override
    public void execute() throws BuildException {
        Commandline commandLine = new Commandline();
        commandLine.setExecutable(this.getCcmCommand());
        commandLine.createArgument().setValue(this.getCcmAction());
        this.checkOptions(commandLine);
        int result = this.run(commandLine);
        if (Execute.isFailure(result)) {
            throw new BuildException("Failed executing: " + commandLine, this.getLocation());
        }
    }

    private void checkOptions(Commandline cmd) {
        if (this.isRecurse()) {
            cmd.createArgument().setValue(FLAG_RECURSE);
        }
        if (this.isVerbose()) {
            cmd.createArgument().setValue(FLAG_VERBOSE);
        }
        if (this.getCcmProject() != null) {
            cmd.createArgument().setValue(FLAG_PROJECT);
            cmd.createArgument().setValue(this.getCcmProject());
        }
    }

    public String getCcmProject() {
        return this.ccmProject;
    }

    public void setCcmProject(String v) {
        this.ccmProject = v;
    }

    public boolean isRecurse() {
        return this.recurse;
    }

    public void setRecurse(boolean v) {
        this.recurse = v;
    }

    public boolean isVerbose() {
        return this.verbose;
    }

    public void setVerbose(boolean v) {
        this.verbose = v;
    }
}

