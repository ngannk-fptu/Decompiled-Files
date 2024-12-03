/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auth.oauth2.GoogleCredentials
 */
package org.springframework.vault.authentication;

import com.google.auth.oauth2.GoogleCredentials;

@FunctionalInterface
public interface GoogleCredentialsAccountIdAccessor {
    public String getServiceAccountId(GoogleCredentials var1);
}

