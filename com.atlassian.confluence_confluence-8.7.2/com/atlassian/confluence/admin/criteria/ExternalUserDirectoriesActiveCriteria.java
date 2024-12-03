/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.ApplicationFactory
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 */
package com.atlassian.confluence.admin.criteria;

import com.atlassian.confluence.admin.criteria.AdminConfigurationCriteria;
import com.atlassian.confluence.admin.criteria.DirectoryUtil;
import com.atlassian.crowd.embedded.api.ApplicationFactory;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;

public class ExternalUserDirectoriesActiveCriteria
implements AdminConfigurationCriteria {
    private final ApplicationFactory applicationFactory;

    public ExternalUserDirectoriesActiveCriteria(ApplicationFactory applicationFactory) {
        this.applicationFactory = applicationFactory;
    }

    @Override
    public boolean isMet() {
        return this.getValue() != null;
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public String getValue() {
        for (Directory directory : DirectoryUtil.getActiveDirectories(this.applicationFactory.getApplication())) {
            if (directory.getType() == DirectoryType.INTERNAL) continue;
            return directory.getName();
        }
        return null;
    }

    @Override
    public boolean hasLiveValue() {
        return true;
    }

    @Override
    public boolean getIgnored() {
        return false;
    }

    @Override
    public void setIgnored(boolean ignored) {
    }
}

