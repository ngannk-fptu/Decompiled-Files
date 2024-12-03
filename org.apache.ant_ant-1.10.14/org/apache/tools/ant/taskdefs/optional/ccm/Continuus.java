/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.ccm;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.FileUtils;

public abstract class Continuus
extends Task {
    private static final String CCM_EXE = "ccm";
    public static final String COMMAND_CREATE_TASK = "create_task";
    public static final String COMMAND_CHECKOUT = "co";
    public static final String COMMAND_CHECKIN = "ci";
    public static final String COMMAND_RECONFIGURE = "reconfigure";
    public static final String COMMAND_DEFAULT_TASK = "default_task";
    private String ccmDir = "";
    private String ccmAction = "";

    public String getCcmAction() {
        return this.ccmAction;
    }

    public void setCcmAction(String v) {
        this.ccmAction = v;
    }

    public final void setCcmDir(String dir) {
        this.ccmDir = FileUtils.translatePath(dir);
    }

    protected final String getCcmCommand() {
        String toReturn = this.ccmDir;
        if (!toReturn.isEmpty() && !toReturn.endsWith("/")) {
            toReturn = toReturn + "/";
        }
        toReturn = toReturn + CCM_EXE;
        return toReturn;
    }

    protected int run(Commandline cmd, ExecuteStreamHandler handler) {
        try {
            Execute exe = new Execute(handler);
            exe.setAntRun(this.getProject());
            exe.setWorkingDirectory(this.getProject().getBaseDir());
            exe.setCommandline(cmd.getCommandline());
            return exe.execute();
        }
        catch (IOException e) {
            throw new BuildException(e, this.getLocation());
        }
    }

    protected int run(Commandline cmd) {
        return this.run(cmd, new LogStreamHandler(this, 3, 1));
    }
}

