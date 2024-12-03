/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.task;

import org.springframework.core.task.TaskRejectedException;

public class TaskTimeoutException
extends TaskRejectedException {
    public TaskTimeoutException(String msg) {
        super(msg);
    }

    public TaskTimeoutException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

