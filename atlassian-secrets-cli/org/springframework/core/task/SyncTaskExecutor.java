/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.task;

import java.io.Serializable;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.Assert;

public class SyncTaskExecutor
implements TaskExecutor,
Serializable {
    @Override
    public void execute(Runnable task) {
        Assert.notNull((Object)task, "Runnable must not be null");
        task.run();
    }
}

