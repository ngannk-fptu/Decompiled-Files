/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dragonfly.api;

public class JiraIntegrationConfigurationException
extends Exception {
    public JiraIntegrationConfigurationException(String prettyUserMessage) {
        super(prettyUserMessage);
    }

    public JiraIntegrationConfigurationException(String prettyUserMessage, Throwable cause) {
        super(prettyUserMessage, cause);
    }
}

