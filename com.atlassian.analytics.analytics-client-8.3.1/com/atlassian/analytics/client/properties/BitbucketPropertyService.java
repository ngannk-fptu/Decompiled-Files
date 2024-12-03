/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 */
package com.atlassian.analytics.client.properties;

import com.atlassian.analytics.client.properties.DefaultPropertyService;
import com.atlassian.sal.api.ApplicationProperties;

public class BitbucketPropertyService
extends DefaultPropertyService {
    public BitbucketPropertyService(ApplicationProperties applicationProperties) {
        super(applicationProperties);
    }

    @Override
    public String getDisplayName() {
        return "Stash";
    }
}

