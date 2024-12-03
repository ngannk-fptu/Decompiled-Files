/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.util.Hashtable;
import java.util.Map;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.SubBuildListener;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.RecorderEntry;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.LogLevel;

public class Recorder
extends Task
implements SubBuildListener {
    private String filename = null;
    private Boolean append = null;
    private Boolean start = null;
    private int loglevel = -1;
    private boolean emacsMode = false;
    private static Map<String, RecorderEntry> recorderEntries = new Hashtable<String, RecorderEntry>();

    @Override
    public void init() {
        this.getProject().addBuildListener(this);
    }

    public void setName(String fname) {
        this.filename = fname;
    }

    public void setAction(ActionChoices action) {
        this.start = action.getValue().equalsIgnoreCase("start") ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setAppend(boolean append) {
        this.append = append ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setEmacsMode(boolean emacsMode) {
        this.emacsMode = emacsMode;
    }

    public void setLoglevel(VerbosityLevelChoices level) {
        this.loglevel = level.getLevel();
    }

    public void setLogLevel(LogLevel level) {
        this.loglevel = level.getLevel();
    }

    @Override
    public void execute() throws BuildException {
        if (this.filename == null) {
            throw new BuildException("No filename specified");
        }
        this.getProject().log("setting a recorder for name " + this.filename, 4);
        RecorderEntry recorder = this.getRecorder(this.filename, this.getProject());
        recorder.setMessageOutputLevel(this.loglevel);
        recorder.setEmacsMode(this.emacsMode);
        if (this.start != null) {
            if (this.start.booleanValue()) {
                recorder.reopenFile();
                recorder.setRecordState(this.start);
            } else {
                recorder.setRecordState(this.start);
                recorder.closeFile();
            }
        }
    }

    protected RecorderEntry getRecorder(String name, Project proj) throws BuildException {
        RecorderEntry entry = recorderEntries.get(name);
        if (entry == null) {
            entry = new RecorderEntry(name);
            if (this.append == null) {
                entry.openFile(false);
            } else {
                entry.openFile(this.append);
            }
            entry.setProject(proj);
            recorderEntries.put(name, entry);
        }
        return entry;
    }

    @Override
    public void buildStarted(BuildEvent event) {
    }

    @Override
    public void subBuildStarted(BuildEvent event) {
    }

    @Override
    public void targetStarted(BuildEvent event) {
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
    }

    @Override
    public void buildFinished(BuildEvent event) {
        this.cleanup();
    }

    @Override
    public void subBuildFinished(BuildEvent event) {
        if (event.getProject() == this.getProject()) {
            this.cleanup();
        }
    }

    private void cleanup() {
        recorderEntries.entrySet().removeIf(e -> ((RecorderEntry)e.getValue()).getProject() == this.getProject());
        this.getProject().removeBuildListener(this);
    }

    public static class ActionChoices
    extends EnumeratedAttribute {
        private static final String[] VALUES = new String[]{"start", "stop"};

        @Override
        public String[] getValues() {
            return VALUES;
        }
    }

    public static class VerbosityLevelChoices
    extends LogLevel {
    }
}

