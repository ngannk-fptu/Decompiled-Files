/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.task;

import java.io.Serializable;

public interface Task
extends Serializable {
    public void execute() throws Exception;
}

