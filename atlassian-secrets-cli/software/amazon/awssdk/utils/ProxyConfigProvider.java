/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils;

import java.util.Optional;
import java.util.Set;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.CollectionUtils;
import software.amazon.awssdk.utils.internal.proxy.ProxyEnvironmentVariableConfigProvider;
import software.amazon.awssdk.utils.internal.proxy.ProxySystemPropertyConfigProvider;

@SdkProtectedApi
public interface ProxyConfigProvider {
    public static final String HTTPS = "https";

    public static ProxyConfigProvider fromSystemPropertySettings(String scheme) {
        return new ProxySystemPropertyConfigProvider(scheme);
    }

    public static ProxyConfigProvider fromEnvironmentSettings(String scheme) {
        return new ProxyEnvironmentVariableConfigProvider(scheme);
    }

    public static ProxyConfigProvider fromSystemEnvironmentSettings(Boolean useSystemPropertyValues, Boolean useEnvironmentVariableValues, String scheme) {
        boolean isProxyConfigurationNotSet;
        ProxyConfigProvider resultProxyConfig = null;
        if (Boolean.TRUE.equals(useSystemPropertyValues)) {
            resultProxyConfig = ProxyConfigProvider.fromSystemPropertySettings(scheme);
        } else if (Boolean.TRUE.equals(useEnvironmentVariableValues)) {
            return ProxyConfigProvider.fromEnvironmentSettings(scheme);
        }
        boolean bl = isProxyConfigurationNotSet = resultProxyConfig != null && resultProxyConfig.host() == null && resultProxyConfig.port() == 0 && !resultProxyConfig.password().isPresent() && !resultProxyConfig.userName().isPresent() && CollectionUtils.isNullOrEmpty(resultProxyConfig.nonProxyHosts());
        if (isProxyConfigurationNotSet && Boolean.TRUE.equals(useEnvironmentVariableValues)) {
            return ProxyConfigProvider.fromEnvironmentSettings(scheme);
        }
        return resultProxyConfig;
    }

    public int port();

    public Optional<String> userName();

    public Optional<String> password();

    public String host();

    public Set<String> nonProxyHosts();
}

