/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.launcher;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.launcher.CommandLauncher;
import org.apache.tools.ant.taskdefs.launcher.CommandLauncherProxy;

public class OS2CommandLauncher
extends CommandLauncherProxy {
    public OS2CommandLauncher(CommandLauncher launcher) {
        super(launcher);
    }

    @Override
    public Process exec(Project project, String[] cmd, String[] env, File workingDir) throws IOException {
        File commandDir = workingDir;
        if (workingDir == null) {
            if (project != null) {
                commandDir = project.getBaseDir();
            } else {
                return this.exec(project, cmd, env);
            }
        }
        int preCmdLength = 7;
        String cmdDir = commandDir.getAbsolutePath();
        String[] newcmd = new String[cmd.length + 7];
        newcmd[0] = "cmd";
        newcmd[1] = "/c";
        newcmd[2] = cmdDir.substring(0, 2);
        newcmd[3] = "&&";
        newcmd[4] = "cd";
        newcmd[5] = cmdDir.substring(2);
        newcmd[6] = "&&";
        System.arraycopy(cmd, 0, newcmd, 7, cmd.length);
        return this.exec(project, newcmd, env);
    }
}

