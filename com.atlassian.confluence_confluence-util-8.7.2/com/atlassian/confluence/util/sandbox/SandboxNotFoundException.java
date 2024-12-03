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
public class SandboxNotFoundException
extends SandboxException {
    public SandboxNotFoundException(String message) {
        super(message);
    }

    public SandboxNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

