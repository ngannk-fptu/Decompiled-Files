/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class TaskConfigurationChecker {
    private List<String> errors = new ArrayList<String>();
    private final Task task;

    public TaskConfigurationChecker(Task task) {
        this.task = task;
    }

    public void assertConfig(boolean condition, String errormessage) {
        if (!condition) {
            this.errors.add(errormessage);
        }
    }

    public void fail(String errormessage) {
        this.errors.add(errormessage);
    }

    public void checkErrors() throws BuildException {
        if (!this.errors.isEmpty()) {
            StringBuilder sb = new StringBuilder(String.format("Configuration error on <%s>:%n", this.task.getTaskName()));
            for (String msg : this.errors) {
                sb.append(String.format("- %s%n", msg));
            }
            throw new BuildException(sb.toString(), this.task.getLocation());
        }
    }
}

