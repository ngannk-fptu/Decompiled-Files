/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.util.sandbox;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.util.sandbox.SandboxException;

@ExperimentalApi
public class SandboxTimeoutException
extends SandboxException {
    public SandboxTimeoutException(String message) {
        super(message);
    }

    public SandboxTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}

