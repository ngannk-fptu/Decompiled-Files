/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 */
package com.atlassian.oauth2.provider.web;

import com.atlassian.sal.api.ApplicationProperties;

abstract class ServletConfiguration {
    static final String PLUGIN_SERVLET_PATH = "/plugins/servlet";
    final ApplicationProperties applicationProperties;

    protected ServletConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }
}

