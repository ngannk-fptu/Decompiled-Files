/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.ObjectLockRetention;
import java.io.Serializable;

public class GetObjectRetentionResult
implements Serializable {
    private ObjectLockRetention retention;

    public ObjectLockRetention getRetention() {
        return this.retention;
    }

    public GetObjectRetentionResult withRetention(ObjectLockRetention retention) {
        this.retention = retention;
        return this;
    }

    public void setRetention(ObjectLockRetention retention) {
        this.withRetention(retention);
    }
}

