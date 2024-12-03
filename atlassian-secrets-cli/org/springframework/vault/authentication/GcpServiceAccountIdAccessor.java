/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.api.client.googleapis.auth.oauth2.GoogleCredential
 */
package org.springframework.vault.authentication;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

@FunctionalInterface
public interface GcpServiceAccountIdAccessor {
    public String getServiceAccountId(GoogleCredential var1);
}

