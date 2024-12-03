/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.utils.internal.proxy;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.ProxyConfigProvider;
import software.amazon.awssdk.utils.ProxyEnvironmentSetting;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkInternalApi
public class ProxyEnvironmentVariableConfigProvider
implements ProxyConfigProvider {
    private static final Logger log = Logger.loggerFor(ProxyEnvironmentVariableConfigProvider.class);
    private final String scheme;
    private final URL proxyUrl;

    public ProxyEnvironmentVariableConfigProvider(String scheme) {
        this.scheme = scheme == null ? "http" : scheme;
        this.proxyUrl = this.silentlyGetUrl().orElse(null);
    }

    private Optional<URL> silentlyGetUrl() {
        String stringUrl;
        String string = stringUrl = Objects.equals(this.scheme, "https") ? (String)ProxyEnvironmentSetting.HTTPS_PROXY.getStringValue().orElse(null) : (String)ProxyEnvironmentSetting.HTTP_PROXY.getStringValue().orElse(null);
        if (StringUtils.isNotBlank(stringUrl)) {
            try {
                return Optional.of(new URL(stringUrl));
            }
            catch (MalformedURLException e) {
                log.error(() -> "Malformed proxy config environment variable " + stringUrl, e);
            }
        }
        return Optional.empty();
    }

    @Override
    public int port() {
        return Optional.ofNullable(this.proxyUrl).map(URL::getPort).orElse(0);
    }

    @Override
    public Optional<String> userName() {
        return Optional.ofNullable(this.proxyUrl).map(URL::getUserInfo).flatMap(userInfo -> Optional.ofNullable(userInfo.split(":", 2)[0]));
    }

    @Override
    public Optional<String> password() {
        return Optional.ofNullable(this.proxyUrl).map(URL::getUserInfo).filter(userInfo -> userInfo.contains(":")).map(userInfo -> userInfo.split(":", 2)).filter(parts -> ((String[])parts).length > 1).map(parts -> parts[1]);
    }

    @Override
    public String host() {
        return Optional.ofNullable(this.proxyUrl).map(URL::getHost).orElse(null);
    }

    @Override
    public Set<String> nonProxyHosts() {
        return SdkHttpUtils.parseNonProxyHostsEnvironmentVariable();
    }
}

