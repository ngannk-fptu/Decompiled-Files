/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.search.ResourceType
 */
package com.atlassian.sal.core.search;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.search.ResourceType;

public class BasicResourceType
implements ResourceType {
    private String name;
    private String url;
    private String type;

    public BasicResourceType(ApplicationProperties applicationProperties, String type) {
        this.name = applicationProperties.getDisplayName();
        this.url = applicationProperties.getBaseUrl(UrlMode.AUTO);
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public String getUrl() {
        return this.url;
    }

    public String getType() {
        return this.type;
    }
}

