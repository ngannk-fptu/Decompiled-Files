/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.launcher;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.launcher.CommandLauncher;
import org.apache.tools.ant.taskdefs.launcher.CommandLauncherProxy;

public class PerlScriptCommandLauncher
extends CommandLauncherProxy {
    private final String myScript;

    public PerlScriptCommandLauncher(String script, CommandLauncher launcher) {
        super(launcher);
        this.myScript = script;
    }

    @Override
    public Process exec(Project project, String[] cmd, String[] env, File workingDir) throws IOException {
        if (project == null) {
            if (workingDir == null) {
                return this.exec(project, cmd, env);
            }
            throw new IOException("Cannot locate antRun script: No project provided");
        }
        String antHome = project.getProperty("ant.home");
        if (antHome == null) {
            throw new IOException("Cannot locate antRun script: Property 'ant.home' not found");
        }
        String antRun = FILE_UTILS.resolveFile(project.getBaseDir(), antHome + File.separator + this.myScript).toString();
        File commandDir = workingDir;
        if (workingDir == null) {
            commandDir = project.getBaseDir();
        }
        String[] newcmd = new String[cmd.length + 3];
        newcmd[0] = "perl";
        newcmd[1] = antRun;
        newcmd[2] = commandDir.getAbsolutePath();
        System.arraycopy(cmd, 0, newcmd, 3, cmd.length);
        return this.exec(project, newcmd, env);
    }
}

