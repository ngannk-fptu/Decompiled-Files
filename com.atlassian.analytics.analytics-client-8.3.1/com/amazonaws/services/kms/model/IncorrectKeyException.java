/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.services.kms.model.AWSKMSException;

public class IncorrectKeyException
extends AWSKMSException {
    private static final long serialVersionUID = 1L;

    public IncorrectKeyException(String message) {
        super(message);
    }
}

