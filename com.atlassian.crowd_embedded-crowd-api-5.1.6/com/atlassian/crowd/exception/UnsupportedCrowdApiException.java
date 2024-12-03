/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.OperationFailedException;

public class UnsupportedCrowdApiException
extends OperationFailedException {
    public UnsupportedCrowdApiException(String requiredVersion, String functionality) {
        super("Crowd REST API version " + requiredVersion + " or greater is required on the server " + functionality + ".");
    }
}

