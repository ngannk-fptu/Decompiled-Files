/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.sos;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.sos.SOS;
import org.apache.tools.ant.types.Commandline;

public class SOSLabel
extends SOS {
    public void setVersion(String version) {
        super.setInternalVersion(version);
    }

    public void setLabel(String label) {
        super.setInternalLabel(label);
    }

    public void setComment(String comment) {
        super.setInternalComment(comment);
    }

    @Override
    protected Commandline buildCmdLine() {
        this.commandLine = new Commandline();
        this.commandLine.createArgument().setValue("-command");
        this.commandLine.createArgument().setValue("AddLabel");
        this.getRequiredAttributes();
        if (this.getLabel() == null) {
            throw new BuildException("label attribute must be set!", this.getLocation());
        }
        this.commandLine.createArgument().setValue("-label");
        this.commandLine.createArgument().setValue(this.getLabel());
        this.commandLine.createArgument().setValue(this.getVerbose());
        if (this.getComment() != null) {
            this.commandLine.createArgument().setValue("-log");
            this.commandLine.createArgument().setValue(this.getComment());
        }
        return this.commandLine;
    }
}

