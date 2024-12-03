/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ApplicationDirectoryMapping
 */
package com.atlassian.crowd.manager.application;

import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ApplicationDirectoryMapping;
import java.util.List;

public interface DefaultGroupMembershipService {
    public void add(Application var1, ApplicationDirectoryMapping var2, String var3) throws OperationFailedException;

    public void remove(Application var1, ApplicationDirectoryMapping var2, String var3) throws OperationFailedException;

    public List<String> listAll(Application var1, ApplicationDirectoryMapping var2) throws OperationFailedException;
}

