/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.secrets.SecretStoreType
 */
package com.atlassian.confluence.status.service.systeminfo;

import com.atlassian.secrets.SecretStoreType;

public class SecurityInfo {
    private String secretStoreClass;

    public String getSecretStoreClass() {
        return this.secretStoreClass;
    }

    public void setSecretStoreClass(String secretStoreClass) {
        this.secretStoreClass = secretStoreClass;
    }

    public SecretStoreType getSecretStoreType() {
        return SecretStoreType.of((String)this.secretStoreClass);
    }
}

