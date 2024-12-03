/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.components.net;

import org.apache.axis.AxisProperties;
import org.apache.axis.components.net.TransportClientProperties;

public class DefaultHTTPTransportClientProperties
implements TransportClientProperties {
    private static final String emptyString = "";
    protected String proxyHost = null;
    protected String nonProxyHosts = null;
    protected String proxyPort = null;
    protected String proxyUser = null;
    protected String proxyPassword = null;

    public String getProxyHost() {
        if (this.proxyHost == null) {
            this.proxyHost = AxisProperties.getProperty("http.proxyHost");
            if (this.proxyHost == null) {
                this.proxyHost = emptyString;
            }
        }
        return this.proxyHost;
    }

    public String getNonProxyHosts() {
        if (this.nonProxyHosts == null) {
            this.nonProxyHosts = AxisProperties.getProperty("http.nonProxyHosts");
            if (this.nonProxyHosts == null) {
                this.nonProxyHosts = emptyString;
            }
        }
        return this.nonProxyHosts;
    }

    public String getProxyPort() {
        if (this.proxyPort == null) {
            this.proxyPort = AxisProperties.getProperty("http.proxyPort");
            if (this.proxyPort == null) {
                this.proxyPort = emptyString;
            }
        }
        return this.proxyPort;
    }

    public String getProxyUser() {
        if (this.proxyUser == null) {
            this.proxyUser = AxisProperties.getProperty("http.proxyUser");
            if (this.proxyUser == null) {
                this.proxyUser = emptyString;
            }
        }
        return this.proxyUser;
    }

    public String getProxyPassword() {
        if (this.proxyPassword == null) {
            this.proxyPassword = AxisProperties.getProperty("http.proxyPassword");
            if (this.proxyPassword == null) {
                this.proxyPassword = emptyString;
            }
        }
        return this.proxyPassword;
    }
}

