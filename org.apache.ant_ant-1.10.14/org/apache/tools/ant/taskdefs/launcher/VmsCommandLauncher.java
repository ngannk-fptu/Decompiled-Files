/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.launcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.launcher.Java13CommandLauncher;
import org.apache.tools.ant.util.FileUtils;

public class VmsCommandLauncher
extends Java13CommandLauncher {
    @Override
    public Process exec(Project project, String[] cmd, String[] env) throws IOException {
        File cmdFile = this.createCommandFile(project, cmd, env);
        Process p = super.exec(project, new String[]{cmdFile.getPath()}, env);
        this.deleteAfter(cmdFile, p);
        return p;
    }

    @Override
    public Process exec(Project project, String[] cmd, String[] env, File workingDir) throws IOException {
        File cmdFile = this.createCommandFile(project, cmd, env);
        Process p = super.exec(project, new String[]{cmdFile.getPath()}, env, workingDir);
        this.deleteAfter(cmdFile, p);
        return p;
    }

    private File createCommandFile(Project project, String[] cmd, String[] env) throws IOException {
        File script = FILE_UTILS.createTempFile(project, "ANT", ".COM", null, true, true);
        try (BufferedWriter out = new BufferedWriter(new FileWriter(script));){
            if (env != null) {
                for (String variable : env) {
                    int eqIndex = variable.indexOf(61);
                    if (eqIndex == -1) continue;
                    out.write("$ DEFINE/NOLOG ");
                    out.write(variable.substring(0, eqIndex));
                    out.write(" \"");
                    out.write(variable.substring(eqIndex + 1));
                    out.write(34);
                    out.newLine();
                }
            }
            out.write("$ " + cmd[0]);
            for (int i = 1; i < cmd.length; ++i) {
                out.write(" -");
                out.newLine();
                out.write(cmd[i]);
            }
        }
        return script;
    }

    private void deleteAfter(File f, Process p) {
        new Thread(() -> {
            try {
                p.waitFor();
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            FileUtils.delete(f);
        }).start();
    }
}

