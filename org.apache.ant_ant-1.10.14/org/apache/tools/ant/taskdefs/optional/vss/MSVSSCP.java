/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.vss;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.vss.MSVSS;
import org.apache.tools.ant.types.Commandline;

public class MSVSSCP
extends MSVSS {
    @Override
    protected Commandline buildCmdLine() {
        Commandline commandLine = new Commandline();
        if (this.getVsspath() == null) {
            String msg = "vsspath attribute must be set!";
            throw new BuildException(msg, this.getLocation());
        }
        commandLine.setExecutable(this.getSSCommand());
        commandLine.createArgument().setValue("CP");
        commandLine.createArgument().setValue(this.getVsspath());
        commandLine.createArgument().setValue(this.getAutoresponse());
        commandLine.createArgument().setValue(this.getLogin());
        return commandLine;
    }

    public void setAutoresponse(String response) {
        super.setInternalAutoResponse(response);
    }
}

