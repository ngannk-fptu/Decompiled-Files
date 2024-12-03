/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.utils.internal.proxy;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.ProxyConfigProvider;
import software.amazon.awssdk.utils.ProxySystemSetting;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkInternalApi
public class ProxySystemPropertyConfigProvider
implements ProxyConfigProvider {
    private static final Logger log = Logger.loggerFor(ProxySystemPropertyConfigProvider.class);
    private final String scheme;

    public ProxySystemPropertyConfigProvider(String scheme) {
        this.scheme = scheme == null ? "http" : scheme;
    }

    private static Integer safelyParseInt(String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (Exception e) {
            log.error(() -> "Failed to parse string" + string, e);
            return null;
        }
    }

    @Override
    public int port() {
        return Objects.equals(this.scheme, "https") ? ProxySystemSetting.HTTPS_PROXY_PORT.getStringValue().map(ProxySystemPropertyConfigProvider::safelyParseInt).orElse(0).intValue() : ProxySystemSetting.PROXY_PORT.getStringValue().map(ProxySystemPropertyConfigProvider::safelyParseInt).orElse(0).intValue();
    }

    @Override
    public Optional<String> userName() {
        return Objects.equals(this.scheme, "https") ? ProxySystemSetting.HTTPS_PROXY_USERNAME.getStringValue() : ProxySystemSetting.PROXY_USERNAME.getStringValue();
    }

    @Override
    public Optional<String> password() {
        return Objects.equals(this.scheme, "https") ? ProxySystemSetting.HTTPS_PROXY_PASSWORD.getStringValue() : ProxySystemSetting.PROXY_PASSWORD.getStringValue();
    }

    @Override
    public String host() {
        return Objects.equals(this.scheme, "https") ? (String)ProxySystemSetting.HTTPS_PROXY_HOST.getStringValue().orElse(null) : (String)ProxySystemSetting.PROXY_HOST.getStringValue().orElse(null);
    }

    @Override
    public Set<String> nonProxyHosts() {
        return SdkHttpUtils.parseNonProxyHostsProperty();
    }
}

