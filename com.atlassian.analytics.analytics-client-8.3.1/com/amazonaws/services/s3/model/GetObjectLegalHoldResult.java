/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.ObjectLockLegalHold;
import java.io.Serializable;

public class GetObjectLegalHoldResult
implements Serializable {
    private ObjectLockLegalHold legalHold;

    public ObjectLockLegalHold getLegalHold() {
        return this.legalHold;
    }

    public GetObjectLegalHoldResult withLegalHold(ObjectLockLegalHold legalHold) {
        this.legalHold = legalHold;
        return this;
    }

    public void setLegalHold(ObjectLockLegalHold legalHold) {
        this.withLegalHold(legalHold);
    }
}

