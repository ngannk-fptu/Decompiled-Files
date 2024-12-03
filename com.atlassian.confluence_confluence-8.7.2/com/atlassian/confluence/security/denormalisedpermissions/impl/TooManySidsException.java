/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl;

import com.atlassian.confluence.security.denormalisedpermissions.impl.RequestCannotBeProcessedByFastPermissionsException;

public class TooManySidsException
extends RequestCannotBeProcessedByFastPermissionsException {
    private final int numberOfSids;

    public TooManySidsException(String message, int numberOfSids) {
        super(message);
        this.numberOfSids = numberOfSids;
    }

    public int getNumberOfSids() {
        return this.numberOfSids;
    }
}

