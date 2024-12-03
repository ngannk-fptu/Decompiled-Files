/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.internet.InternetAddress
 */
package com.atlassian.crowd.util.mail;

import java.util.Objects;
import javax.mail.internet.InternetAddress;

public class SMTPServer {
    private final String host;
    private final String username;
    private final String password;
    private final InternetAddress from;
    private final String prefix;
    private final String jndiLocation;
    private final int port;
    private final boolean jndiMailActive;
    private final boolean startTLS;
    private final boolean useSSL;
    private final int timeout;
    public static final int DEFAULT_MAIL_PORT = 25;
    public static final int DEFAULT_TIMEOUT = 60;

    @Deprecated
    public SMTPServer(String jndiLocation, InternetAddress from, String prefix) {
        this(SMTPServer.builder().setJndiMailActive(true).setStartTLS(false).setJndiLocation(jndiLocation).setFrom(from).setPrefix(prefix));
    }

    @Deprecated
    public SMTPServer(int port, String prefix, InternetAddress from, String password, String username, String host, boolean useSSL) {
        this(SMTPServer.builder().setJndiMailActive(false).setStartTLS(false).setPort(port).setPrefix(prefix).setFrom(from).setPassword(password).setUsername(username).setHost(host).setUseSSL(useSSL));
    }

    @Deprecated
    public SMTPServer() {
        this(SMTPServer.builder());
    }

    private SMTPServer(Builder builder) {
        this.host = builder.host;
        this.username = builder.username;
        this.password = builder.password;
        this.from = builder.from;
        this.prefix = builder.prefix;
        this.jndiLocation = builder.jndiLocation;
        this.port = builder.port;
        this.jndiMailActive = builder.jndiMailActive;
        this.useSSL = builder.useSSL;
        this.timeout = builder.timeout;
        this.startTLS = builder.startTLS;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(SMTPServer smtpServer) {
        return new Builder(smtpServer);
    }

    public String getHost() {
        return this.host;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean getUseSSL() {
        return this.useSSL;
    }

    public InternetAddress getFrom() {
        return this.from;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public int getPort() {
        return this.port;
    }

    public String getJndiLocation() {
        return this.jndiLocation;
    }

    public boolean isJndiMailActive() {
        return this.jndiMailActive;
    }

    public boolean isStartTLS() {
        return this.startTLS;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SMTPServer that = (SMTPServer)o;
        return this.port == that.port && this.jndiMailActive == that.jndiMailActive && this.startTLS == that.startTLS && this.useSSL == that.useSSL && this.timeout == that.timeout && Objects.equals(this.host, that.host) && Objects.equals(this.username, that.username) && Objects.equals(this.password, that.password) && Objects.equals(this.from, that.from) && Objects.equals(this.prefix, that.prefix) && Objects.equals(this.jndiLocation, that.jndiLocation);
    }

    public int hashCode() {
        return Objects.hash(this.host, this.username, this.password, this.from, this.prefix, this.jndiLocation, this.port, this.jndiMailActive, this.useSSL, this.timeout, this.startTLS);
    }

    public static final class Builder {
        private String host;
        private String username;
        private String password;
        private InternetAddress from;
        private String prefix;
        private String jndiLocation;
        private int port;
        private boolean jndiMailActive;
        private boolean startTLS;
        private boolean useSSL;
        private int timeout;

        private Builder() {
        }

        private Builder(SMTPServer smtpServer) {
            this.host = smtpServer.getHost();
            this.username = smtpServer.getUsername();
            this.password = smtpServer.getPassword();
            this.from = smtpServer.getFrom();
            this.prefix = smtpServer.getPrefix();
            this.jndiLocation = smtpServer.getJndiLocation();
            this.port = smtpServer.getPort();
            this.jndiMailActive = smtpServer.isJndiMailActive();
            this.startTLS = smtpServer.isStartTLS();
            this.useSSL = smtpServer.getUseSSL();
            this.timeout = smtpServer.getTimeout();
        }

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setFrom(InternetAddress from) {
            this.from = from;
            return this;
        }

        public Builder setPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public Builder setJndiLocation(String jndiLocation) {
            this.jndiLocation = jndiLocation;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setJndiMailActive(boolean jndiMailActive) {
            this.jndiMailActive = jndiMailActive;
            return this;
        }

        public Builder setUseSSL(boolean useSSL) {
            this.useSSL = useSSL;
            return this;
        }

        public Builder setTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder setStartTLS(boolean startTLS) {
            this.startTLS = startTLS;
            return this;
        }

        public SMTPServer build() {
            return new SMTPServer(this);
        }
    }
}

