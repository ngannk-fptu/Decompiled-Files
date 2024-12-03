/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.sos;

import org.apache.tools.ant.taskdefs.optional.sos.SOS;
import org.apache.tools.ant.types.Commandline;

public class SOSCheckout
extends SOS {
    public final void setFile(String filename) {
        super.setInternalFilename(filename);
    }

    public void setRecursive(boolean recursive) {
        super.setInternalRecursive(recursive);
    }

    @Override
    protected Commandline buildCmdLine() {
        this.commandLine = new Commandline();
        if (this.getFilename() != null) {
            this.commandLine.createArgument().setValue("-command");
            this.commandLine.createArgument().setValue("CheckOutFile");
            this.commandLine.createArgument().setValue("-file");
            this.commandLine.createArgument().setValue(this.getFilename());
        } else {
            this.commandLine.createArgument().setValue("-command");
            this.commandLine.createArgument().setValue("CheckOutProject");
            this.commandLine.createArgument().setValue(this.getRecursive());
        }
        this.getRequiredAttributes();
        this.getOptionalAttributes();
        return this.commandLine;
    }
}

