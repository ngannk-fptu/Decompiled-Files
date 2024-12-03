/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.vss;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.vss.MSVSS;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;

public class MSVSSCHECKIN
extends MSVSS {
    @Override
    protected Commandline buildCmdLine() {
        Commandline commandLine = new Commandline();
        if (this.getVsspath() == null) {
            String msg = "vsspath attribute must be set!";
            throw new BuildException(msg, this.getLocation());
        }
        commandLine.setExecutable(this.getSSCommand());
        commandLine.createArgument().setValue("Checkin");
        commandLine.createArgument().setValue(this.getVsspath());
        commandLine.createArgument().setValue(this.getLocalpath());
        commandLine.createArgument().setValue(this.getAutoresponse());
        commandLine.createArgument().setValue(this.getRecursive());
        commandLine.createArgument().setValue(this.getWritable());
        commandLine.createArgument().setValue(this.getLogin());
        commandLine.createArgument().setValue(this.getComment());
        return commandLine;
    }

    public void setLocalpath(Path localPath) {
        super.setInternalLocalPath(localPath.toString());
    }

    public void setRecursive(boolean recursive) {
        super.setInternalRecursive(recursive);
    }

    public final void setWritable(boolean writable) {
        super.setInternalWritable(writable);
    }

    public void setAutoresponse(String response) {
        super.setInternalAutoResponse(response);
    }

    public void setComment(String comment) {
        super.setInternalComment(comment);
    }
}

