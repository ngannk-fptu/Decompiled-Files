/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.ccm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.optional.ccm.Continuus;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.StringUtils;

public class CCMCreateTask
extends Continuus
implements ExecuteStreamHandler {
    public static final String FLAG_COMMENT = "/synopsis";
    public static final String FLAG_PLATFORM = "/plat";
    public static final String FLAG_RESOLVER = "/resolver";
    public static final String FLAG_RELEASE = "/release";
    public static final String FLAG_SUBSYSTEM = "/subsystem";
    public static final String FLAG_TASK = "/task";
    private String comment = null;
    private String platform = null;
    private String resolver = null;
    private String release = null;
    private String subSystem = null;
    private String task = null;

    public CCMCreateTask() {
        this.setCcmAction("create_task");
    }

    @Override
    public void execute() throws BuildException {
        Commandline commandLine = new Commandline();
        commandLine.setExecutable(this.getCcmCommand());
        commandLine.createArgument().setValue(this.getCcmAction());
        this.checkOptions(commandLine);
        if (Execute.isFailure(this.run(commandLine, this))) {
            throw new BuildException("Failed executing: " + commandLine, this.getLocation());
        }
        Commandline commandLine2 = new Commandline();
        commandLine2.setExecutable(this.getCcmCommand());
        commandLine2.createArgument().setValue("default_task");
        commandLine2.createArgument().setValue(this.getTask());
        this.log(commandLine.describeCommand(), 4);
        if (this.run(commandLine2) != 0) {
            throw new BuildException("Failed executing: " + commandLine2, this.getLocation());
        }
    }

    private void checkOptions(Commandline cmd) {
        if (this.getComment() != null) {
            cmd.createArgument().setValue(FLAG_COMMENT);
            cmd.createArgument().setValue("\"" + this.getComment() + "\"");
        }
        if (this.getPlatform() != null) {
            cmd.createArgument().setValue(FLAG_PLATFORM);
            cmd.createArgument().setValue(this.getPlatform());
        }
        if (this.getResolver() != null) {
            cmd.createArgument().setValue(FLAG_RESOLVER);
            cmd.createArgument().setValue(this.getResolver());
        }
        if (this.getSubSystem() != null) {
            cmd.createArgument().setValue(FLAG_SUBSYSTEM);
            cmd.createArgument().setValue("\"" + this.getSubSystem() + "\"");
        }
        if (this.getRelease() != null) {
            cmd.createArgument().setValue(FLAG_RELEASE);
            cmd.createArgument().setValue(this.getRelease());
        }
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String v) {
        this.comment = v;
    }

    public String getPlatform() {
        return this.platform;
    }

    public void setPlatform(String v) {
        this.platform = v;
    }

    public String getResolver() {
        return this.resolver;
    }

    public void setResolver(String v) {
        this.resolver = v;
    }

    public String getRelease() {
        return this.release;
    }

    public void setRelease(String v) {
        this.release = v;
    }

    public String getSubSystem() {
        return this.subSystem;
    }

    public void setSubSystem(String v) {
        this.subSystem = v;
    }

    public String getTask() {
        return this.task;
    }

    public void setTask(String v) {
        this.task = v;
    }

    @Override
    public void start() throws IOException {
    }

    @Override
    public void stop() {
    }

    @Override
    public void setProcessInputStream(OutputStream param1) throws IOException {
    }

    @Override
    public void setProcessErrorStream(InputStream is) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is));){
            String s = reader.readLine();
            if (s != null) {
                this.log("err " + s, 4);
            }
        }
    }

    @Override
    public void setProcessOutputStream(InputStream is) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is));){
            String buffer = reader.readLine();
            if (buffer != null) {
                this.log("buffer:" + buffer, 4);
                String taskstring = buffer.substring(buffer.indexOf(32)).trim();
                taskstring = taskstring.substring(0, taskstring.lastIndexOf(32)).trim();
                this.setTask(taskstring);
                this.log("task is " + this.getTask(), 4);
            }
        }
        catch (NullPointerException npe) {
            this.log("error procession stream, null pointer exception", 0);
            this.log(StringUtils.getStackTrace(npe), 0);
            throw new BuildException(npe);
        }
        catch (Exception e) {
            this.log("error procession stream " + e.getMessage(), 0);
            throw new BuildException(e.getMessage());
        }
    }
}

