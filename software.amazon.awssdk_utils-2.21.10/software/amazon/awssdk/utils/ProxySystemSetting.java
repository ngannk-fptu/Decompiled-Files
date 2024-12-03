/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.SystemSetting;

@SdkProtectedApi
public enum ProxySystemSetting implements SystemSetting
{
    PROXY_HOST("http.proxyHost"),
    PROXY_PORT("http.proxyPort"),
    NON_PROXY_HOSTS("http.nonProxyHosts"),
    PROXY_USERNAME("http.proxyUser"),
    PROXY_PASSWORD("http.proxyPassword"),
    HTTPS_PROXY_HOST("https.proxyHost"),
    HTTPS_PROXY_PORT("https.proxyPort"),
    HTTPS_PROXY_USERNAME("https.proxyUser"),
    HTTPS_PROXY_PASSWORD("https.proxyPassword");

    private final String systemProperty;

    private ProxySystemSetting(String systemProperty) {
        this.systemProperty = systemProperty;
    }

    @Override
    public String property() {
        return this.systemProperty;
    }

    @Override
    public String environmentVariable() {
        return null;
    }

    @Override
    public String defaultValue() {
        return null;
    }
}

