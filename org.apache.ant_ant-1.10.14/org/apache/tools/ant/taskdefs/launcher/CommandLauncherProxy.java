/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.launcher;

import java.io.IOException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.launcher.CommandLauncher;

public class CommandLauncherProxy
extends CommandLauncher {
    private final CommandLauncher myLauncher;

    protected CommandLauncherProxy(CommandLauncher launcher) {
        this.myLauncher = launcher;
    }

    @Override
    public Process exec(Project project, String[] cmd, String[] env) throws IOException {
        return this.myLauncher.exec(project, cmd, env);
    }
}

