/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.ProxyConfigProvider;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class ProxyConfiguration
implements ToCopyableBuilder<Builder, ProxyConfiguration> {
    private final Boolean useSystemPropertyValues;
    private final Boolean useEnvironmentVariablesValues;
    private final String scheme;
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final Set<String> nonProxyHosts;

    private ProxyConfiguration(BuilderImpl builder) {
        this.useSystemPropertyValues = builder.useSystemPropertyValues;
        this.useEnvironmentVariablesValues = builder.useEnvironmentVariablesValues;
        this.scheme = builder.scheme;
        ProxyConfigProvider proxyConfigProvider = ProxyConfigProvider.fromSystemEnvironmentSettings(builder.useSystemPropertyValues, builder.useEnvironmentVariablesValues, builder.scheme);
        this.host = ProxyConfiguration.resolveHost(builder, proxyConfigProvider);
        this.port = ProxyConfiguration.resolvePort(builder, proxyConfigProvider);
        this.username = ProxyConfiguration.resolveUserName(builder, proxyConfigProvider);
        this.password = ProxyConfiguration.resolvePassword(builder, proxyConfigProvider);
        this.nonProxyHosts = ProxyConfiguration.resolveNonProxyHosts(builder, proxyConfigProvider);
    }

    private static Set<String> resolveNonProxyHosts(BuilderImpl builder, ProxyConfigProvider proxyConfigProvider) {
        if (builder.nonProxyHosts != null || proxyConfigProvider == null) {
            return builder.nonProxyHosts;
        }
        return proxyConfigProvider.nonProxyHosts();
    }

    private static String resolvePassword(BuilderImpl builder, ProxyConfigProvider proxyConfigProvider) {
        if (!StringUtils.isEmpty(builder.password) || proxyConfigProvider == null) {
            return builder.password;
        }
        return proxyConfigProvider.password().orElseGet(() -> builder.password);
    }

    private static String resolveUserName(BuilderImpl builder, ProxyConfigProvider proxyConfigProvider) {
        if (!StringUtils.isEmpty(builder.username) || proxyConfigProvider == null) {
            return builder.username;
        }
        return proxyConfigProvider.userName().orElseGet(() -> builder.username);
    }

    private static int resolvePort(BuilderImpl builder, ProxyConfigProvider proxyConfigProvider) {
        if (builder.port != 0 || proxyConfigProvider == null) {
            return builder.port;
        }
        return proxyConfigProvider.port();
    }

    private static String resolveHost(BuilderImpl builder, ProxyConfigProvider proxyConfigProvider) {
        if (builder.host != null || proxyConfigProvider == null) {
            return builder.host;
        }
        return proxyConfigProvider.host();
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public String scheme() {
        return this.scheme;
    }

    public String host() {
        return this.host;
    }

    public int port() {
        return this.port;
    }

    public String username() {
        return this.username;
    }

    public String password() {
        return this.password;
    }

    public Set<String> nonProxyHosts() {
        return Collections.unmodifiableSet(this.nonProxyHosts != null ? this.nonProxyHosts : Collections.emptySet());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ProxyConfiguration that = (ProxyConfiguration)o;
        if (this.port != that.port) {
            return false;
        }
        if (this.scheme != null ? !this.scheme.equals(that.scheme) : that.scheme != null) {
            return false;
        }
        if (this.host != null ? !this.host.equals(that.host) : that.host != null) {
            return false;
        }
        if (this.username != null ? !this.username.equals(that.username) : that.username != null) {
            return false;
        }
        if (this.password != null ? !this.password.equals(that.password) : that.password != null) {
            return false;
        }
        return this.nonProxyHosts.equals(that.nonProxyHosts);
    }

    public int hashCode() {
        int result = this.scheme != null ? this.scheme.hashCode() : 0;
        result = 31 * result + (this.host != null ? this.host.hashCode() : 0);
        result = 31 * result + this.port;
        result = 31 * result + this.nonProxyHosts.hashCode();
        result = 31 * result + (this.username != null ? this.username.hashCode() : 0);
        result = 31 * result + (this.password != null ? this.password.hashCode() : 0);
        return result;
    }

    @Override
    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    private static final class BuilderImpl
    implements Builder {
        private String scheme = "http";
        private String host;
        private int port = 0;
        private String username;
        private String password;
        private Set<String> nonProxyHosts;
        private Boolean useSystemPropertyValues = Boolean.TRUE;
        private Boolean useEnvironmentVariablesValues = Boolean.TRUE;

        private BuilderImpl() {
        }

        private BuilderImpl(ProxyConfiguration proxyConfiguration) {
            this.useSystemPropertyValues = proxyConfiguration.useSystemPropertyValues;
            this.useEnvironmentVariablesValues = proxyConfiguration.useEnvironmentVariablesValues;
            this.scheme = proxyConfiguration.scheme;
            this.host = proxyConfiguration.host;
            this.port = proxyConfiguration.port;
            this.nonProxyHosts = proxyConfiguration.nonProxyHosts != null ? new HashSet(proxyConfiguration.nonProxyHosts) : null;
            this.username = proxyConfiguration.username;
            this.password = proxyConfiguration.password;
        }

        @Override
        public Builder scheme(String scheme) {
            this.scheme = scheme;
            return this;
        }

        @Override
        public Builder host(String host) {
            this.host = host;
            return this;
        }

        @Override
        public Builder port(int port) {
            this.port = port;
            return this;
        }

        @Override
        public Builder nonProxyHosts(Set<String> nonProxyHosts) {
            this.nonProxyHosts = nonProxyHosts != null ? new HashSet<String>(nonProxyHosts) : Collections.emptySet();
            return this;
        }

        @Override
        public Builder username(String username) {
            this.username = username;
            return this;
        }

        @Override
        public Builder password(String password) {
            this.password = password;
            return this;
        }

        @Override
        public Builder useSystemPropertyValues(Boolean useSystemPropertyValues) {
            this.useSystemPropertyValues = useSystemPropertyValues;
            return this;
        }

        public void setUseSystemPropertyValues(Boolean useSystemPropertyValues) {
            this.useSystemPropertyValues(useSystemPropertyValues);
        }

        @Override
        public Builder useEnvironmentVariableValues(Boolean useEnvironmentVariablesValues) {
            this.useEnvironmentVariablesValues = useEnvironmentVariablesValues;
            return this;
        }

        public void setUseEnvironmentVariablesValues(Boolean useEnvironmentVariablesValues) {
            this.useEnvironmentVariableValues(useEnvironmentVariablesValues);
        }

        @Override
        public ProxyConfiguration build() {
            return new ProxyConfiguration(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, ProxyConfiguration> {
        public Builder host(String var1);

        public Builder port(int var1);

        public Builder scheme(String var1);

        public Builder nonProxyHosts(Set<String> var1);

        public Builder username(String var1);

        public Builder password(String var1);

        public Builder useSystemPropertyValues(Boolean var1);

        public Builder useEnvironmentVariableValues(Boolean var1);
    }
}

