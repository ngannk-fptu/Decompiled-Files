/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http;

import javax.net.ssl.TrustManager;
import software.amazon.awssdk.annotations.SdkPublicApi;

@FunctionalInterface
@SdkPublicApi
public interface TlsTrustManagersProvider {
    public TrustManager[] trustManagers();
}

