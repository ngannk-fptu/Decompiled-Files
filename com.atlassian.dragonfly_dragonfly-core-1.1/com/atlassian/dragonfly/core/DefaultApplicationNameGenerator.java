/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dragonfly.api.ApplicationNameGenerator
 */
package com.atlassian.dragonfly.core;

import com.atlassian.dragonfly.api.ApplicationNameGenerator;

public class DefaultApplicationNameGenerator
implements ApplicationNameGenerator {
    private final String baseUrl;
    private final String id;
    private final String applicationType;

    public DefaultApplicationNameGenerator(String baseUrl, String id, String applicationType) {
        this.baseUrl = baseUrl;
        this.id = id;
        this.applicationType = applicationType;
    }

    public String generateApplicationName() {
        return this.applicationType + " - " + this.baseUrl + " - " + this.id;
    }
}

