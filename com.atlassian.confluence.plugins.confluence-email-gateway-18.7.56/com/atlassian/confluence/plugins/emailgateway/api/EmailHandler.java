/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.annotations.PublicSpi;
import com.atlassian.confluence.plugins.emailgateway.api.EmailHandlingException;
import com.atlassian.confluence.plugins.emailgateway.api.ReceivedEmail;

@PublicSpi
public interface EmailHandler {
    public boolean handle(ReceivedEmail var1) throws EmailHandlingException;
}

