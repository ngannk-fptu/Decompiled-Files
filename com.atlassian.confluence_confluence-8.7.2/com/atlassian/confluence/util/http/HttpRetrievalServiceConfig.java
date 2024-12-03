/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.http;

import com.atlassian.confluence.util.http.HttpRequestConfig;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Deprecated(forRemoval=true)
public class HttpRetrievalServiceConfig
implements Serializable {
    private List configurations = new ArrayList();
    private HttpRequestConfig defaultConfiguration = new HttpRequestConfig(524288000, 1800000);

    public List getConfigurations() {
        return this.configurations;
    }

    public void setConfigurations(List configurations) {
        this.configurations = configurations;
    }

    public HttpRequestConfig getDefaultConfiguration() {
        return this.defaultConfiguration;
    }

    public void setDefaultConfiguration(HttpRequestConfig defaultConfiguration) {
        this.defaultConfiguration = defaultConfiguration;
    }
}

