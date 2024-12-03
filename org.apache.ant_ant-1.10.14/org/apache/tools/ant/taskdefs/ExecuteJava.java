/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteWatchdog;
import org.apache.tools.ant.taskdefs.Redirector;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Permissions;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.util.TimeoutObserver;
import org.apache.tools.ant.util.Watchdog;

public class ExecuteJava
implements Runnable,
TimeoutObserver {
    private Commandline javaCommand = null;
    private Path classpath = null;
    private CommandlineJava.SysProperties sysProperties = null;
    private Permissions perm = null;
    private Method main = null;
    private Long timeout = null;
    private volatile Throwable caught = null;
    private volatile boolean timedOut = false;
    private boolean done = false;
    private Thread thread = null;

    public void setJavaCommand(Commandline javaCommand) {
        this.javaCommand = javaCommand;
    }

    public void setClasspath(Path p) {
        this.classpath = p;
    }

    public void setSystemProperties(CommandlineJava.SysProperties s) {
        this.sysProperties = s;
    }

    public void setPermissions(Permissions permissions) {
        this.perm = permissions;
    }

    @Deprecated
    public void setOutput(PrintStream out) {
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void execute(Project project) throws BuildException {
        String classname = this.javaCommand.getExecutable();
        AntClassLoader loader = null;
        try {
            Class<?> target;
            if (this.sysProperties != null) {
                this.sysProperties.setSystem();
            }
            try {
                if (this.classpath == null) {
                    target = Class.forName(classname);
                } else {
                    loader = project.createClassLoader(this.classpath);
                    loader.setParent(project.getCoreLoader());
                    loader.setParentFirst(false);
                    loader.addJavaLibraries();
                    loader.setIsolated(true);
                    loader.setThreadContextLoader();
                    loader.forceLoadClass(classname);
                    target = Class.forName(classname, true, loader);
                }
            }
            catch (ClassNotFoundException e) {
                throw new BuildException("Could not find %s. Make sure you have it in your classpath", classname);
            }
            this.main = target.getMethod("main", String[].class);
            if (this.main == null) {
                throw new BuildException("Could not find main() method in %s", classname);
            }
            if ((this.main.getModifiers() & 8) == 0) {
                throw new BuildException("main() method in %s is not declared static", classname);
            }
            if (this.timeout == null) {
                this.run();
            } else {
                this.thread = new Thread((Runnable)this, "ExecuteJava");
                Task currentThreadTask = project.getThreadTask(Thread.currentThread());
                project.registerThreadTask(this.thread, currentThreadTask);
                this.thread.setDaemon(true);
                Watchdog w = new Watchdog(this.timeout);
                w.addTimeoutObserver(this);
                ExecuteJava executeJava = this;
                synchronized (executeJava) {
                    this.thread.start();
                    w.start();
                    try {
                        while (!this.done) {
                            this.wait();
                        }
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                    if (this.timedOut) {
                        project.log("Timeout: sub-process interrupted", 1);
                    } else {
                        this.thread = null;
                        w.stop();
                    }
                }
            }
            if (this.caught != null) {
                throw this.caught;
            }
        }
        catch (SecurityException | ThreadDeath | BuildException e) {
            throw e;
        }
        catch (Throwable e) {
            throw new BuildException(e);
        }
        finally {
            if (loader != null) {
                loader.resetThreadContextLoader();
                loader.cleanup();
                loader = null;
            }
            if (this.sysProperties != null) {
                this.sysProperties.restoreSystem();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        Object[] argument = new Object[]{this.javaCommand.getArguments()};
        boolean restoreSecMgr = false;
        try {
            if (this.perm != null) {
                this.perm.setSecurityManager();
                restoreSecMgr = true;
            }
            this.main.invoke(null, argument);
        }
        catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (!(t instanceof InterruptedException)) {
                this.caught = t;
            }
        }
        catch (Throwable t) {
            this.caught = t;
        }
        finally {
            if (this.perm != null && restoreSecMgr) {
                this.perm.restoreSecurityManager();
            }
            ExecuteJava e = this;
            synchronized (e) {
                this.done = true;
                this.notifyAll();
            }
        }
    }

    @Override
    public synchronized void timeoutOccured(Watchdog w) {
        if (this.thread != null) {
            this.timedOut = true;
            this.thread.interrupt();
        }
        this.done = true;
        this.notifyAll();
    }

    public synchronized boolean killedProcess() {
        return this.timedOut;
    }

    public int fork(ProjectComponent pc) throws BuildException {
        CommandlineJava cmdl = new CommandlineJava();
        cmdl.setClassname(this.javaCommand.getExecutable());
        for (String arg : this.javaCommand.getArguments()) {
            cmdl.createArgument().setValue(arg);
        }
        if (this.classpath != null) {
            cmdl.createClasspath(pc.getProject()).append(this.classpath);
        }
        if (this.sysProperties != null) {
            cmdl.addSysproperties(this.sysProperties);
        }
        Redirector redirector = new Redirector(pc);
        Execute exe = new Execute(redirector.createHandler(), this.timeout == null ? null : new ExecuteWatchdog(this.timeout));
        exe.setAntRun(pc.getProject());
        if (Os.isFamily("openvms")) {
            ExecuteJava.setupCommandLineForVMS(exe, cmdl.getCommandline());
        } else {
            exe.setCommandline(cmdl.getCommandline());
        }
        try {
            int rc = exe.execute();
            redirector.complete();
            int n = rc;
            return n;
        }
        catch (IOException e) {
            throw new BuildException(e);
        }
        finally {
            this.timedOut = exe.killedProcess();
        }
    }

    public static void setupCommandLineForVMS(Execute exe, String[] command) {
        exe.setVMLauncher(true);
        File vmsJavaOptionFile = null;
        try {
            String[] args = new String[command.length - 1];
            System.arraycopy(command, 1, args, 0, command.length - 1);
            vmsJavaOptionFile = JavaEnvUtils.createVmsJavaOptionFile(args);
            vmsJavaOptionFile.deleteOnExit();
            String[] vmsCmd = new String[]{command[0], "-V", vmsJavaOptionFile.getPath()};
            exe.setCommandline(vmsCmd);
        }
        catch (IOException e) {
            throw new BuildException("Failed to create a temporary file for \"-V\" switch");
        }
    }
}

