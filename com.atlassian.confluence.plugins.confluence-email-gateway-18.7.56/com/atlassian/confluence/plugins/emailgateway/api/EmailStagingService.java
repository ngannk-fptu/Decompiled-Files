/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.plugins.emailgateway.api.EmailStagingException;
import com.atlassian.confluence.plugins.emailgateway.api.ReceivedEmail;
import com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThreadKey;

@PublicApi
public interface EmailStagingService {
    public StagedEmailThreadKey stageEmailThread(ReceivedEmail var1) throws EmailStagingException;
}

