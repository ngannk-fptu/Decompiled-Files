/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.ExecuteWatchdog;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.taskdefs.ProcessDestroyer;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.taskdefs.launcher.CommandLauncher;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.FileUtils;

public class Execute {
    private static final int ONE_SECOND = 1000;
    public static final int INVALID = Integer.MAX_VALUE;
    private static String antWorkingDirectory = System.getProperty("user.dir");
    private static Map<String, String> procEnvironment = null;
    private static ProcessDestroyer processDestroyer = new ProcessDestroyer();
    private static boolean environmentCaseInSensitive = false;
    private String[] cmdl = null;
    private String[] env = null;
    private int exitValue = Integer.MAX_VALUE;
    private ExecuteStreamHandler streamHandler;
    private final ExecuteWatchdog watchdog;
    private File workingDirectory = null;
    private Project project = null;
    private boolean newEnvironment = false;
    private boolean useVMLauncher = true;

    @Deprecated
    public void setSpawn(boolean spawn) {
    }

    public static synchronized Map<String, String> getEnvironmentVariables() {
        if (procEnvironment != null) {
            return procEnvironment;
        }
        if (!Os.isFamily("openvms")) {
            try {
                procEnvironment = System.getenv();
                return procEnvironment;
            }
            catch (Exception x) {
                x.printStackTrace();
            }
        }
        procEnvironment = new LinkedHashMap<String, String>();
        try {
            int eq;
            String line;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Execute exe = new Execute(new PumpStreamHandler(out));
            exe.setCommandline(Execute.getProcEnvCommand());
            exe.setNewenvironment(true);
            int retval = exe.execute();
            if (retval != 0) {
                // empty if block
            }
            BufferedReader in = new BufferedReader(new StringReader(Execute.toString(out)));
            if (Os.isFamily("openvms")) {
                procEnvironment = Execute.getVMSLogicals(in);
                return procEnvironment;
            }
            StringBuilder var = null;
            while ((line = in.readLine()) != null) {
                if (line.contains("=")) {
                    if (var != null) {
                        eq = var.toString().indexOf(61);
                        procEnvironment.put(var.substring(0, eq), var.substring(eq + 1));
                    }
                    var = new StringBuilder(line);
                    continue;
                }
                if (var == null) {
                    var = new StringBuilder(System.lineSeparator() + line);
                    continue;
                }
                var.append(System.lineSeparator()).append(line);
            }
            if (var != null) {
                eq = var.toString().indexOf(61);
                procEnvironment.put(var.substring(0, eq), var.substring(eq + 1));
            }
        }
        catch (IOException exc) {
            exc.printStackTrace();
        }
        return procEnvironment;
    }

    @Deprecated
    public static synchronized Vector<String> getProcEnvironment() {
        Vector<String> v = new Vector<String>();
        Execute.getEnvironmentVariables().forEach((key, value) -> v.add(key + "=" + value));
        return v;
    }

    private static String[] getProcEnvCommand() {
        if (Os.isFamily("os/2")) {
            return new String[]{"cmd", "/c", "set"};
        }
        if (Os.isFamily("windows")) {
            if (Os.isFamily("win9x")) {
                return new String[]{"command.com", "/c", "set"};
            }
            return new String[]{"cmd", "/c", "set"};
        }
        if (Os.isFamily("z/os") || Os.isFamily("unix")) {
            String[] cmd = new String[]{new File("/bin/env").canRead() ? "/bin/env" : (new File("/usr/bin/env").canRead() ? "/usr/bin/env" : "env")};
            return cmd;
        }
        if (Os.isFamily("netware") || Os.isFamily("os/400")) {
            return new String[]{"env"};
        }
        if (Os.isFamily("openvms")) {
            return new String[]{"show", "logical"};
        }
        return null;
    }

    public static String toString(ByteArrayOutputStream bos) {
        if (Os.isFamily("z/os")) {
            try {
                return bos.toString("Cp1047");
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
            }
        } else if (Os.isFamily("os/400")) {
            try {
                return bos.toString("Cp500");
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                // empty catch block
            }
        }
        return bos.toString();
    }

    public Execute() {
        this(new PumpStreamHandler(), null);
    }

    public Execute(ExecuteStreamHandler streamHandler) {
        this(streamHandler, null);
    }

    public Execute(ExecuteStreamHandler streamHandler, ExecuteWatchdog watchdog) {
        this.setStreamHandler(streamHandler);
        this.watchdog = watchdog;
        if (Os.isFamily("openvms")) {
            this.useVMLauncher = false;
        }
    }

    public void setStreamHandler(ExecuteStreamHandler streamHandler) {
        this.streamHandler = streamHandler;
    }

    public String[] getCommandline() {
        return this.cmdl;
    }

    public void setCommandline(String[] commandline) {
        this.cmdl = commandline;
    }

    public void setNewenvironment(boolean newenv) {
        this.newEnvironment = newenv;
    }

    public String[] getEnvironment() {
        return this.env == null || this.newEnvironment ? this.env : this.patchEnvironment();
    }

    public void setEnvironment(String[] env) {
        this.env = env;
    }

    public void setWorkingDirectory(File wd) {
        this.workingDirectory = wd;
    }

    public File getWorkingDirectory() {
        return this.workingDirectory == null ? new File(antWorkingDirectory) : this.workingDirectory;
    }

    public void setAntRun(Project project) throws BuildException {
        this.project = project;
    }

    public void setVMLauncher(boolean useVMLauncher) {
        this.useVMLauncher = useVMLauncher;
    }

    public static Process launch(Project project, String[] command, String[] env, File dir, boolean useVM) throws IOException {
        if (dir != null && !dir.exists()) {
            throw new BuildException("%s doesn't exist.", dir);
        }
        CommandLauncher vmLauncher = CommandLauncher.getVMLauncher(project);
        CommandLauncher launcher = useVM && vmLauncher != null ? vmLauncher : CommandLauncher.getShellLauncher(project);
        return launcher.exec(project, command, env, dir);
    }

    public int execute() throws IOException {
        if (this.workingDirectory != null && !this.workingDirectory.exists()) {
            throw new BuildException("%s doesn't exist.", this.workingDirectory);
        }
        Process process = Execute.launch(this.project, this.getCommandline(), this.getEnvironment(), this.workingDirectory, this.useVMLauncher);
        try {
            this.streamHandler.setProcessInputStream(process.getOutputStream());
            this.streamHandler.setProcessOutputStream(process.getInputStream());
            this.streamHandler.setProcessErrorStream(process.getErrorStream());
        }
        catch (IOException e) {
            process.destroy();
            throw e;
        }
        this.streamHandler.start();
        try {
            processDestroyer.add(process);
            if (this.watchdog != null) {
                this.watchdog.start(process);
            }
            this.waitFor(process);
            if (this.watchdog != null) {
                this.watchdog.stop();
            }
            this.streamHandler.stop();
            Execute.closeStreams(process);
            if (this.watchdog != null) {
                this.watchdog.checkException();
            }
            int e = this.getExitValue();
            return e;
        }
        catch (ThreadDeath t) {
            process.destroy();
            throw t;
        }
        finally {
            processDestroyer.remove(process);
        }
    }

    public void spawn() throws IOException {
        if (this.workingDirectory != null && !this.workingDirectory.exists()) {
            throw new BuildException("%s doesn't exist.", this.workingDirectory);
        }
        Process process = Execute.launch(this.project, this.getCommandline(), this.getEnvironment(), this.workingDirectory, this.useVMLauncher);
        if (Os.isFamily("windows")) {
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException e) {
                this.project.log("interruption in the sleep after having spawned a process", 3);
            }
        }
        OutputStream dummyOut = new OutputStream(){

            @Override
            public void write(int b) throws IOException {
            }
        };
        PumpStreamHandler handler = new PumpStreamHandler(dummyOut);
        handler.setProcessErrorStream(process.getErrorStream());
        handler.setProcessOutputStream(process.getInputStream());
        handler.start();
        process.getOutputStream().close();
        this.project.log("spawned process " + process.toString(), 3);
    }

    protected void waitFor(Process process) {
        try {
            process.waitFor();
            this.setExitValue(process.exitValue());
        }
        catch (InterruptedException e) {
            process.destroy();
        }
    }

    protected void setExitValue(int value) {
        this.exitValue = value;
    }

    public int getExitValue() {
        return this.exitValue;
    }

    public static boolean isFailure(int exitValue) {
        return Os.isFamily("openvms") ? exitValue % 2 == 0 : exitValue != 0;
    }

    public boolean isFailure() {
        return Execute.isFailure(this.getExitValue());
    }

    public boolean killedProcess() {
        return this.watchdog != null && this.watchdog.killedProcess();
    }

    private String[] patchEnvironment() {
        if (Os.isFamily("openvms")) {
            return this.env;
        }
        LinkedHashMap<String, String> osEnv = new LinkedHashMap<String, String>(Execute.getEnvironmentVariables());
        for (String keyValue : this.env) {
            String key = keyValue.substring(0, keyValue.indexOf(61));
            if (osEnv.remove(key) == null && environmentCaseInSensitive) {
                for (String osEnvItem : osEnv.keySet()) {
                    if (!osEnvItem.equalsIgnoreCase(key)) continue;
                    key = osEnvItem;
                    break;
                }
            }
            osEnv.put(key, keyValue.substring(key.length() + 1));
        }
        return (String[])osEnv.entrySet().stream().map(e -> (String)e.getKey() + "=" + (String)e.getValue()).toArray(String[]::new);
    }

    public static void runCommand(Task task, String ... cmdline) throws BuildException {
        try {
            task.log(Commandline.describeCommand(cmdline), 3);
            Execute exe = new Execute(new LogStreamHandler(task, 2, 0));
            exe.setAntRun(task.getProject());
            exe.setCommandline(cmdline);
            int retval = exe.execute();
            if (Execute.isFailure(retval)) {
                throw new BuildException(cmdline[0] + " failed with return code " + retval, task.getLocation());
            }
        }
        catch (IOException exc) {
            throw new BuildException("Could not launch " + cmdline[0] + ": " + exc, task.getLocation());
        }
    }

    public static void closeStreams(Process process) {
        FileUtils.close(process.getInputStream());
        FileUtils.close(process.getOutputStream());
        FileUtils.close(process.getErrorStream());
    }

    private static Map<String, String> getVMSLogicals(BufferedReader in) throws IOException {
        String line;
        HashMap<String, String> logicals = new HashMap<String, String>();
        String logName = null;
        String logValue = null;
        while ((line = in.readLine()) != null) {
            int eqIndex;
            String newLogName;
            if (line.startsWith("\t=")) {
                if (logName == null) continue;
                logValue = logValue + "," + line.substring(4, line.length() - 1);
                continue;
            }
            if (!line.startsWith("  \"")) continue;
            if (logName != null) {
                logicals.put(logName, logValue);
            }
            if (logicals.containsKey(newLogName = line.substring(3, (eqIndex = line.indexOf(61)) - 2))) {
                logName = null;
                continue;
            }
            logName = newLogName;
            logValue = line.substring(eqIndex + 3, line.length() - 1);
        }
        if (logName != null) {
            logicals.put(logName, logValue);
        }
        return logicals;
    }

    static {
        if (Os.isFamily("windows")) {
            environmentCaseInSensitive = true;
        }
    }
}

