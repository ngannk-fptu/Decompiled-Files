/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.JikesOutputParser;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.util.FileUtils;

@Deprecated
public class Jikes {
    private static final int MAX_FILES_ON_COMMAND_LINE = 250;
    protected JikesOutputParser jop;
    protected String command;
    protected Project project;

    protected Jikes(JikesOutputParser jop, String command, Project project) {
        System.err.println("As of Ant 1.2 released in October 2000, the Jikes class");
        System.err.println("is considered to be dead code by the Ant developers and is unmaintained.");
        System.err.println("Don't use it!");
        this.jop = jop;
        this.command = command;
        this.project = project;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void compile(String[] args) {
        String[] commandArray = null;
        File tmpFile = null;
        try {
            block15: {
                if (Os.isFamily("windows") && args.length > 250) {
                    tmpFile = FileUtils.getFileUtils().createTempFile(this.project, "jikes", "tmp", null, false, true);
                    try (BufferedWriter out = new BufferedWriter(new FileWriter(tmpFile));){
                        for (String arg : args) {
                            out.write(arg);
                            out.newLine();
                        }
                        out.flush();
                        commandArray = new String[]{this.command, "@" + tmpFile.getAbsolutePath()};
                        break block15;
                    }
                    catch (IOException e) {
                        throw new BuildException("Error creating temporary file", e);
                    }
                }
                commandArray = new String[args.length + 1];
                commandArray[0] = this.command;
                System.arraycopy(args, 0, commandArray, 1, args.length);
            }
            try {
                Execute exe = new Execute(this.jop);
                exe.setAntRun(this.project);
                exe.setWorkingDirectory(this.project.getBaseDir());
                exe.setCommandline(commandArray);
                exe.execute();
            }
            catch (IOException e) {
                throw new BuildException("Error running Jikes compiler", e);
            }
        }
        finally {
            if (tmpFile != null && !tmpFile.delete()) {
                tmpFile.deleteOnExit();
            }
        }
    }
}

