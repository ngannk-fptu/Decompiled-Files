/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.config;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.util.Timeout;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class TlsConfig
implements Cloneable {
    public static final TlsConfig DEFAULT = new Builder().build();
    private final Timeout handshakeTimeout;
    private final String[] supportedProtocols;
    private final String[] supportedCipherSuites;
    private final HttpVersionPolicy httpVersionPolicy;

    protected TlsConfig() {
        this(null, null, null, null);
    }

    TlsConfig(Timeout handshakeTimeout, String[] supportedProtocols, String[] supportedCipherSuites, HttpVersionPolicy httpVersionPolicy) {
        this.handshakeTimeout = handshakeTimeout;
        this.supportedProtocols = supportedProtocols;
        this.supportedCipherSuites = supportedCipherSuites;
        this.httpVersionPolicy = httpVersionPolicy;
    }

    public Timeout getHandshakeTimeout() {
        return this.handshakeTimeout;
    }

    public String[] getSupportedProtocols() {
        return this.supportedProtocols != null ? (String[])this.supportedProtocols.clone() : null;
    }

    public String[] getSupportedCipherSuites() {
        return this.supportedCipherSuites != null ? (String[])this.supportedCipherSuites.clone() : null;
    }

    public HttpVersionPolicy getHttpVersionPolicy() {
        return this.httpVersionPolicy;
    }

    protected TlsConfig clone() throws CloneNotSupportedException {
        return (TlsConfig)super.clone();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append("handshakeTimeout=").append(this.handshakeTimeout);
        builder.append(", supportedProtocols=").append(Arrays.toString(this.supportedProtocols));
        builder.append(", supportedCipherSuites=").append(Arrays.toString(this.supportedCipherSuites));
        builder.append(", httpVersionPolicy=").append((Object)this.httpVersionPolicy);
        builder.append("]");
        return builder.toString();
    }

    public static Builder custom() {
        return new Builder();
    }

    public static Builder copy(TlsConfig config) {
        return new Builder().setHandshakeTimeout(config.getHandshakeTimeout()).setSupportedProtocols(config.getSupportedProtocols()).setSupportedCipherSuites(config.getSupportedCipherSuites()).setVersionPolicy(config.getHttpVersionPolicy());
    }

    public static class Builder {
        private Timeout handshakeTimeout;
        private String[] supportedProtocols;
        private String[] supportedCipherSuites;
        private HttpVersionPolicy versionPolicy;

        public Builder setHandshakeTimeout(Timeout handshakeTimeout) {
            this.handshakeTimeout = handshakeTimeout;
            return this;
        }

        public Builder setHandshakeTimeout(long handshakeTimeout, TimeUnit timeUnit) {
            this.handshakeTimeout = Timeout.of(handshakeTimeout, timeUnit);
            return this;
        }

        public Builder setSupportedProtocols(String ... supportedProtocols) {
            this.supportedProtocols = supportedProtocols;
            return this;
        }

        public Builder setSupportedProtocols(TLS ... supportedProtocols) {
            this.supportedProtocols = new String[supportedProtocols.length];
            for (int i = 0; i < supportedProtocols.length; ++i) {
                TLS protocol = supportedProtocols[i];
                if (protocol == null) continue;
                this.supportedProtocols[i] = protocol.id;
            }
            return this;
        }

        public Builder setSupportedCipherSuites(String ... supportedCipherSuites) {
            this.supportedCipherSuites = supportedCipherSuites;
            return this;
        }

        public Builder setVersionPolicy(HttpVersionPolicy versionPolicy) {
            this.versionPolicy = versionPolicy;
            return this;
        }

        public TlsConfig build() {
            return new TlsConfig(this.handshakeTimeout, this.supportedProtocols, this.supportedCipherSuites, this.versionPolicy != null ? this.versionPolicy : HttpVersionPolicy.NEGOTIATE);
        }
    }
}

