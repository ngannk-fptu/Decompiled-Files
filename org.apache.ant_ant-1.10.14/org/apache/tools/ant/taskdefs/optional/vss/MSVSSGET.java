/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.vss;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.vss.MSVSS;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;

public class MSVSSGET
extends MSVSS {
    @Override
    Commandline buildCmdLine() {
        Commandline commandLine = new Commandline();
        commandLine.setExecutable(this.getSSCommand());
        commandLine.createArgument().setValue("Get");
        if (this.getVsspath() == null) {
            throw new BuildException("vsspath attribute must be set!", this.getLocation());
        }
        commandLine.createArgument().setValue(this.getVsspath());
        commandLine.createArgument().setValue(this.getLocalpath());
        commandLine.createArgument().setValue(this.getAutoresponse());
        commandLine.createArgument().setValue(this.getQuiet());
        commandLine.createArgument().setValue(this.getRecursive());
        commandLine.createArgument().setValue(this.getVersionDateLabel());
        commandLine.createArgument().setValue(this.getWritable());
        commandLine.createArgument().setValue(this.getLogin());
        commandLine.createArgument().setValue(this.getFileTimeStamp());
        commandLine.createArgument().setValue(this.getWritableFiles());
        return commandLine;
    }

    public void setLocalpath(Path localPath) {
        super.setInternalLocalPath(localPath.toString());
    }

    public final void setRecursive(boolean recursive) {
        super.setInternalRecursive(recursive);
    }

    public final void setQuiet(boolean quiet) {
        super.setInternalQuiet(quiet);
    }

    public final void setWritable(boolean writable) {
        super.setInternalWritable(writable);
    }

    public void setVersion(String version) {
        super.setInternalVersion(version);
    }

    public void setDate(String date) {
        super.setInternalDate(date);
    }

    public void setLabel(String label) {
        super.setInternalLabel(label);
    }

    public void setAutoresponse(String response) {
        super.setInternalAutoResponse(response);
    }

    public void setFileTimeStamp(MSVSS.CurrentModUpdated timestamp) {
        super.setInternalFileTimeStamp(timestamp);
    }

    public void setWritableFiles(MSVSS.WritableFiles files) {
        super.setInternalWritableFiles(files);
    }
}

