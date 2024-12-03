/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.util.List;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.property.LocalProperties;

public class Sequential
extends Task
implements TaskContainer {
    private List<Task> nestedTasks = new Vector<Task>();

    @Override
    public void addTask(Task nestedTask) {
        this.nestedTasks.add(nestedTask);
    }

    @Override
    public void execute() throws BuildException {
        LocalProperties localProperties = LocalProperties.get(this.getProject());
        localProperties.enterScope();
        try {
            this.nestedTasks.forEach(Task::perform);
        }
        finally {
            localProperties.exitScope();
        }
    }
}

