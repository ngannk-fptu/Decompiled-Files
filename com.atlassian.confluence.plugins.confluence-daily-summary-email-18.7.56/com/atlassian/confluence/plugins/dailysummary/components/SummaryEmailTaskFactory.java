/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.core.task.Task
 *  com.atlassian.user.User
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.dailysummary.components;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.core.task.Task;
import com.atlassian.user.User;
import java.util.Date;
import java.util.Optional;
import javax.annotation.Nullable;

public interface SummaryEmailTaskFactory {
    public Optional<Task> createEmailTask(User var1, Date var2);

    public Optional<Task> createEmailTask(User var1, Date var2, @Nullable Space var3);
}

