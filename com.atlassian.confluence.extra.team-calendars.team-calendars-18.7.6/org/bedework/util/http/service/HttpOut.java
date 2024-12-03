/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.pool.PoolStats
 */
package org.bedework.util.http.service;

import org.apache.http.pool.PoolStats;
import org.bedework.util.http.BasicHttpClient;
import org.bedework.util.http.service.HttpConfigImpl;
import org.bedework.util.http.service.HttpOutMBean;
import org.bedework.util.jmx.ConfBase;

public class HttpOut
extends ConfBase<HttpConfigImpl>
implements HttpOutMBean {
    public HttpOut(String confuriPname, String domain, String serviceName) {
        this.setConfigName(serviceName);
        this.setConfigPname(confuriPname);
        this.setServiceName(domain + ":service=" + serviceName);
    }

    @Override
    public void setMaxConnections(int val) {
        ((HttpConfigImpl)this.getConfig()).setMaxConnections(val);
        BasicHttpClient.setMaxConnections(val);
    }

    @Override
    public int getMaxConnections() {
        return BasicHttpClient.getMaxConnections();
    }

    @Override
    public void setDefaultMaxPerRoute(int val) {
        ((HttpConfigImpl)this.getConfig()).setDefaultMaxPerRoute(val);
        BasicHttpClient.setDefaultMaxPerRoute(val);
    }

    @Override
    public int getDefaultMaxPerRoute() {
        return BasicHttpClient.getDefaultMaxPerRoute();
    }

    @Override
    public void disableSSL() {
    }

    @Override
    public PoolStats getConnStats() {
        return BasicHttpClient.getConnStats();
    }

    @Override
    public String loadConfig() {
        String res = this.loadConfig(HttpConfigImpl.class);
        this.refresh();
        return res;
    }

    private void refresh() {
        BasicHttpClient.setMaxConnections(((HttpConfigImpl)this.getConfig()).getMaxConnections());
        BasicHttpClient.setDefaultMaxPerRoute(((HttpConfigImpl)this.getConfig()).getDefaultMaxPerRoute());
    }
}

