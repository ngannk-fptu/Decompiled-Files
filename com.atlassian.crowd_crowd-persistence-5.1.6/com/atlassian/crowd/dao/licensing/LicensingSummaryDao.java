/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ObjectNotFoundException
 *  com.atlassian.crowd.model.ApplicationSubtype
 *  com.atlassian.crowd.model.application.Application
 */
package com.atlassian.crowd.dao.licensing;

import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.model.ApplicationSubtype;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.licensing.LicensingSummary;
import java.util.List;
import java.util.Optional;

public interface LicensingSummaryDao {
    public LicensingSummary findById(Long var1) throws ObjectNotFoundException;

    public Optional<LicensingSummary> getLicensingSummaryByVersion(Application var1, ApplicationSubtype var2, long var3);

    public Optional<LicensingSummary> findByApplication(Application var1, ApplicationSubtype var2);

    public Optional<LicensingSummary> getLatestLicensingSummary(Application var1);

    public List<LicensingSummary> findByApplication(Application var1, Long var2);

    public void saveAfterCleanup(LicensingSummary var1);

    public void activate(LicensingSummary var1);

    public void removeByApplicationId(Long var1);
}

