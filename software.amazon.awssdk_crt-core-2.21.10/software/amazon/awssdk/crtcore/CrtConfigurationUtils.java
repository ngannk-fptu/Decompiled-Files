/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.crt.http.HttpMonitoringOptions
 *  software.amazon.awssdk.crt.http.HttpProxyOptions
 *  software.amazon.awssdk.crt.http.HttpProxyOptions$HttpProxyAuthorizationType
 *  software.amazon.awssdk.crt.io.TlsContext
 *  software.amazon.awssdk.utils.NumericUtils
 */
package software.amazon.awssdk.crtcore;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.crt.http.HttpMonitoringOptions;
import software.amazon.awssdk.crt.http.HttpProxyOptions;
import software.amazon.awssdk.crt.io.TlsContext;
import software.amazon.awssdk.crtcore.CrtConnectionHealthConfiguration;
import software.amazon.awssdk.crtcore.CrtProxyConfiguration;
import software.amazon.awssdk.utils.NumericUtils;

@SdkProtectedApi
public final class CrtConfigurationUtils {
    private CrtConfigurationUtils() {
    }

    public static Optional<HttpProxyOptions> resolveProxy(CrtProxyConfiguration proxyConfiguration, TlsContext tlsContext) {
        if (proxyConfiguration == null) {
            return Optional.empty();
        }
        HttpProxyOptions clientProxyOptions = new HttpProxyOptions();
        clientProxyOptions.setHost(proxyConfiguration.host());
        clientProxyOptions.setPort(proxyConfiguration.port());
        if ("https".equalsIgnoreCase(proxyConfiguration.scheme())) {
            clientProxyOptions.setTlsContext(tlsContext);
        }
        if (proxyConfiguration.username() != null && proxyConfiguration.password() != null) {
            clientProxyOptions.setAuthorizationUsername(proxyConfiguration.username());
            clientProxyOptions.setAuthorizationPassword(proxyConfiguration.password());
            clientProxyOptions.setAuthorizationType(HttpProxyOptions.HttpProxyAuthorizationType.Basic);
        } else {
            clientProxyOptions.setAuthorizationType(HttpProxyOptions.HttpProxyAuthorizationType.None);
        }
        return Optional.of(clientProxyOptions);
    }

    public static Optional<HttpMonitoringOptions> resolveHttpMonitoringOptions(CrtConnectionHealthConfiguration config) {
        if (config == null) {
            return Optional.empty();
        }
        HttpMonitoringOptions httpMonitoringOptions = new HttpMonitoringOptions();
        httpMonitoringOptions.setMinThroughputBytesPerSecond(config.minimumThroughputInBps());
        int seconds = NumericUtils.saturatedCast((long)config.minimumThroughputTimeout().getSeconds());
        httpMonitoringOptions.setAllowableThroughputFailureIntervalSeconds(seconds);
        return Optional.of(httpMonitoringOptions);
    }
}

