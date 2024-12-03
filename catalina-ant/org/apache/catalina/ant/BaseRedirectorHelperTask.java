/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 *  org.apache.tools.ant.Task
 *  org.apache.tools.ant.taskdefs.Redirector
 *  org.apache.tools.ant.types.RedirectorElement
 */
package org.apache.catalina.ant;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Redirector;
import org.apache.tools.ant.types.RedirectorElement;

public abstract class BaseRedirectorHelperTask
extends Task {
    protected final Redirector redirector = new Redirector((Task)this);
    protected RedirectorElement redirectorElement = null;
    protected OutputStream redirectOutStream = null;
    protected OutputStream redirectErrStream = null;
    PrintStream redirectOutPrintStream = null;
    PrintStream redirectErrPrintStream = null;
    protected boolean failOnError = true;
    protected boolean redirectOutput = false;
    protected boolean redirectorConfigured = false;
    protected boolean alwaysLog = false;

    public void setFailonerror(boolean fail) {
        this.failOnError = fail;
    }

    public boolean isFailOnError() {
        return this.failOnError;
    }

    public void setOutput(File out) {
        this.redirector.setOutput(out);
        this.redirectOutput = true;
    }

    public void setError(File error) {
        this.redirector.setError(error);
        this.redirectOutput = true;
    }

    public void setLogError(boolean logError) {
        this.redirector.setLogError(logError);
        this.redirectOutput = true;
    }

    public void setOutputproperty(String outputProperty) {
        this.redirector.setOutputProperty(outputProperty);
        this.redirectOutput = true;
    }

    public void setErrorProperty(String errorProperty) {
        this.redirector.setErrorProperty(errorProperty);
        this.redirectOutput = true;
    }

    public void setAppend(boolean append) {
        this.redirector.setAppend(append);
        this.redirectOutput = true;
    }

    public void setAlwaysLog(boolean alwaysLog) {
        this.alwaysLog = alwaysLog;
        this.redirectOutput = true;
    }

    public void setCreateEmptyFiles(boolean createEmptyFiles) {
        this.redirector.setCreateEmptyFiles(createEmptyFiles);
        this.redirectOutput = true;
    }

    public void addConfiguredRedirector(RedirectorElement redirectorElement) {
        if (this.redirectorElement != null) {
            throw new BuildException("Cannot have > 1 nested <redirector>s");
        }
        this.redirectorElement = redirectorElement;
    }

    private void configureRedirector() {
        if (this.redirectorElement != null) {
            this.redirectorElement.configure(this.redirector);
            this.redirectOutput = true;
        }
        this.redirectorConfigured = true;
    }

    protected void openRedirector() {
        if (!this.redirectorConfigured) {
            this.configureRedirector();
        }
        if (this.redirectOutput) {
            this.redirector.createStreams();
            this.redirectOutStream = this.redirector.getOutputStream();
            this.redirectOutPrintStream = new PrintStream(this.redirectOutStream);
            this.redirectErrStream = this.redirector.getErrorStream();
            this.redirectErrPrintStream = new PrintStream(this.redirectErrStream);
        }
    }

    protected void closeRedirector() {
        try {
            if (this.redirectOutput && this.redirectOutPrintStream != null) {
                this.redirector.complete();
            }
        }
        catch (IOException ioe) {
            this.log("Error closing redirector: " + ioe.getMessage(), 0);
        }
        this.redirectOutStream = null;
        this.redirectOutPrintStream = null;
        this.redirectErrStream = null;
        this.redirectErrPrintStream = null;
    }

    protected void handleOutput(String output) {
        if (this.redirectOutput) {
            if (this.redirectOutPrintStream == null) {
                this.openRedirector();
            }
            this.redirectOutPrintStream.println(output);
            if (this.alwaysLog) {
                this.log(output, 2);
            }
        } else {
            this.log(output, 2);
        }
    }

    protected void handleFlush(String output) {
        this.handleOutput(output);
        this.redirectOutPrintStream.flush();
    }

    protected void handleErrorOutput(String output) {
        if (this.redirectOutput) {
            if (this.redirectErrPrintStream == null) {
                this.openRedirector();
            }
            this.redirectErrPrintStream.println(output);
            if (this.alwaysLog) {
                this.log(output, 0);
            }
        } else {
            this.log(output, 0);
        }
    }

    protected void handleErrorFlush(String output) {
        this.handleErrorOutput(output);
        this.redirectErrPrintStream.flush();
    }

    protected void handleOutput(String output, int priority) {
        if (priority == 0) {
            this.handleErrorOutput(output);
        } else {
            this.handleOutput(output);
        }
    }
}

