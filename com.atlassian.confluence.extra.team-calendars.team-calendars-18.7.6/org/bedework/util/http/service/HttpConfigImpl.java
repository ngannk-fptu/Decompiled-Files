/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.http.service;

import org.bedework.util.config.ConfInfo;
import org.bedework.util.config.ConfigBase;
import org.bedework.util.http.service.HttpConfig;

@ConfInfo(elementName="http-properties", type="org.bedework.util.http.service.HttpConfig")
public class HttpConfigImpl
extends ConfigBase<HttpConfigImpl>
implements HttpConfig {
    private int maxConnections;
    private int defaultMaxPerRoute;

    @Override
    public void setMaxConnections(int val) {
        this.maxConnections = val;
    }

    @Override
    public int getMaxConnections() {
        return this.maxConnections;
    }

    @Override
    public void setDefaultMaxPerRoute(int val) {
        this.defaultMaxPerRoute = val;
    }

    @Override
    public int getDefaultMaxPerRoute() {
        return this.defaultMaxPerRoute;
    }

    @Override
    public void disableSSL() {
    }
}

