/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.annotations.PublicApi;

@PublicApi
public class EmailStagingException
extends Exception {
    public EmailStagingException(String message, Throwable t) {
        super(message, t);
    }

    public EmailStagingException(String message) {
        super(message);
    }
}

