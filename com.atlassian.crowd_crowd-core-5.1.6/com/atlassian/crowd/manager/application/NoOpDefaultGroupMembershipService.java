/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.manager.application.DefaultGroupMembershipService
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ApplicationDirectoryMapping
 */
package com.atlassian.crowd.manager.application;

import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.application.DefaultGroupMembershipService;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ApplicationDirectoryMapping;
import java.util.Collections;
import java.util.List;

public class NoOpDefaultGroupMembershipService
implements DefaultGroupMembershipService {
    public void add(Application application, ApplicationDirectoryMapping directoryMapping, String groupName) throws OperationFailedException {
    }

    public void remove(Application application, ApplicationDirectoryMapping directoryMapping, String groupName) throws OperationFailedException {
    }

    public List<String> listAll(Application application, ApplicationDirectoryMapping directoryMapping) throws OperationFailedException {
        return Collections.emptyList();
    }
}

