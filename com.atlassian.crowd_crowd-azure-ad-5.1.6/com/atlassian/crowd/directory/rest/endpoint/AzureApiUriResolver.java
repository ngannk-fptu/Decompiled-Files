/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.rest.endpoint;

public interface AzureApiUriResolver {
    public String getGraphApiUrl();

    public String getAuthorityApiUrl(String var1);

    default public String getScopeUrl() {
        return this.getGraphApiUrl() + "/.default";
    }
}

