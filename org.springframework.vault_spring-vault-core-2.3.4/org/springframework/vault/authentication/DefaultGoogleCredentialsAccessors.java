/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auth.oauth2.GoogleCredentials
 *  com.google.auth.oauth2.ServiceAccountCredentials
 *  org.springframework.util.Assert
 */
package org.springframework.vault.authentication;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.springframework.util.Assert;
import org.springframework.vault.authentication.GoogleCredentialsAccountIdAccessor;

enum DefaultGoogleCredentialsAccessors implements GoogleCredentialsAccountIdAccessor
{
    INSTANCE;


    @Override
    public String getServiceAccountId(GoogleCredentials credentials) {
        Assert.notNull((Object)credentials, (String)"GoogleCredentials must not be null");
        Assert.isInstanceOf(ServiceAccountCredentials.class, (Object)credentials, (String)"The configured GoogleCredentials does not represent a service account. Configure the service account id with GcpIamCredentialsAuthenticationOptionsBuilder#serviceAccountId(String).");
        return ((ServiceAccountCredentials)credentials).getAccount();
    }
}

