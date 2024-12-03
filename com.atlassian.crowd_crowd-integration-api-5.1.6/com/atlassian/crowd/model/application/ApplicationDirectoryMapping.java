/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.OperationType
 */
package com.atlassian.crowd.model.application;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.OperationType;
import java.util.Set;

public interface ApplicationDirectoryMapping {
    public Directory getDirectory();

    public boolean isAllowAllToAuthenticate();

    public Set<String> getAuthorisedGroupNames();

    public Set<OperationType> getAllowedOperations();
}

