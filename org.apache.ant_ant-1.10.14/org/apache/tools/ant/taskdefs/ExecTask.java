/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.ExecuteWatchdog;
import org.apache.tools.ant.taskdefs.Redirector;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.RedirectorElement;
import org.apache.tools.ant.util.FileUtils;

public class ExecTask
extends Task {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private String os;
    private String osFamily;
    private File dir;
    protected boolean failOnError = false;
    protected boolean newEnvironment = false;
    private Long timeout = null;
    private Environment env = new Environment();
    protected Commandline cmdl = new Commandline();
    private String resultProperty;
    private boolean failIfExecFails = true;
    private String executable;
    private boolean resolveExecutable = false;
    private boolean searchPath = false;
    private boolean spawn = false;
    private boolean incompatibleWithSpawn = false;
    private String inputString;
    private File input;
    private File output;
    private File error;
    protected Redirector redirector = new Redirector(this);
    protected RedirectorElement redirectorElement;
    private boolean vmLauncher = true;

    public ExecTask() {
    }

    public ExecTask(Task owner) {
        this.bindToOwner(owner);
    }

    public void setSpawn(boolean spawn) {
        this.spawn = spawn;
    }

    public void setTimeout(Long value) {
        this.timeout = value;
        this.incompatibleWithSpawn |= this.timeout != null;
    }

    public void setTimeout(Integer value) {
        this.setTimeout(value == null ? null : Long.valueOf(value.intValue()));
    }

    public void setExecutable(String value) {
        this.executable = value;
        this.cmdl.setExecutable(value);
    }

    public void setDir(File d) {
        this.dir = d;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public final String getOs() {
        return this.os;
    }

    public void setCommand(Commandline cmdl) {
        this.log("The command attribute is deprecated.\nPlease use the executable attribute and nested arg elements.", 1);
        this.cmdl = cmdl;
    }

    public void setOutput(File out) {
        this.output = out;
        this.incompatibleWithSpawn = true;
    }

    public void setInput(File input) {
        if (this.inputString != null) {
            throw new BuildException("The \"input\" and \"inputstring\" attributes cannot both be specified");
        }
        this.input = input;
        this.incompatibleWithSpawn = true;
    }

    public void setInputString(String inputString) {
        if (this.input != null) {
            throw new BuildException("The \"input\" and \"inputstring\" attributes cannot both be specified");
        }
        this.inputString = inputString;
        this.incompatibleWithSpawn = true;
    }

    public void setLogError(boolean logError) {
        this.redirector.setLogError(logError);
        this.incompatibleWithSpawn |= logError;
    }

    public void setError(File error) {
        this.error = error;
        this.incompatibleWithSpawn = true;
    }

    public void setOutputproperty(String outputProp) {
        this.redirector.setOutputProperty(outputProp);
        this.incompatibleWithSpawn = true;
    }

    public void setErrorProperty(String errorProperty) {
        this.redirector.setErrorProperty(errorProperty);
        this.incompatibleWithSpawn = true;
    }

    public void setFailonerror(boolean fail) {
        this.failOnError = fail;
        this.incompatibleWithSpawn |= fail;
    }

    public void setNewenvironment(boolean newenv) {
        this.newEnvironment = newenv;
    }

    public void setResolveExecutable(boolean resolveExecutable) {
        this.resolveExecutable = resolveExecutable;
    }

    public void setSearchPath(boolean searchPath) {
        this.searchPath = searchPath;
    }

    public boolean getResolveExecutable() {
        return this.resolveExecutable;
    }

    public void addEnv(Environment.Variable var) {
        this.env.addVariable(var);
    }

    public Commandline.Argument createArg() {
        return this.cmdl.createArgument();
    }

    public void setResultProperty(String resultProperty) {
        this.resultProperty = resultProperty;
        this.incompatibleWithSpawn = true;
    }

    protected void maybeSetResultPropertyValue(int result) {
        if (this.resultProperty != null) {
            String res = Integer.toString(result);
            this.getProject().setNewProperty(this.resultProperty, res);
        }
    }

    public void setFailIfExecutionFails(boolean flag) {
        this.failIfExecFails = flag;
        this.incompatibleWithSpawn |= flag;
    }

    public void setAppend(boolean append) {
        this.redirector.setAppend(append);
        this.incompatibleWithSpawn |= append;
    }

    public void setDiscardOutput(boolean discard) {
        this.redirector.setDiscardOutput(discard);
    }

    public void setDiscardError(boolean discard) {
        this.redirector.setDiscardError(discard);
    }

    public void addConfiguredRedirector(RedirectorElement redirectorElement) {
        if (this.redirectorElement != null) {
            throw new BuildException("cannot have > 1 nested <redirector>s");
        }
        this.redirectorElement = redirectorElement;
        this.incompatibleWithSpawn = true;
    }

    public void setOsFamily(String osFamily) {
        this.osFamily = osFamily.toLowerCase(Locale.ENGLISH);
    }

    public final String getOsFamily() {
        return this.osFamily;
    }

    protected String resolveExecutable(String exec, boolean mustSearchPath) {
        if (!this.resolveExecutable) {
            return exec;
        }
        File executableFile = this.getProject().resolveFile(exec);
        if (executableFile.exists()) {
            return executableFile.getAbsolutePath();
        }
        if (this.dir != null && (executableFile = FILE_UTILS.resolveFile(this.dir, exec)).exists()) {
            return executableFile.getAbsolutePath();
        }
        if (mustSearchPath) {
            String path;
            Path p = null;
            String[] environment = this.env.getVariables();
            if (environment != null) {
                for (String variable : environment) {
                    if (!this.isPath(variable)) continue;
                    p = new Path(this.getProject(), this.getPath(variable));
                    break;
                }
            }
            if (p == null && (path = this.getPath(Execute.getEnvironmentVariables())) != null) {
                p = new Path(this.getProject(), path);
            }
            if (p != null) {
                for (String pathname : p.list()) {
                    executableFile = FILE_UTILS.resolveFile(new File(pathname), exec);
                    if (!executableFile.exists()) continue;
                    return executableFile.getAbsolutePath();
                }
            }
        }
        return exec;
    }

    @Override
    public void execute() throws BuildException {
        if (!this.isValidOs()) {
            return;
        }
        File savedDir = this.dir;
        this.cmdl.setExecutable(this.resolveExecutable(this.executable, this.searchPath));
        this.checkConfiguration();
        try {
            this.runExec(this.prepareExec());
        }
        finally {
            this.dir = savedDir;
        }
    }

    protected void checkConfiguration() throws BuildException {
        if (this.cmdl.getExecutable() == null) {
            throw new BuildException("no executable specified", this.getLocation());
        }
        if (this.dir != null && !this.dir.exists()) {
            throw new BuildException("The directory " + this.dir + " does not exist");
        }
        if (this.dir != null && !this.dir.isDirectory()) {
            throw new BuildException(this.dir + " is not a directory");
        }
        if (this.spawn && this.incompatibleWithSpawn) {
            this.getProject().log("spawn does not allow attributes related to input, output, error, result", 0);
            this.getProject().log("spawn also does not allow timeout", 0);
            this.getProject().log("finally, spawn is not compatible with a nested I/O <redirector>", 0);
            throw new BuildException("You have used an attribute or nested element which is not compatible with spawn");
        }
        this.setupRedirector();
    }

    protected void setupRedirector() {
        this.redirector.setInput(this.input);
        this.redirector.setInputString(this.inputString);
        this.redirector.setOutput(this.output);
        this.redirector.setError(this.error);
    }

    protected boolean isValidOs() {
        if (this.osFamily != null && !Os.isFamily(this.osFamily)) {
            return false;
        }
        String myos = System.getProperty("os.name");
        this.log("Current OS is " + myos, 3);
        if (this.os != null && !this.os.contains(myos)) {
            this.log("This OS, " + myos + " was not found in the specified list of valid OSes: " + this.os, 3);
            return false;
        }
        return true;
    }

    public void setVMLauncher(boolean vmLauncher) {
        this.vmLauncher = vmLauncher;
    }

    protected Execute prepareExec() throws BuildException {
        if (this.dir == null) {
            this.dir = this.getProject().getBaseDir();
        }
        if (this.redirectorElement != null) {
            this.redirectorElement.configure(this.redirector);
        }
        Execute exe = new Execute(this.createHandler(), this.createWatchdog());
        exe.setAntRun(this.getProject());
        exe.setWorkingDirectory(this.dir);
        exe.setVMLauncher(this.vmLauncher);
        String[] environment = this.env.getVariables();
        if (environment != null) {
            for (String variable : environment) {
                this.log("Setting environment variable: " + variable, 3);
            }
        }
        exe.setNewenvironment(this.newEnvironment);
        exe.setEnvironment(environment);
        return exe;
    }

    protected final void runExecute(Execute exe) throws IOException {
        int returnCode = -1;
        if (!this.spawn) {
            returnCode = exe.execute();
            if (exe.killedProcess()) {
                String msg = "Timeout: killed the sub-process";
                if (this.failOnError) {
                    throw new BuildException(msg);
                }
                this.log(msg, 1);
            }
            this.maybeSetResultPropertyValue(returnCode);
            this.redirector.complete();
            if (Execute.isFailure(returnCode)) {
                if (this.failOnError) {
                    throw new BuildException(this.getTaskType() + " returned: " + returnCode, this.getLocation());
                }
                this.log("Result: " + returnCode, 0);
            }
        } else {
            exe.spawn();
        }
    }

    protected void runExec(Execute exe) throws BuildException {
        this.log(this.cmdl.describeCommand(), 3);
        exe.setCommandline(this.cmdl.getCommandline());
        try {
            this.runExecute(exe);
        }
        catch (IOException e) {
            if (this.failIfExecFails) {
                throw new BuildException("Execute failed: " + e.toString(), e, this.getLocation());
            }
            this.log("Execute failed: " + e.toString(), 0);
        }
        finally {
            this.logFlush();
        }
    }

    protected ExecuteStreamHandler createHandler() throws BuildException {
        return this.redirector.createHandler();
    }

    protected ExecuteWatchdog createWatchdog() throws BuildException {
        return this.timeout == null ? null : new ExecuteWatchdog(this.timeout);
    }

    protected void logFlush() {
    }

    private boolean isPath(String line) {
        return line.startsWith("PATH=") || line.startsWith("Path=");
    }

    private String getPath(String line) {
        return line.substring("PATH=".length());
    }

    private String getPath(Map<String, String> map) {
        String p = map.get("PATH");
        return p != null ? p : map.get("Path");
    }
}

