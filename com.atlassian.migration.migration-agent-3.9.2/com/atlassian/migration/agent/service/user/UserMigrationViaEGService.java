/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.user;

import com.atlassian.migration.agent.service.catalogue.EnterpriseGatekeeperClient;
import com.atlassian.migration.agent.service.check.email.InvalidEmailCheckRequest;
import com.atlassian.migration.agent.service.user.EmailCheckStatusResponse;
import com.atlassian.migration.agent.service.user.LicenceCheckRequest;
import com.atlassian.migration.agent.service.user.LicenceCheckStatusResponse;
import lombok.Generated;

public class UserMigrationViaEGService {
    private final EnterpriseGatekeeperClient enterpriseGatekeeperClient;

    public String startEmailCheck(String cloudId, String migrationScopeId, InvalidEmailCheckRequest invalidEmailCheckRequest) {
        return this.enterpriseGatekeeperClient.startEmailCheck((String)cloudId, (String)migrationScopeId, (InvalidEmailCheckRequest)invalidEmailCheckRequest).taskId;
    }

    public EmailCheckStatusResponse getEmailCheckStatus(String cloudId, String migrationScopeId, String taskId) {
        return this.enterpriseGatekeeperClient.getInvalidEmailsCheckStatus(cloudId, migrationScopeId, taskId);
    }

    public String startLicenceCheck(String cloudId, String migrationScopeId, LicenceCheckRequest licenceCheckRequest) {
        return this.enterpriseGatekeeperClient.startLicenceCheck((String)cloudId, (String)migrationScopeId, (LicenceCheckRequest)licenceCheckRequest).taskId;
    }

    public LicenceCheckStatusResponse getLicenceCheckStatus(String cloudId, String migrationScopeId, String taskId) {
        return this.enterpriseGatekeeperClient.getLicenceCheckStatus(cloudId, migrationScopeId, taskId);
    }

    @Generated
    public UserMigrationViaEGService(EnterpriseGatekeeperClient enterpriseGatekeeperClient) {
        this.enterpriseGatekeeperClient = enterpriseGatekeeperClient;
    }
}

