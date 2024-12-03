/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.services.kms.model.AWSKMSException;

public class CloudHsmClusterInUseException
extends AWSKMSException {
    private static final long serialVersionUID = 1L;

    public CloudHsmClusterInUseException(String message) {
        super(message);
    }
}

