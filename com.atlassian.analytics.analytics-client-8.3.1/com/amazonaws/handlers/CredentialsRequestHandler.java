/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.handlers;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.handlers.RequestHandler2;

@Deprecated
public abstract class CredentialsRequestHandler
extends RequestHandler2 {
    protected AWSCredentials awsCredentials;

    public void setCredentials(AWSCredentials awsCredentials) {
        this.awsCredentials = awsCredentials;
    }
}

