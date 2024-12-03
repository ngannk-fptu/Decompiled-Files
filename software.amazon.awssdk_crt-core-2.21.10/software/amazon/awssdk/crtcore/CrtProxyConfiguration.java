/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.ProxyConfigProvider
 *  software.amazon.awssdk.utils.StringUtils
 */
package software.amazon.awssdk.crtcore;

import java.util.Objects;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.ProxyConfigProvider;
import software.amazon.awssdk.utils.StringUtils;

@SdkPublicApi
public abstract class CrtProxyConfiguration {
    private final String scheme;
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final Boolean useSystemPropertyValues;
    private final Boolean useEnvironmentVariableValues;

    protected CrtProxyConfiguration(DefaultBuilder<?> builder) {
        this.useSystemPropertyValues = ((DefaultBuilder)builder).useSystemPropertyValues;
        this.useEnvironmentVariableValues = ((DefaultBuilder)builder).useEnvironmentVariableValues;
        this.scheme = ((DefaultBuilder)builder).scheme;
        ProxyConfigProvider proxyConfigProvider = ProxyConfigProvider.fromSystemEnvironmentSettings((Boolean)((DefaultBuilder)builder).useSystemPropertyValues, (Boolean)((DefaultBuilder)builder).useEnvironmentVariableValues, (String)((DefaultBuilder)builder).scheme);
        this.host = CrtProxyConfiguration.resolveHost(builder, proxyConfigProvider);
        this.port = CrtProxyConfiguration.resolvePort(builder, proxyConfigProvider);
        this.username = CrtProxyConfiguration.resolveUsername(builder, proxyConfigProvider);
        this.password = CrtProxyConfiguration.resolvePassword(builder, proxyConfigProvider);
    }

    private static String resolvePassword(DefaultBuilder<?> builder, ProxyConfigProvider proxyConfigProvider) {
        if (!StringUtils.isEmpty((CharSequence)((DefaultBuilder)builder).password) || proxyConfigProvider == null) {
            return ((DefaultBuilder)builder).password;
        }
        return proxyConfigProvider.password().orElseGet(() -> ((DefaultBuilder)builder).password);
    }

    private static String resolveUsername(DefaultBuilder<?> builder, ProxyConfigProvider proxyConfigProvider) {
        if (!StringUtils.isEmpty((CharSequence)((DefaultBuilder)builder).username) || proxyConfigProvider == null) {
            return ((DefaultBuilder)builder).username;
        }
        return proxyConfigProvider.userName().orElseGet(() -> ((DefaultBuilder)builder).username);
    }

    private static int resolvePort(DefaultBuilder<?> builder, ProxyConfigProvider proxyConfigProvider) {
        if (((DefaultBuilder)builder).port != 0 || proxyConfigProvider == null) {
            return ((DefaultBuilder)builder).port;
        }
        return proxyConfigProvider.port();
    }

    private static String resolveHost(DefaultBuilder<?> builder, ProxyConfigProvider proxyConfigProvider) {
        if (((DefaultBuilder)builder).host != null || proxyConfigProvider == null) {
            return ((DefaultBuilder)builder).host;
        }
        return proxyConfigProvider.host();
    }

    public final String scheme() {
        return this.scheme;
    }

    public final String host() {
        return this.host;
    }

    public final int port() {
        return this.port;
    }

    public final String username() {
        return this.username;
    }

    public final String password() {
        return this.password;
    }

    public final Boolean isUseEnvironmentVariableValues() {
        return this.useEnvironmentVariableValues;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CrtProxyConfiguration that = (CrtProxyConfiguration)o;
        if (this.port != that.port) {
            return false;
        }
        if (!Objects.equals(this.scheme, that.scheme)) {
            return false;
        }
        if (!Objects.equals(this.host, that.host)) {
            return false;
        }
        if (!Objects.equals(this.username, that.username)) {
            return false;
        }
        if (!Objects.equals(this.password, that.password)) {
            return false;
        }
        if (!Objects.equals(this.useSystemPropertyValues, that.useSystemPropertyValues)) {
            return false;
        }
        return Objects.equals(this.useEnvironmentVariableValues, that.useEnvironmentVariableValues);
    }

    public int hashCode() {
        int result = this.scheme != null ? this.scheme.hashCode() : 0;
        result = 31 * result + (this.host != null ? this.host.hashCode() : 0);
        result = 31 * result + this.port;
        result = 31 * result + (this.username != null ? this.username.hashCode() : 0);
        result = 31 * result + (this.password != null ? this.password.hashCode() : 0);
        result = 31 * result + (this.useSystemPropertyValues != null ? this.useSystemPropertyValues.hashCode() : 0);
        result = 31 * result + (this.useEnvironmentVariableValues != null ? this.useEnvironmentVariableValues.hashCode() : 0);
        result = 31 * result + (this.scheme != null ? this.scheme.hashCode() : 0);
        return result;
    }

    protected static abstract class DefaultBuilder<B extends Builder>
    implements Builder {
        private String scheme;
        private String host;
        private int port = 0;
        private String username;
        private String password;
        private Boolean useSystemPropertyValues = Boolean.TRUE;
        private Boolean useEnvironmentVariableValues = Boolean.TRUE;

        protected DefaultBuilder() {
        }

        protected DefaultBuilder(CrtProxyConfiguration proxyConfiguration) {
            this.useSystemPropertyValues = proxyConfiguration.useSystemPropertyValues;
            this.useEnvironmentVariableValues = proxyConfiguration.useEnvironmentVariableValues;
            this.scheme = proxyConfiguration.scheme;
            this.host = proxyConfiguration.host;
            this.port = proxyConfiguration.port;
            this.username = proxyConfiguration.username;
            this.password = proxyConfiguration.password;
        }

        public B scheme(String scheme) {
            this.scheme = scheme;
            return (B)this;
        }

        public B host(String host) {
            this.host = host;
            return (B)this;
        }

        public B port(int port) {
            this.port = port;
            return (B)this;
        }

        public B username(String username) {
            this.username = username;
            return (B)this;
        }

        public B password(String password) {
            this.password = password;
            return (B)this;
        }

        public B useSystemPropertyValues(Boolean useSystemPropertyValues) {
            this.useSystemPropertyValues = useSystemPropertyValues;
            return (B)this;
        }

        public B useEnvironmentVariableValues(Boolean useEnvironmentVariableValues) {
            this.useEnvironmentVariableValues = useEnvironmentVariableValues;
            return (B)this;
        }

        public B setuseEnvironmentVariableValues(Boolean useEnvironmentVariableValues) {
            return this.useEnvironmentVariableValues(useEnvironmentVariableValues);
        }

        public void setUseSystemPropertyValues(Boolean useSystemPropertyValues) {
            this.useSystemPropertyValues(useSystemPropertyValues);
        }
    }

    public static interface Builder {
        public Builder host(String var1);

        public Builder port(int var1);

        public Builder scheme(String var1);

        public Builder username(String var1);

        public Builder password(String var1);

        public Builder useSystemPropertyValues(Boolean var1);

        public Builder useEnvironmentVariableValues(Boolean var1);

        public CrtProxyConfiguration build();
    }
}

