/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.SubBuildListener;
import org.apache.tools.ant.util.FileUtils;

public class RecorderEntry
implements BuildLogger,
SubBuildListener {
    private String filename = null;
    private boolean record = true;
    private int loglevel = 2;
    private PrintStream out = null;
    private long targetStartTime = System.currentTimeMillis();
    private boolean emacsMode = false;
    private Project project;

    protected RecorderEntry(String name) {
        this.filename = name;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setRecordState(Boolean state) {
        if (state != null) {
            this.flush();
            this.record = state;
        }
    }

    @Override
    public void buildStarted(BuildEvent event) {
        this.log("> BUILD STARTED", 4);
    }

    @Override
    public void buildFinished(BuildEvent event) {
        this.log("< BUILD FINISHED", 4);
        if (this.record && this.out != null) {
            Throwable error = event.getException();
            if (error == null) {
                this.out.printf("%nBUILD SUCCESSFUL%n", new Object[0]);
            } else {
                this.out.printf("%nBUILD FAILED%n%n", new Object[0]);
                error.printStackTrace(this.out);
            }
        }
        this.cleanup();
    }

    @Override
    public void subBuildFinished(BuildEvent event) {
        if (event.getProject() == this.project) {
            this.cleanup();
        }
    }

    @Override
    public void subBuildStarted(BuildEvent event) {
    }

    @Override
    public void targetStarted(BuildEvent event) {
        this.log(">> TARGET STARTED -- " + event.getTarget(), 4);
        this.log(String.format("%n%s:", event.getTarget().getName()), 2);
        this.targetStartTime = System.currentTimeMillis();
    }

    @Override
    public void targetFinished(BuildEvent event) {
        this.log("<< TARGET FINISHED -- " + event.getTarget(), 4);
        String time = RecorderEntry.formatTime(System.currentTimeMillis() - this.targetStartTime);
        this.log(event.getTarget() + ":  duration " + time, 3);
        this.flush();
    }

    @Override
    public void taskStarted(BuildEvent event) {
        this.log(">>> TASK STARTED -- " + event.getTask(), 4);
    }

    @Override
    public void taskFinished(BuildEvent event) {
        this.log("<<< TASK FINISHED -- " + event.getTask(), 4);
        this.flush();
    }

    @Override
    public void messageLogged(BuildEvent event) {
        this.log("--- MESSAGE LOGGED", 4);
        StringBuilder buf = new StringBuilder();
        if (event.getTask() != null) {
            String name = event.getTask().getTaskName();
            if (!this.emacsMode) {
                String label = "[" + name + "] ";
                int size = 12 - label.length();
                for (int i = 0; i < size; ++i) {
                    buf.append(" ");
                }
                buf.append(label);
            }
        }
        buf.append(event.getMessage());
        this.log(buf.toString(), event.getPriority());
    }

    private void log(String mesg, int level) {
        if (this.record && level <= this.loglevel && this.out != null) {
            this.out.println(mesg);
        }
    }

    private void flush() {
        if (this.record && this.out != null) {
            this.out.flush();
        }
    }

    @Override
    public void setMessageOutputLevel(int level) {
        if (level >= 0 && level <= 4) {
            this.loglevel = level;
        }
    }

    @Override
    public int getMessageOutputLevel() {
        return this.loglevel;
    }

    @Override
    public void setOutputPrintStream(PrintStream output) {
        this.closeFile();
        this.out = output;
    }

    @Override
    public void setEmacsMode(boolean emacsMode) {
        this.emacsMode = emacsMode;
    }

    @Override
    public void setErrorPrintStream(PrintStream err) {
        this.setOutputPrintStream(err);
    }

    private static String formatTime(long millis) {
        long seconds = millis / 1000L;
        long minutes = seconds / 60L;
        if (minutes > 0L) {
            return minutes + " minute" + (minutes == 1L ? " " : "s ") + seconds % 60L + " second" + (seconds % 60L == 1L ? "" : "s");
        }
        return seconds + " second" + (seconds % 60L == 1L ? "" : "s");
    }

    public void setProject(Project project) {
        this.project = project;
        if (project != null) {
            project.addBuildListener(this);
        }
    }

    public Project getProject() {
        return this.project;
    }

    public void cleanup() {
        this.closeFile();
        if (this.project != null) {
            this.project.removeBuildListener(this);
        }
        this.project = null;
    }

    void openFile(boolean append) throws BuildException {
        this.openFileImpl(append);
    }

    void closeFile() {
        if (this.out != null) {
            this.out.close();
            this.out = null;
        }
    }

    void reopenFile() throws BuildException {
        this.openFileImpl(true);
    }

    private void openFileImpl(boolean append) throws BuildException {
        if (this.out == null) {
            try {
                this.out = new PrintStream(FileUtils.newOutputStream(Paths.get(this.filename, new String[0]), append));
            }
            catch (IOException ioe) {
                throw new BuildException("Problems opening file using a recorder entry", ioe);
            }
        }
    }
}

