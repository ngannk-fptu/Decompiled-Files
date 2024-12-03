/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.vss;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.vss.MSVSS;
import org.apache.tools.ant.types.Commandline;

public class MSVSSLABEL
extends MSVSS {
    @Override
    Commandline buildCmdLine() {
        Commandline commandLine = new Commandline();
        if (this.getVsspath() == null) {
            throw new BuildException("vsspath attribute must be set!", this.getLocation());
        }
        String label = this.getLabel();
        if (label.isEmpty()) {
            String msg = "label attribute must be set!";
            throw new BuildException(msg, this.getLocation());
        }
        commandLine.setExecutable(this.getSSCommand());
        commandLine.createArgument().setValue("Label");
        commandLine.createArgument().setValue(this.getVsspath());
        commandLine.createArgument().setValue(this.getComment());
        commandLine.createArgument().setValue(this.getAutoresponse());
        commandLine.createArgument().setValue(label);
        commandLine.createArgument().setValue(this.getVersion());
        commandLine.createArgument().setValue(this.getLogin());
        return commandLine;
    }

    public void setLabel(String label) {
        super.setInternalLabel(label);
    }

    public void setVersion(String version) {
        super.setInternalVersion(version);
    }

    public void setComment(String comment) {
        super.setInternalComment(comment);
    }

    public void setAutoresponse(String response) {
        super.setInternalAutoResponse(response);
    }
}

