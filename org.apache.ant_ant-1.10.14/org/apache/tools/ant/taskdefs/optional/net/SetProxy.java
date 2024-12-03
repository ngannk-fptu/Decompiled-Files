/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.net;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class SetProxy
extends Task {
    private static final int HTTP_PORT = 80;
    private static final int SOCKS_PORT = 1080;
    protected String proxyHost = null;
    protected int proxyPort = 80;
    private String socksProxyHost = null;
    private int socksProxyPort = 1080;
    private String nonProxyHosts = null;
    private String proxyUser = null;
    private String proxyPassword = null;

    public void setProxyHost(String hostname) {
        this.proxyHost = hostname;
    }

    public void setProxyPort(int port) {
        this.proxyPort = port;
    }

    public void setSocksProxyHost(String host) {
        this.socksProxyHost = host;
    }

    public void setSocksProxyPort(int port) {
        this.socksProxyPort = port;
    }

    public void setNonProxyHosts(String nonProxyHosts) {
        this.nonProxyHosts = nonProxyHosts;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public void applyWebProxySettings() {
        boolean settingsChanged = false;
        boolean enablingProxy = false;
        Properties sysprops = System.getProperties();
        if (this.proxyHost != null) {
            settingsChanged = true;
            if (!this.proxyHost.isEmpty()) {
                this.traceSettingInfo();
                enablingProxy = true;
                sysprops.put("http.proxyHost", this.proxyHost);
                String portString = Integer.toString(this.proxyPort);
                sysprops.put("http.proxyPort", portString);
                sysprops.put("https.proxyHost", this.proxyHost);
                sysprops.put("https.proxyPort", portString);
                sysprops.put("ftp.proxyHost", this.proxyHost);
                sysprops.put("ftp.proxyPort", portString);
                if (this.nonProxyHosts != null) {
                    sysprops.put("http.nonProxyHosts", this.nonProxyHosts);
                    sysprops.put("https.nonProxyHosts", this.nonProxyHosts);
                    sysprops.put("ftp.nonProxyHosts", this.nonProxyHosts);
                }
                if (this.proxyUser != null) {
                    sysprops.put("http.proxyUser", this.proxyUser);
                    sysprops.put("http.proxyPassword", this.proxyPassword);
                }
            } else {
                this.log("resetting http proxy", 3);
                sysprops.remove("http.proxyHost");
                sysprops.remove("http.proxyPort");
                sysprops.remove("http.proxyUser");
                sysprops.remove("http.proxyPassword");
                sysprops.remove("https.proxyHost");
                sysprops.remove("https.proxyPort");
                sysprops.remove("ftp.proxyHost");
                sysprops.remove("ftp.proxyPort");
            }
        }
        if (this.socksProxyHost != null) {
            settingsChanged = true;
            if (!this.socksProxyHost.isEmpty()) {
                enablingProxy = true;
                sysprops.put("socksProxyHost", this.socksProxyHost);
                sysprops.put("socksProxyPort", Integer.toString(this.socksProxyPort));
                if (this.proxyUser != null) {
                    sysprops.put("java.net.socks.username", this.proxyUser);
                    sysprops.put("java.net.socks.password", this.proxyPassword);
                }
            } else {
                this.log("resetting socks proxy", 3);
                sysprops.remove("socksProxyHost");
                sysprops.remove("socksProxyPort");
                sysprops.remove("java.net.socks.username");
                sysprops.remove("java.net.socks.password");
            }
        }
        if (this.proxyUser != null) {
            if (enablingProxy) {
                Authenticator.setDefault(new ProxyAuth(this.proxyUser, this.proxyPassword));
            } else if (settingsChanged) {
                Authenticator.setDefault(new ProxyAuth("", ""));
            }
        }
    }

    private void traceSettingInfo() {
        this.log("Setting proxy to " + (this.proxyHost != null ? this.proxyHost : "''") + ":" + this.proxyPort, 3);
    }

    @Override
    public void execute() throws BuildException {
        this.applyWebProxySettings();
    }

    private static final class ProxyAuth
    extends Authenticator {
        private PasswordAuthentication auth;

        private ProxyAuth(String user, String pass) {
            this.auth = new PasswordAuthentication(user, pass.toCharArray());
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return this.auth;
        }
    }
}

