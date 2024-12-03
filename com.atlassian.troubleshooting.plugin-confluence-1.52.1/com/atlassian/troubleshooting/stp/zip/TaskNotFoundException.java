/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp.zip;

import javax.annotation.Nonnull;

public class TaskNotFoundException
extends Exception {
    private static final long serialVersionUID = -3280772794810965284L;

    public TaskNotFoundException(@Nonnull String taskId) {
        super(String.format("No task found with an ID of '%s'", taskId));
    }
}

