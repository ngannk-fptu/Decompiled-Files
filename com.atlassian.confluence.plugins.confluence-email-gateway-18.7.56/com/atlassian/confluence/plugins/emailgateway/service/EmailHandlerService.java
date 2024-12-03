/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.emailgateway.service;

import com.atlassian.confluence.plugins.emailgateway.api.EmailHandlingException;
import com.atlassian.confluence.plugins.emailgateway.api.ReceivedEmail;

public interface EmailHandlerService {
    public void handle(ReceivedEmail var1) throws EmailHandlingException;
}

