/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.spring.container.ContainerManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Required
 */
package com.atlassian.confluence.util;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.impl.user.RegisteredUsersCache;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.util.LicenseCalculator;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.spring.container.ContainerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class DefaultUserChecker
implements UserChecker {
    private static final Logger log = LoggerFactory.getLogger(DefaultUserChecker.class);
    @Deprecated
    public static final String CACHE_KEY = RegisteredUsersCache.getCacheName();
    private LicenseCalculator licenseCalculator;
    private LicenseService licenseService;
    private RegisteredUsersCache registeredUsersCache;

    @Required
    public void setLicenseCalculator(LicenseCalculator licenseCalculator) {
        this.licenseCalculator = licenseCalculator;
    }

    @Override
    public int getNumberOfRegisteredUsers() {
        return this.licenseCalculator.getNumberOfLicensedUsers();
    }

    @Override
    public boolean hasTooManyUsers() {
        if (!ContainerManager.isContainerSetup()) {
            return false;
        }
        ConfluenceLicense license = this.licenseService.retrieve();
        return !license.isUnlimitedNumberOfUsers() && this.getNumberOfRegisteredUsers() > this.licenseService.retrieve().getMaximumNumberOfUsers();
    }

    @Override
    public boolean isUnlimitedUserLicense() {
        return this.licenseService.retrieve().isUnlimitedNumberOfUsers();
    }

    @Deprecated
    public boolean isUnlimitedUserLicense(ConfluenceLicense license) {
        return license.isUnlimitedNumberOfUsers();
    }

    @Override
    public boolean isLicensedToAddMoreUsers() {
        boolean licensed;
        if (!ContainerManager.isContainerSetup()) {
            return false;
        }
        ConfluenceLicense license = this.licenseService.retrieve();
        if (license.isUnlimitedNumberOfUsers()) {
            return true;
        }
        int count = this.getNumberOfRegisteredUsers();
        if (count == -1) {
            log.warn("Attempt to check number of users before license checking has completed");
        }
        boolean bl = licensed = count < license.getMaximumNumberOfUsers();
        if (!licensed) {
            log.info("Not licensed to add more users. Registered users: " + this.getNumberOfRegisteredUsers() + " licensed users: " + license.getMaximumNumberOfUsers());
        }
        return licensed;
    }

    @Override
    public void resetResult() {
        try {
            this.registeredUsersCache.clear();
        }
        catch (Exception e) {
            log.error("Error resetting cache.", (Throwable)e);
        }
    }

    public boolean isRunning() {
        return this.licenseCalculator.isRunning();
    }

    @Override
    public void incrementRegisteredUserCount() {
        ConfluenceLicense license = this.licenseService.retrieve();
        if (license.isUnlimitedNumberOfUsers()) {
            return;
        }
        int numberOfRegisteredUsers = this.getNumberOfRegisteredUsers();
        if (numberOfRegisteredUsers > 0) {
            this.registeredUsersCache.setNumberOfRegisteredUsers(numberOfRegisteredUsers + 1);
        }
    }

    @Override
    public void decrementRegisteredUserCount() {
        ConfluenceLicense license = this.licenseService.retrieve();
        if (license.isUnlimitedNumberOfUsers()) {
            return;
        }
        int numberOfRegisteredUsers = this.getNumberOfRegisteredUsers();
        if (numberOfRegisteredUsers > 0) {
            this.registeredUsersCache.setNumberOfRegisteredUsers(numberOfRegisteredUsers - 1);
        }
    }

    @Required
    public void setLicenseService(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @Deprecated
    public void setCacheFactory(CacheFactory cacheFactory) {
    }

    @Required
    public void setRegisteredUsersCache(RegisteredUsersCache registeredUsersCache) {
        this.registeredUsersCache = registeredUsersCache;
    }
}

