/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.confluence.plugins.emailgateway.api.EmailHandlingException;

public class ReadOnlyModeHandleException
extends EmailHandlingException {
    public ReadOnlyModeHandleException(String message) {
        super(message);
    }
}

