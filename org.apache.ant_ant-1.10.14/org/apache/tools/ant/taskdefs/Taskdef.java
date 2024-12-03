/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskAdapter;
import org.apache.tools.ant.taskdefs.Typedef;

public class Taskdef
extends Typedef {
    public Taskdef() {
        this.setAdapterClass(TaskAdapter.class);
        this.setAdaptToClass(Task.class);
    }
}

