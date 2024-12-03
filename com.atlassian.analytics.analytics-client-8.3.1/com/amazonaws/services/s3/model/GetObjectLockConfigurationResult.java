/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.ObjectLockConfiguration;

public class GetObjectLockConfigurationResult {
    private ObjectLockConfiguration objectLockConfiguration;

    public ObjectLockConfiguration getObjectLockConfiguration() {
        return this.objectLockConfiguration;
    }

    public GetObjectLockConfigurationResult withObjectLockConfiguration(ObjectLockConfiguration objectLockConfiguration) {
        this.objectLockConfiguration = objectLockConfiguration;
        return this;
    }

    public void setObjectLockConfiguration(ObjectLockConfiguration objectLockConfiguration) {
        this.withObjectLockConfiguration(objectLockConfiguration);
    }
}

