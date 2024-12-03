/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.atlassian.crowd.exception.ApplicationNotFoundException
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.model.application.Application
 */
package com.atlassian.crowd.manager.permission;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.exception.ApplicationNotFoundException;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.model.application.Application;

public interface PermissionManager {
    public boolean hasPermission(Directory var1, OperationType var2);

    public boolean hasPermission(Application var1, Directory var2, OperationType var3);

    public void removePermission(Application var1, Directory var2, OperationType var3) throws ApplicationNotFoundException;

    public void addPermission(Application var1, Directory var2, OperationType var3) throws ApplicationNotFoundException;

    public void removePermission(Directory var1, OperationType var2) throws DirectoryNotFoundException;

    public void addPermission(Directory var1, OperationType var2) throws DirectoryNotFoundException;
}

