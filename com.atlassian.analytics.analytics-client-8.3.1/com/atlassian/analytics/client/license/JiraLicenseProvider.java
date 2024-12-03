/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.jira.JiraLicense
 *  com.atlassian.jira.license.JiraLicenseManager
 *  com.atlassian.jira.license.LicenseDetails
 */
package com.atlassian.analytics.client.license;

import com.atlassian.analytics.client.license.LicenseProvider;
import com.atlassian.extras.api.jira.JiraLicense;
import com.atlassian.jira.license.JiraLicenseManager;
import com.atlassian.jira.license.LicenseDetails;
import java.util.Date;

public class JiraLicenseProvider
implements LicenseProvider {
    private final JiraLicenseManager jiraLicenseManager;

    public JiraLicenseProvider(JiraLicenseManager jiraLicenseManager) {
        this.jiraLicenseManager = jiraLicenseManager;
    }

    @Override
    public Date getLicenseCreationDate() {
        Date minCreationDate = null;
        for (LicenseDetails license : this.jiraLicenseManager.getLicenses()) {
            JiraLicense jiraLicense = license.getJiraLicense();
            if (jiraLicense == null) continue;
            Date creationDate = jiraLicense.getCreationDate();
            if (minCreationDate != null && creationDate.compareTo(minCreationDate) >= 0) continue;
            minCreationDate = creationDate;
        }
        return minCreationDate;
    }
}

