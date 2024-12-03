/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.sso;

public class ApplicationSamlConfigurationNotFoundException
extends RuntimeException {
    private final String assertionConsumerUrl;
    private final String audience;

    public ApplicationSamlConfigurationNotFoundException(String audience, String assertionConsumerUrl) {
        super(String.format("Could not find configuration for application with Assertion Consumer Service URL %s and Issuer %s", assertionConsumerUrl, audience));
        this.assertionConsumerUrl = assertionConsumerUrl;
        this.audience = audience;
    }

    public String getAssertionConsumerUrl() {
        return this.assertionConsumerUrl;
    }

    public String getAudience() {
        return this.audience;
    }
}

