/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

public class BorlandGenerateClient
extends Task {
    static final String JAVA_MODE = "java";
    static final String FORK_MODE = "fork";
    boolean debug = false;
    File ejbjarfile = null;
    File clientjarfile = null;
    Path classpath;
    String mode = "fork";
    int version = 4;

    public void setVersion(int version) {
        this.version = version;
    }

    public void setMode(String s) {
        this.mode = s;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setEjbjar(File ejbfile) {
        this.ejbjarfile = ejbfile;
    }

    public void setClientjar(File clientjar) {
        this.clientjarfile = clientjar;
    }

    public void setClasspath(Path classpath) {
        if (this.classpath == null) {
            this.classpath = classpath;
        } else {
            this.classpath.append(classpath);
        }
    }

    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        return this.classpath.createPath();
    }

    public void setClasspathRef(Reference r) {
        this.createClasspath().setRefid(r);
    }

    @Override
    public void execute() throws BuildException {
        if (this.ejbjarfile == null || this.ejbjarfile.isDirectory()) {
            throw new BuildException("invalid ejb jar file.");
        }
        if (this.clientjarfile == null || this.clientjarfile.isDirectory()) {
            this.log("invalid or missing client jar file.", 3);
            String ejbjarname = this.ejbjarfile.getAbsolutePath();
            String clientname = ejbjarname.substring(0, ejbjarname.lastIndexOf("."));
            clientname = clientname + "client.jar";
            this.clientjarfile = new File(clientname);
        }
        if (this.mode == null) {
            this.log("mode is null default mode  is java");
            this.setMode(JAVA_MODE);
        }
        if (this.version != 5 && this.version != 4) {
            throw new BuildException("version %d is not supported", this.version);
        }
        this.log("client jar file is " + this.clientjarfile);
        if (FORK_MODE.equalsIgnoreCase(this.mode)) {
            this.executeFork();
        } else {
            this.executeJava();
        }
    }

    protected void executeJava() throws BuildException {
        try {
            if (this.version == 5) {
                throw new BuildException("java mode is supported only for previous version <= %d", 4);
            }
            this.log("mode : java");
            Java execTask = new Java(this);
            execTask.setDir(new File("."));
            execTask.setClassname("com.inprise.server.commandline.EJBUtilities");
            execTask.setClasspath(this.classpath.concatSystemClasspath());
            execTask.setFork(true);
            execTask.createArg().setValue("generateclient");
            if (this.debug) {
                execTask.createArg().setValue("-trace");
            }
            execTask.createArg().setValue("-short");
            execTask.createArg().setValue("-jarfile");
            execTask.createArg().setValue(this.ejbjarfile.getAbsolutePath());
            execTask.createArg().setValue("-single");
            execTask.createArg().setValue("-clientjarfile");
            execTask.createArg().setValue(this.clientjarfile.getAbsolutePath());
            this.log("Calling EJBUtilities", 3);
            execTask.execute();
        }
        catch (Exception e) {
            throw new BuildException("Exception while calling generateclient", e);
        }
    }

    protected void executeFork() throws BuildException {
        if (this.version == 4) {
            this.executeForkV4();
        }
        if (this.version == 5) {
            this.executeForkV5();
        }
    }

    protected void executeForkV4() throws BuildException {
        try {
            this.log("mode : fork 4", 4);
            ExecTask execTask = new ExecTask(this);
            execTask.setDir(new File("."));
            execTask.setExecutable("iastool");
            execTask.createArg().setValue("generateclient");
            if (this.debug) {
                execTask.createArg().setValue("-trace");
            }
            execTask.createArg().setValue("-short");
            execTask.createArg().setValue("-jarfile");
            execTask.createArg().setValue(this.ejbjarfile.getAbsolutePath());
            execTask.createArg().setValue("-single");
            execTask.createArg().setValue("-clientjarfile");
            execTask.createArg().setValue(this.clientjarfile.getAbsolutePath());
            this.log("Calling iastool", 3);
            execTask.execute();
        }
        catch (Exception e) {
            throw new BuildException("Exception while calling generateclient", e);
        }
    }

    protected void executeForkV5() throws BuildException {
        try {
            this.log("mode : fork 5", 4);
            ExecTask execTask = new ExecTask(this);
            execTask.setDir(new File("."));
            execTask.setExecutable("iastool");
            if (this.debug) {
                execTask.createArg().setValue("-debug");
            }
            execTask.createArg().setValue("-genclient");
            execTask.createArg().setValue("-jars");
            execTask.createArg().setValue(this.ejbjarfile.getAbsolutePath());
            execTask.createArg().setValue("-target");
            execTask.createArg().setValue(this.clientjarfile.getAbsolutePath());
            execTask.createArg().setValue("-cp");
            execTask.createArg().setValue(this.classpath.toString());
            this.log("Calling iastool", 3);
            execTask.execute();
        }
        catch (Exception e) {
            throw new BuildException("Exception while calling generateclient", e);
        }
    }
}

