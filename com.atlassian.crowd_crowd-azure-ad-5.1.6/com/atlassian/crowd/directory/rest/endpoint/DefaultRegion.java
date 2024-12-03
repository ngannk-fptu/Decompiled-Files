/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.rest.endpoint;

public enum DefaultRegion {
    GLOBAL("https://graph.microsoft.com", "https://login.windows.net"),
    GERMANY("https://graph.microsoft.de", "https://login.microsoftonline.de"),
    CHINA("https://microsoftgraph.chinacloudapi.cn", "https://login.chinacloudapi.cn"),
    UNITED_STATES_GOVERNMENT("https://graph.windows.net/", "https://login.microsoftonline.us");

    private final String graphEndpoint;
    private final String authorityEndpoint;

    private DefaultRegion(String graphEndpoint, String authorityEndpoint) {
        this.graphEndpoint = graphEndpoint;
        this.authorityEndpoint = authorityEndpoint;
    }

    public String getGraphApiUrl() {
        return this.graphEndpoint;
    }

    public String getBasicAuthorityApiUrl() {
        return this.authorityEndpoint;
    }
}

