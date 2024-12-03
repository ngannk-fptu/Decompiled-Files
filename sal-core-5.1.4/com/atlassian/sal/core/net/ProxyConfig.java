/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.core.net;

public interface ProxyConfig {
    public boolean isSet();

    public boolean requiresAuthentication();

    public String getHost();

    public int getPort();

    public String getUser();

    public String getPassword();

    public String[] getNonProxyHosts();
}

