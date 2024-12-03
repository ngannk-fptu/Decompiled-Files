/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.emailgateway.polling;

import com.atlassian.confluence.plugins.emailgateway.api.ReceivedEmail;
import com.atlassian.confluence.plugins.emailgateway.polling.EmailPollingException;
import java.util.Collection;

public interface EmailPoller {
    public boolean isAvailable();

    public Collection<ReceivedEmail> pollForIncomingEmails() throws EmailPollingException;
}

