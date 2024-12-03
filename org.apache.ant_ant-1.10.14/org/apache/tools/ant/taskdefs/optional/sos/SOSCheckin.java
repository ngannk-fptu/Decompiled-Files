/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.sos;

import org.apache.tools.ant.taskdefs.optional.sos.SOS;
import org.apache.tools.ant.types.Commandline;

public class SOSCheckin
extends SOS {
    public final void setFile(String filename) {
        super.setInternalFilename(filename);
    }

    public void setRecursive(boolean recursive) {
        super.setInternalRecursive(recursive);
    }

    public void setComment(String comment) {
        super.setInternalComment(comment);
    }

    @Override
    protected Commandline buildCmdLine() {
        this.commandLine = new Commandline();
        if (this.getFilename() != null) {
            this.commandLine.createArgument().setValue("-command");
            this.commandLine.createArgument().setValue("CheckInFile");
            this.commandLine.createArgument().setValue("-file");
            this.commandLine.createArgument().setValue(this.getFilename());
        } else {
            this.commandLine.createArgument().setValue("-command");
            this.commandLine.createArgument().setValue("CheckInProject");
            this.commandLine.createArgument().setValue(this.getRecursive());
        }
        this.getRequiredAttributes();
        this.getOptionalAttributes();
        if (this.getComment() != null) {
            this.commandLine.createArgument().setValue("-log");
            this.commandLine.createArgument().setValue(this.getComment());
        }
        return this.commandLine;
    }
}

