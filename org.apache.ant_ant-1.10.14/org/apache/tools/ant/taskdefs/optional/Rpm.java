/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

public class Rpm
extends Task {
    private static final String PATH1 = "PATH";
    private static final String PATH2 = "Path";
    private static final String PATH3 = "path";
    private String specFile;
    private File topDir;
    private String command = "-bb";
    private String rpmBuildCommand = null;
    private boolean cleanBuildDir = false;
    private boolean removeSpec = false;
    private boolean removeSource = false;
    private File output;
    private File error;
    private boolean failOnError = false;
    private boolean quiet = false;

    @Override
    public void execute() throws BuildException {
        Commandline toExecute = new Commandline();
        toExecute.setExecutable(this.rpmBuildCommand == null ? this.guessRpmBuildCommand() : this.rpmBuildCommand);
        if (this.topDir != null) {
            toExecute.createArgument().setValue("--define");
            toExecute.createArgument().setValue("_topdir " + this.topDir);
        }
        toExecute.createArgument().setLine(this.command);
        if (this.cleanBuildDir) {
            toExecute.createArgument().setValue("--clean");
        }
        if (this.removeSpec) {
            toExecute.createArgument().setValue("--rmspec");
        }
        if (this.removeSource) {
            toExecute.createArgument().setValue("--rmsource");
        }
        toExecute.createArgument().setValue("SPECS/" + this.specFile);
        PumpStreamHandler streamhandler = null;
        OutputStream outputstream = null;
        OutputStream errorstream = null;
        if (this.error == null && this.output == null) {
            streamhandler = !this.quiet ? new LogStreamHandler(this, 2, 1) : new LogStreamHandler(this, 4, 4);
        } else {
            BufferedOutputStream bos;
            OutputStream fos;
            if (this.output != null) {
                fos = null;
                try {
                    fos = Files.newOutputStream(this.output.toPath(), new OpenOption[0]);
                    bos = new BufferedOutputStream(fos);
                    outputstream = new PrintStream(bos);
                }
                catch (IOException e) {
                    FileUtils.close(fos);
                    throw new BuildException(e, this.getLocation());
                }
            } else {
                outputstream = !this.quiet ? new LogOutputStream(this, 2) : new LogOutputStream(this, 4);
            }
            if (this.error != null) {
                fos = null;
                try {
                    fos = Files.newOutputStream(this.error.toPath(), new OpenOption[0]);
                    bos = new BufferedOutputStream(fos);
                    errorstream = new PrintStream(bos);
                }
                catch (IOException e) {
                    FileUtils.close(fos);
                    throw new BuildException(e, this.getLocation());
                }
            } else {
                errorstream = !this.quiet ? new LogOutputStream(this, 1) : new LogOutputStream(this, 4);
            }
            streamhandler = new PumpStreamHandler(outputstream, errorstream);
        }
        Execute exe = this.getExecute(toExecute, streamhandler);
        try {
            this.log("Building the RPM based on the " + this.specFile + " file");
            int returncode = exe.execute();
            if (Execute.isFailure(returncode)) {
                String msg = "'" + toExecute.getExecutable() + "' failed with exit code " + returncode;
                if (this.failOnError) {
                    throw new BuildException(msg);
                }
                this.log(msg, 0);
            }
        }
        catch (IOException e) {
            throw new BuildException(e, this.getLocation());
        }
        finally {
            FileUtils.close(outputstream);
            FileUtils.close(errorstream);
        }
    }

    public void setTopDir(File td) {
        this.topDir = td;
    }

    public void setCommand(String c) {
        this.command = c;
    }

    public void setSpecFile(String sf) {
        if (sf == null || sf.trim().isEmpty()) {
            throw new BuildException("You must specify a spec file", this.getLocation());
        }
        this.specFile = sf;
    }

    public void setCleanBuildDir(boolean cbd) {
        this.cleanBuildDir = cbd;
    }

    public void setRemoveSpec(boolean rs) {
        this.removeSpec = rs;
    }

    public void setRemoveSource(boolean rs) {
        this.removeSource = rs;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public void setError(File error) {
        this.error = error;
    }

    public void setRpmBuildCommand(String c) {
        this.rpmBuildCommand = c;
    }

    public void setFailOnError(boolean value) {
        this.failOnError = value;
    }

    public void setQuiet(boolean value) {
        this.quiet = value;
    }

    protected String guessRpmBuildCommand() {
        Map<String, String> env = Execute.getEnvironmentVariables();
        String path = env.get(PATH1);
        if (path == null && (path = env.get(PATH2)) == null) {
            path = env.get(PATH3);
        }
        if (path != null) {
            String[] pElements;
            Path p = new Path(this.getProject(), path);
            for (String pElement : pElements = p.list()) {
                File f = new File(pElement, "rpmbuild" + (Os.isFamily("dos") ? ".exe" : ""));
                if (!f.canRead()) continue;
                return f.getAbsolutePath();
            }
        }
        return "rpm";
    }

    protected Execute getExecute(Commandline toExecute, ExecuteStreamHandler streamhandler) {
        Execute exe = new Execute(streamhandler, null);
        exe.setAntRun(this.getProject());
        if (this.topDir == null) {
            this.topDir = this.getProject().getBaseDir();
        }
        exe.setWorkingDirectory(this.topDir);
        exe.setCommandline(toExecute.getCommandline());
        return exe;
    }
}

