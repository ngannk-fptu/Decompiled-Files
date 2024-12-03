/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.license.service;

import com.atlassian.plugins.license.entity.LicenseTierInfoEntity;
import com.atlassian.plugins.license.exception.InvalidDataVersionException;
import com.atlassian.plugins.license.model.LicenseDataVersion;
import com.atlassian.plugins.license.model.LicensedUser;
import com.atlassian.plugins.license.model.UserDirectoryInformation;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LicenseUsageService {
    public List<LicensedUser> getLicensedUsers(Long var1, int var2, int var3) throws InvalidDataVersionException;

    public void saveDirectoryInfo(Collection<UserDirectoryInformation> var1);

    public List<UserDirectoryInformation> getDirectoryInfo(Long var1, int var2, int var3) throws InvalidDataVersionException;

    public void clearStaleLicenseDataIfAny();

    public void markVersionComplete(Long var1);

    public LicenseDataVersion createLicenseDataVersion();

    public Optional<LicenseDataVersion> getLatestValidVersion();

    public void saveLicensedUserInformation(LicensedUser var1);

    public void saveLicensedUsersInformation(Collection<? extends LicensedUser> var1);

    public List<LicenseTierInfoEntity> getLicenseTierInformation();

    public void clearLicenseData();
}

