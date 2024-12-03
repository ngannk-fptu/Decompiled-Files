/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.text.DateFormat;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.DateUtils;
import org.apache.tools.ant.util.StringUtils;

public class DefaultLogger
implements BuildLogger {
    public static final int LEFT_COLUMN_SIZE = 12;
    protected PrintStream out;
    protected PrintStream err;
    protected int msgOutputLevel = 0;
    private long startTime = System.currentTimeMillis();
    @Deprecated
    protected static final String lSep = StringUtils.LINE_SEP;
    protected boolean emacsMode = false;

    @Override
    public void setMessageOutputLevel(int level) {
        this.msgOutputLevel = level;
    }

    @Override
    public int getMessageOutputLevel() {
        return this.msgOutputLevel;
    }

    @Override
    public void setOutputPrintStream(PrintStream output) {
        this.out = new PrintStream(output, true);
    }

    @Override
    public void setErrorPrintStream(PrintStream err) {
        this.err = new PrintStream(err, true);
    }

    @Override
    public void setEmacsMode(boolean emacsMode) {
        this.emacsMode = emacsMode;
    }

    @Override
    public void buildStarted(BuildEvent event) {
        this.startTime = System.currentTimeMillis();
    }

    static void throwableMessage(StringBuffer m, Throwable error, boolean verbose) {
        String msg2;
        String msg1;
        Throwable cause;
        while (error instanceof BuildException && (cause = error.getCause()) != null && (msg1 = error.toString()).endsWith(msg2 = cause.toString())) {
            m.append(msg1, 0, msg1.length() - msg2.length());
            error = cause;
        }
        if (verbose || !(error instanceof BuildException)) {
            m.append(StringUtils.getStackTrace(error));
        } else {
            m.append(String.format("%s%n", error));
        }
    }

    @Override
    public void buildFinished(BuildEvent event) {
        Throwable error = event.getException();
        StringBuffer message = new StringBuffer();
        if (error == null) {
            message.append(String.format("%n%s", this.getBuildSuccessfulMessage()));
        } else {
            message.append(String.format("%n%s%n", this.getBuildFailedMessage()));
            DefaultLogger.throwableMessage(message, error, 3 <= this.msgOutputLevel);
        }
        message.append(String.format("%nTotal time: %s", DefaultLogger.formatTime(System.currentTimeMillis() - this.startTime)));
        String msg = message.toString();
        if (error == null) {
            this.printMessage(msg, this.out, 3);
        } else {
            this.printMessage(msg, this.err, 0);
        }
        this.log(msg);
    }

    protected String getBuildFailedMessage() {
        return "BUILD FAILED";
    }

    protected String getBuildSuccessfulMessage() {
        return "BUILD SUCCESSFUL";
    }

    @Override
    public void targetStarted(BuildEvent event) {
        if (2 <= this.msgOutputLevel && !event.getTarget().getName().isEmpty()) {
            String msg = String.format("%n%s:", event.getTarget().getName());
            this.printMessage(msg, this.out, event.getPriority());
            this.log(msg);
        }
    }

    @Override
    public void targetFinished(BuildEvent event) {
    }

    @Override
    public void taskStarted(BuildEvent event) {
    }

    @Override
    public void taskFinished(BuildEvent event) {
    }

    @Override
    public void messageLogged(BuildEvent event) {
        int priority = event.getPriority();
        if (priority <= this.msgOutputLevel) {
            StringBuilder message = new StringBuilder();
            if (event.getTask() == null || this.emacsMode) {
                message.append(event.getMessage());
            } else {
                String name = event.getTask().getTaskName();
                String label = "[" + name + "] ";
                int size = 12 - label.length();
                String prefix = size > 0 ? Stream.generate(() -> " ").limit(size).collect(Collectors.joining()) + label : label;
                try (BufferedReader r = new BufferedReader(new StringReader(event.getMessage()));){
                    message.append(r.lines().collect(Collectors.joining(System.lineSeparator() + prefix, prefix, "")));
                }
                catch (IOException e) {
                    message.append(label).append(event.getMessage());
                }
            }
            Throwable ex = event.getException();
            if (4 <= this.msgOutputLevel && ex != null) {
                message.append(String.format("%n%s: ", ex.getClass().getSimpleName())).append(StringUtils.getStackTrace(ex));
            }
            String msg = message.toString();
            if (priority != 0) {
                this.printMessage(msg, this.out, priority);
            } else {
                this.printMessage(msg, this.err, priority);
            }
            this.log(msg);
        }
    }

    protected static String formatTime(long millis) {
        return DateUtils.formatElapsedTime(millis);
    }

    protected void printMessage(String message, PrintStream stream, int priority) {
        stream.println(message);
    }

    protected void log(String message) {
    }

    protected String getTimestamp() {
        Date date = new Date(System.currentTimeMillis());
        DateFormat formatter = DateFormat.getDateTimeInstance(3, 3);
        return formatter.format(date);
    }

    protected String extractProjectName(BuildEvent event) {
        Project project = event.getProject();
        return project != null ? project.getName() : null;
    }
}

