/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.spi.DcLicenseChecker
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.model.application.Application
 */
package com.atlassian.crowd.manager.application.filtering;

import com.atlassian.crowd.embedded.spi.DcLicenseChecker;
import com.atlassian.crowd.manager.application.filtering.AccessFilter;
import com.atlassian.crowd.manager.application.filtering.AccessFilterFactory;
import com.atlassian.crowd.manager.application.filtering.BaseAccessFilter;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.model.application.Application;

public class AccessFilterFactoryImpl
implements AccessFilterFactory {
    private DirectoryManager directoryManager;
    private DcLicenseChecker dcLicenseChecker;

    public AccessFilterFactoryImpl(DirectoryManager directoryManager, DcLicenseChecker dcLicenseChecker) {
        this.directoryManager = directoryManager;
        this.dcLicenseChecker = dcLicenseChecker;
    }

    @Override
    public AccessFilter create(Application application, boolean queryForAllUsers) {
        if ((application.isFilteringUsersWithAccessEnabled() || application.isFilteringGroupsWithAccessEnabled()) && this.dcLicenseChecker.isDcLicense()) {
            return new BaseAccessFilter(this.directoryManager, application, queryForAllUsers);
        }
        return AccessFilter.UNFILTERED;
    }
}

