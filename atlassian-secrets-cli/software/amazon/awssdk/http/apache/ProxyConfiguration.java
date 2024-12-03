/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.apache;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.ProxyConfigProvider;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class ProxyConfiguration
implements ToCopyableBuilder<Builder, ProxyConfiguration> {
    private final URI endpoint;
    private final String username;
    private final String password;
    private final String ntlmDomain;
    private final String ntlmWorkstation;
    private final Set<String> nonProxyHosts;
    private final Boolean preemptiveBasicAuthenticationEnabled;
    private final Boolean useSystemPropertyValues;
    private final String host;
    private final int port;
    private final String scheme;
    private final Boolean useEnvironmentVariablesValues;

    private ProxyConfiguration(DefaultClientProxyConfigurationBuilder builder) {
        String resolvedScheme;
        this.endpoint = builder.endpoint;
        this.scheme = resolvedScheme = this.getResolvedScheme(builder);
        ProxyConfigProvider proxyConfiguration = ProxyConfigProvider.fromSystemEnvironmentSettings(builder.useSystemPropertyValues, builder.useEnvironmentVariableValues, resolvedScheme);
        this.username = ProxyConfiguration.resolveUsername(builder, proxyConfiguration);
        this.password = ProxyConfiguration.resolvePassword(builder, proxyConfiguration);
        this.ntlmDomain = builder.ntlmDomain;
        this.ntlmWorkstation = builder.ntlmWorkstation;
        this.nonProxyHosts = ProxyConfiguration.resolveNonProxyHosts(builder, proxyConfiguration);
        this.preemptiveBasicAuthenticationEnabled = builder.preemptiveBasicAuthenticationEnabled == null ? Boolean.FALSE : builder.preemptiveBasicAuthenticationEnabled;
        this.useSystemPropertyValues = builder.useSystemPropertyValues;
        this.useEnvironmentVariablesValues = builder.useEnvironmentVariableValues;
        if (this.endpoint != null) {
            this.host = this.endpoint.getHost();
            this.port = this.endpoint.getPort();
        } else {
            this.host = proxyConfiguration != null ? proxyConfiguration.host() : null;
            this.port = proxyConfiguration != null ? proxyConfiguration.port() : 0;
        }
    }

    private static String resolvePassword(DefaultClientProxyConfigurationBuilder builder, ProxyConfigProvider proxyConfiguration) {
        return !StringUtils.isEmpty(builder.password) || proxyConfiguration == null ? builder.password : proxyConfiguration.password().orElseGet(() -> builder.password);
    }

    private static String resolveUsername(DefaultClientProxyConfigurationBuilder builder, ProxyConfigProvider proxyConfiguration) {
        return !StringUtils.isEmpty(builder.username) || proxyConfiguration == null ? builder.username : proxyConfiguration.userName().orElseGet(() -> builder.username);
    }

    private static Set<String> resolveNonProxyHosts(DefaultClientProxyConfigurationBuilder builder, ProxyConfigProvider proxyConfiguration) {
        if (builder.nonProxyHosts != null || proxyConfiguration == null) {
            return builder.nonProxyHosts;
        }
        return proxyConfiguration.nonProxyHosts();
    }

    private String getResolvedScheme(DefaultClientProxyConfigurationBuilder builder) {
        return this.endpoint != null ? this.endpoint.getScheme() : builder.scheme;
    }

    public String host() {
        return this.host;
    }

    public int port() {
        return this.port;
    }

    public String scheme() {
        return this.scheme;
    }

    public String username() {
        return this.username;
    }

    public String password() {
        return this.password;
    }

    public String ntlmDomain() {
        return this.ntlmDomain;
    }

    public String ntlmWorkstation() {
        return this.ntlmWorkstation;
    }

    public Set<String> nonProxyHosts() {
        return Collections.unmodifiableSet(this.nonProxyHosts != null ? this.nonProxyHosts : Collections.emptySet());
    }

    public Boolean preemptiveBasicAuthenticationEnabled() {
        return this.preemptiveBasicAuthenticationEnabled;
    }

    @Override
    public Builder toBuilder() {
        return ProxyConfiguration.builder().endpoint(this.endpoint).username(this.username).password(this.password).ntlmDomain(this.ntlmDomain).ntlmWorkstation(this.ntlmWorkstation).nonProxyHosts(this.nonProxyHosts).preemptiveBasicAuthenticationEnabled(this.preemptiveBasicAuthenticationEnabled).useSystemPropertyValues(this.useSystemPropertyValues).scheme(this.scheme).useEnvironmentVariableValues(this.useEnvironmentVariablesValues);
    }

    public static Builder builder() {
        return new DefaultClientProxyConfigurationBuilder();
    }

    public String toString() {
        return ToString.builder("ProxyConfiguration").add("endpoint", this.endpoint).add("username", this.username).add("ntlmDomain", this.ntlmDomain).add("ntlmWorkstation", this.ntlmWorkstation).add("nonProxyHosts", this.nonProxyHosts).add("preemptiveBasicAuthenticationEnabled", this.preemptiveBasicAuthenticationEnabled).add("useSystemPropertyValues", this.useSystemPropertyValues).add("useEnvironmentVariablesValues", this.useEnvironmentVariablesValues).add("scheme", this.scheme).build();
    }

    public String resolveScheme() {
        return this.endpoint != null ? this.endpoint.getScheme() : this.scheme;
    }

    private static final class DefaultClientProxyConfigurationBuilder
    implements Builder {
        private URI endpoint;
        private String username;
        private String password;
        private String ntlmDomain;
        private String ntlmWorkstation;
        private Set<String> nonProxyHosts;
        private Boolean preemptiveBasicAuthenticationEnabled;
        private Boolean useSystemPropertyValues = Boolean.TRUE;
        private Boolean useEnvironmentVariableValues = Boolean.TRUE;
        private String scheme = "http";

        private DefaultClientProxyConfigurationBuilder() {
        }

        @Override
        public Builder endpoint(URI endpoint) {
            if (endpoint != null) {
                Validate.isTrue(StringUtils.isEmpty(endpoint.getUserInfo()), "Proxy endpoint user info is not supported.", new Object[0]);
                Validate.isTrue(StringUtils.isEmpty(endpoint.getPath()), "Proxy endpoint path is not supported.", new Object[0]);
                Validate.isTrue(StringUtils.isEmpty(endpoint.getQuery()), "Proxy endpoint query is not supported.", new Object[0]);
                Validate.isTrue(StringUtils.isEmpty(endpoint.getFragment()), "Proxy endpoint fragment is not supported.", new Object[0]);
            }
            this.endpoint = endpoint;
            return this;
        }

        public void setEndpoint(URI endpoint) {
            this.endpoint(endpoint);
        }

        @Override
        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public void setUsername(String username) {
            this.username(username);
        }

        @Override
        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public void setPassword(String password) {
            this.password(password);
        }

        @Override
        public Builder ntlmDomain(String proxyDomain) {
            this.ntlmDomain = proxyDomain;
            return this;
        }

        public void setNtlmDomain(String ntlmDomain) {
            this.ntlmDomain(ntlmDomain);
        }

        @Override
        public Builder ntlmWorkstation(String proxyWorkstation) {
            this.ntlmWorkstation = proxyWorkstation;
            return this;
        }

        public void setNtlmWorkstation(String ntlmWorkstation) {
            this.ntlmWorkstation(ntlmWorkstation);
        }

        @Override
        public Builder nonProxyHosts(Set<String> nonProxyHosts) {
            this.nonProxyHosts = nonProxyHosts != null ? new HashSet<String>(nonProxyHosts) : null;
            return this;
        }

        @Override
        public Builder addNonProxyHost(String nonProxyHost) {
            if (this.nonProxyHosts == null) {
                this.nonProxyHosts = new HashSet<String>();
            }
            this.nonProxyHosts.add(nonProxyHost);
            return this;
        }

        public void setNonProxyHosts(Set<String> nonProxyHosts) {
            this.nonProxyHosts(nonProxyHosts);
        }

        @Override
        public Builder preemptiveBasicAuthenticationEnabled(Boolean preemptiveBasicAuthenticationEnabled) {
            this.preemptiveBasicAuthenticationEnabled = preemptiveBasicAuthenticationEnabled;
            return this;
        }

        public void setPreemptiveBasicAuthenticationEnabled(Boolean preemptiveBasicAuthenticationEnabled) {
            this.preemptiveBasicAuthenticationEnabled(preemptiveBasicAuthenticationEnabled);
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
        public Builder useEnvironmentVariableValues(Boolean useEnvironmentVariableValues) {
            this.useEnvironmentVariableValues = useEnvironmentVariableValues;
            return this;
        }

        @Override
        public Builder scheme(String scheme) {
            this.scheme = scheme;
            return this;
        }

        public void setuseEnvironmentVariableValues(Boolean useEnvironmentVariableValues) {
            this.useEnvironmentVariableValues(useEnvironmentVariableValues);
        }

        @Override
        public ProxyConfiguration build() {
            return new ProxyConfiguration(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, ProxyConfiguration> {
        public Builder endpoint(URI var1);

        public Builder username(String var1);

        public Builder password(String var1);

        public Builder ntlmDomain(String var1);

        public Builder ntlmWorkstation(String var1);

        public Builder nonProxyHosts(Set<String> var1);

        public Builder addNonProxyHost(String var1);

        public Builder preemptiveBasicAuthenticationEnabled(Boolean var1);

        public Builder useSystemPropertyValues(Boolean var1);

        public Builder useEnvironmentVariableValues(Boolean var1);

        public Builder scheme(String var1);
    }
}

