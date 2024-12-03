/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.components.net;

import org.apache.axis.AxisProperties;
import org.apache.axis.components.net.DefaultHTTPTransportClientProperties;

public class DefaultHTTPSTransportClientProperties
extends DefaultHTTPTransportClientProperties {
    public String getProxyHost() {
        if (this.proxyHost == null) {
            this.proxyHost = AxisProperties.getProperty("https.proxyHost");
            super.getProxyHost();
        }
        return this.proxyHost;
    }

    public String getNonProxyHosts() {
        if (this.nonProxyHosts == null) {
            this.nonProxyHosts = AxisProperties.getProperty("https.nonProxyHosts");
            super.getNonProxyHosts();
        }
        return this.nonProxyHosts;
    }

    public String getProxyPort() {
        if (this.proxyPort == null) {
            this.proxyPort = AxisProperties.getProperty("https.proxyPort");
            super.getProxyPort();
        }
        return this.proxyPort;
    }

    public String getProxyUser() {
        if (this.proxyUser == null) {
            this.proxyUser = AxisProperties.getProperty("https.proxyUser");
            super.getProxyUser();
        }
        return this.proxyUser;
    }

    public String getProxyPassword() {
        if (this.proxyPassword == null) {
            this.proxyPassword = AxisProperties.getProperty("https.proxyPassword");
            super.getProxyPassword();
        }
        return this.proxyPassword;
    }
}

