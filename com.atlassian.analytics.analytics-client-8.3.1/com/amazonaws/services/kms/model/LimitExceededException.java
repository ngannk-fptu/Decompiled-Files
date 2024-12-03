/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.services.kms.model.AWSKMSException;

public class LimitExceededException
extends AWSKMSException {
    private static final long serialVersionUID = 1L;

    public LimitExceededException(String message) {
        super(message);
    }
}

