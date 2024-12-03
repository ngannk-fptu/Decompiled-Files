/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.util.TimeValue
 *  org.apache.hc.core5.util.Timeout
 */
package org.apache.hc.client5.http.config;

import java.util.concurrent.TimeUnit;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class ConnectionConfig
implements Cloneable {
    private static final Timeout DEFAULT_CONNECT_TIMEOUT = Timeout.ofMinutes((long)3L);
    public static final ConnectionConfig DEFAULT = new Builder().build();
    private final Timeout connectTimeout;
    private final Timeout socketTimeout;
    private final TimeValue validateAfterInactivity;
    private final TimeValue timeToLive;

    protected ConnectionConfig() {
        this(DEFAULT_CONNECT_TIMEOUT, null, null, null);
    }

    ConnectionConfig(Timeout connectTimeout, Timeout socketTimeout, TimeValue validateAfterInactivity, TimeValue timeToLive) {
        this.connectTimeout = connectTimeout;
        this.socketTimeout = socketTimeout;
        this.validateAfterInactivity = validateAfterInactivity;
        this.timeToLive = timeToLive;
    }

    public Timeout getSocketTimeout() {
        return this.socketTimeout;
    }

    public Timeout getConnectTimeout() {
        return this.connectTimeout;
    }

    public TimeValue getValidateAfterInactivity() {
        return this.validateAfterInactivity;
    }

    public TimeValue getTimeToLive() {
        return this.timeToLive;
    }

    protected ConnectionConfig clone() throws CloneNotSupportedException {
        return (ConnectionConfig)super.clone();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append("connectTimeout=").append(this.connectTimeout);
        builder.append(", socketTimeout=").append(this.socketTimeout);
        builder.append(", validateAfterInactivity=").append(this.validateAfterInactivity);
        builder.append(", timeToLive=").append(this.timeToLive);
        builder.append("]");
        return builder.toString();
    }

    public static Builder custom() {
        return new Builder();
    }

    public static Builder copy(ConnectionConfig config) {
        return new Builder().setConnectTimeout(config.getConnectTimeout()).setSocketTimeout(config.getSocketTimeout()).setValidateAfterInactivity(config.getValidateAfterInactivity()).setTimeToLive(config.getTimeToLive());
    }

    public static class Builder {
        private Timeout socketTimeout;
        private Timeout connectTimeout = ConnectionConfig.access$000();
        private TimeValue validateAfterInactivity;
        private TimeValue timeToLive;

        Builder() {
        }

        public Builder setSocketTimeout(int soTimeout, TimeUnit timeUnit) {
            this.socketTimeout = Timeout.of((long)soTimeout, (TimeUnit)timeUnit);
            return this;
        }

        public Builder setSocketTimeout(Timeout soTimeout) {
            this.socketTimeout = soTimeout;
            return this;
        }

        public Builder setConnectTimeout(Timeout connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setConnectTimeout(long connectTimeout, TimeUnit timeUnit) {
            this.connectTimeout = Timeout.of((long)connectTimeout, (TimeUnit)timeUnit);
            return this;
        }

        public Builder setValidateAfterInactivity(TimeValue validateAfterInactivity) {
            this.validateAfterInactivity = validateAfterInactivity;
            return this;
        }

        public Builder setValidateAfterInactivity(long validateAfterInactivity, TimeUnit timeUnit) {
            this.validateAfterInactivity = TimeValue.of((long)validateAfterInactivity, (TimeUnit)timeUnit);
            return this;
        }

        public Builder setTimeToLive(TimeValue timeToLive) {
            this.timeToLive = timeToLive;
            return this;
        }

        public Builder setTimeToLive(long timeToLive, TimeUnit timeUnit) {
            this.timeToLive = TimeValue.of((long)timeToLive, (TimeUnit)timeUnit);
            return this;
        }

        public ConnectionConfig build() {
            return new ConnectionConfig(this.connectTimeout != null ? this.connectTimeout : DEFAULT_CONNECT_TIMEOUT, this.socketTimeout, this.validateAfterInactivity, this.timeToLive);
        }
    }
}

