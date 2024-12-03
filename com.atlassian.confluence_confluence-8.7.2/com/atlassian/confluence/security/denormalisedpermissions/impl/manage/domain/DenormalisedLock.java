/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.manage.domain;

import com.atlassian.confluence.core.NotExportable;

public class DenormalisedLock
implements NotExportable {
    private String lockName;

    public String getLockName() {
        return this.lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }
}

