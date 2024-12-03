/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.admin;

import com.atlassian.confluence.admin.tasks.AdminTask;
import com.atlassian.user.User;
import java.util.List;

public interface AdminTasklistManager {
    public List<AdminTask> getAllTasks();

    public AdminTask markTaskComplete(String var1);

    public AdminTask markTaskComplete(String var1, User var2);
}

