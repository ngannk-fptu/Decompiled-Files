/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.plugins.emailgateway.api.EmailStagingException;

@PublicApi
public class EmailHandlingException
extends RuntimeException {
    public EmailHandlingException(String message) {
        super(message);
    }

    public EmailHandlingException(EmailStagingException e) {
        super(e);
    }
}

