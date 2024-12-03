/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import java.util.EventObject;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

public class BuildEvent
extends EventObject {
    private static final long serialVersionUID = 4538050075952288486L;
    private final Project project;
    private final Target target;
    private final Task task;
    private String message;
    private int priority = 3;
    private Throwable exception;

    public BuildEvent(Project project) {
        super(project);
        this.project = project;
        this.target = null;
        this.task = null;
    }

    public BuildEvent(Target target) {
        super(target);
        this.project = target.getProject();
        this.target = target;
        this.task = null;
    }

    public BuildEvent(Task task) {
        super(task);
        this.project = task.getProject();
        this.target = task.getOwningTarget();
        this.task = task;
    }

    public void setMessage(String message, int priority) {
        this.message = message;
        this.priority = priority;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public Project getProject() {
        return this.project;
    }

    public Target getTarget() {
        return this.target;
    }

    public Task getTask() {
        return this.task;
    }

    public String getMessage() {
        return this.message;
    }

    public int getPriority() {
        return this.priority;
    }

    public Throwable getException() {
        return this.exception;
    }
}

