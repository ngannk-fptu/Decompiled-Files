/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.api.client.googleapis.auth.oauth2.GoogleCredential
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.vault.authentication;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.vault.authentication.GcpProjectIdAccessor;
import org.springframework.vault.authentication.GcpServiceAccountIdAccessor;

enum DefaultGcpCredentialAccessors implements GcpProjectIdAccessor,
GcpServiceAccountIdAccessor
{
    INSTANCE;


    @Override
    public String getServiceAccountId(GoogleCredential credential) {
        Assert.notNull((Object)credential, (String)"GoogleCredential must not be null");
        Assert.notNull((Object)credential.getServiceAccountId(), (String)"The configured GoogleCredential does not represent a service account. Configure the service account id with GcpIamAuthenticationOptionsBuilder#serviceAccountId(String).");
        return credential.getServiceAccountId();
    }

    @Override
    public String getProjectId(GoogleCredential credential) {
        Assert.notNull((Object)credential, (String)"GoogleCredential must not be null");
        return StringUtils.isEmpty((Object)credential.getServiceAccountProjectId()) ? "-" : credential.getServiceAccountProjectId();
    }
}

