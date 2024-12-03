/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.launcher;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.taskdefs.launcher.Java13CommandLauncher;
import org.apache.tools.ant.taskdefs.launcher.MacCommandLauncher;
import org.apache.tools.ant.taskdefs.launcher.OS2CommandLauncher;
import org.apache.tools.ant.taskdefs.launcher.PerlScriptCommandLauncher;
import org.apache.tools.ant.taskdefs.launcher.ScriptCommandLauncher;
import org.apache.tools.ant.taskdefs.launcher.VmsCommandLauncher;
import org.apache.tools.ant.taskdefs.launcher.WinNTCommandLauncher;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.FileUtils;

public class CommandLauncher {
    protected static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static CommandLauncher vmLauncher = null;
    private static CommandLauncher shellLauncher = null;

    public Process exec(Project project, String[] cmd, String[] env) throws IOException {
        if (project != null) {
            project.log("Execute:CommandLauncher: " + Commandline.describeCommand(cmd), 4);
        }
        return Runtime.getRuntime().exec(cmd, env);
    }

    public Process exec(Project project, String[] cmd, String[] env, File workingDir) throws IOException {
        if (workingDir == null) {
            return this.exec(project, cmd, env);
        }
        throw new IOException("Cannot execute a process in different directory under this JVM");
    }

    public static CommandLauncher getShellLauncher(Project project) {
        CommandLauncher launcher = CommandLauncher.extractLauncher("ant.shellLauncher", project);
        if (launcher == null) {
            launcher = shellLauncher;
        }
        return launcher;
    }

    public static CommandLauncher getVMLauncher(Project project) {
        CommandLauncher launcher = CommandLauncher.extractLauncher("ant.vmLauncher", project);
        if (launcher == null) {
            launcher = vmLauncher;
        }
        return launcher;
    }

    private static CommandLauncher extractLauncher(String referenceName, Project project) {
        return Optional.ofNullable(project).map(p -> (CommandLauncher)p.getReference(referenceName)).orElseGet(() -> CommandLauncher.getSystemLauncher(referenceName));
    }

    private static CommandLauncher getSystemLauncher(String launcherRefId) {
        String launcherClass = System.getProperty(launcherRefId);
        if (launcherClass != null) {
            try {
                return Class.forName(launcherClass).asSubclass(CommandLauncher.class).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                System.err.println("Could not instantiate launcher class " + launcherClass + ": " + e.getMessage());
            }
        }
        return null;
    }

    public static void setVMLauncher(Project project, CommandLauncher launcher) {
        if (project != null) {
            project.addReference("ant.vmLauncher", launcher);
        }
    }

    public static void setShellLauncher(Project project, CommandLauncher launcher) {
        if (project != null) {
            project.addReference("ant.shellLauncher", launcher);
        }
    }

    static {
        if (!Os.isFamily("os/2")) {
            vmLauncher = new Java13CommandLauncher();
        }
        if (Os.isFamily("mac") && !Os.isFamily("unix")) {
            shellLauncher = new MacCommandLauncher(new CommandLauncher());
        } else if (Os.isFamily("os/2")) {
            shellLauncher = new OS2CommandLauncher(new CommandLauncher());
        } else if (Os.isFamily("windows")) {
            CommandLauncher baseLauncher = new CommandLauncher();
            shellLauncher = !Os.isFamily("win9x") ? new WinNTCommandLauncher(baseLauncher) : new ScriptCommandLauncher("bin/antRun.bat", baseLauncher);
        } else if (Os.isFamily("netware")) {
            CommandLauncher baseLauncher = new CommandLauncher();
            shellLauncher = new PerlScriptCommandLauncher("bin/antRun.pl", baseLauncher);
        } else {
            shellLauncher = Os.isFamily("openvms") ? new VmsCommandLauncher() : new ScriptCommandLauncher("bin/antRun", new CommandLauncher());
        }
    }
}

