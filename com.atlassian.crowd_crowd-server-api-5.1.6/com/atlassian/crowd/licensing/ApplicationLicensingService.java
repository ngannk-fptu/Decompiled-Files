/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ApplicationNotFoundException
 *  com.atlassian.crowd.exception.ObjectNotFoundException
 *  com.atlassian.crowd.model.ApplicationSubtype
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.fugue.Pair
 */
package com.atlassian.crowd.licensing;

import com.atlassian.crowd.exception.ApplicationNotFoundException;
import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.model.ApplicationSubtype;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.licensing.ApplicationLicensedDirectory;
import com.atlassian.crowd.model.licensing.ApplicationLicensedUser;
import com.atlassian.crowd.model.licensing.ApplicationLicensingSummary;
import com.atlassian.fugue.Pair;
import java.util.List;
import java.util.Optional;

public interface ApplicationLicensingService {
    public boolean canShowLicenseUsageForApplication(Long var1) throws ApplicationNotFoundException;

    public boolean isVersionUptoDate(Long var1, String var2, Long var3) throws ApplicationNotFoundException;

    public List<ApplicationSubtype> listJiraTypes(Long var1, Long var2) throws ApplicationNotFoundException;

    public List<ApplicationLicensedDirectory> listDirectories(Long var1, String var2, Long var3, int var4, int var5) throws ApplicationNotFoundException;

    public Pair<List<ApplicationLicensedUser>, Long> searchLicensedUsers(Long var1, String var2, String var3, String var4, Long var5, Long var6, int var7, int var8) throws ObjectNotFoundException;

    public Optional<ApplicationLicensingSummary> getLicensingSummary(Long var1, String var2, Long var3) throws ApplicationNotFoundException;

    public boolean updateApplicationData(Application var1);

    public void scheduleRefreshApplicationDataJobImmediately(long var1) throws ApplicationNotFoundException;

    public void clearAllJobs(Application var1);

    public boolean isLicensingConfigured(Long var1) throws ApplicationNotFoundException;
}

