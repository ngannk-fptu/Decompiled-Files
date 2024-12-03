/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.launcher;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.launcher.CommandLauncher;
import org.apache.tools.ant.taskdefs.launcher.CommandLauncherProxy;

public class MacCommandLauncher
extends CommandLauncherProxy {
    public MacCommandLauncher(CommandLauncher launcher) {
        super(launcher);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Process exec(Project project, String[] cmd, String[] env, File workingDir) throws IOException {
        if (workingDir == null) {
            return this.exec(project, cmd, env);
        }
        System.getProperties().put("user.dir", workingDir.getAbsolutePath());
        try {
            Process process = this.exec(project, cmd, env);
            return process;
        }
        finally {
            System.getProperties().put("user.dir", System.getProperty("user.dir"));
        }
    }
}

