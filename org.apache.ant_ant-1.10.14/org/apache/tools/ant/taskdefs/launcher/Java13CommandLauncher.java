/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.launcher;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.launcher.CommandLauncher;
import org.apache.tools.ant.types.Commandline;

public class Java13CommandLauncher
extends CommandLauncher {
    @Override
    public Process exec(Project project, String[] cmd, String[] env, File workingDir) throws IOException {
        try {
            if (project != null) {
                project.log("Execute:Java13CommandLauncher: " + Commandline.describeCommand(cmd), 4);
            }
            return Runtime.getRuntime().exec(cmd, env, workingDir);
        }
        catch (IOException ioex) {
            throw ioex;
        }
        catch (Exception exc) {
            throw new BuildException("Unable to execute command", exc);
        }
    }
}

