/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.http;

import javax.net.ssl.KeyManager;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.internal.http.NoneTlsKeyManagersProvider;

@FunctionalInterface
@SdkPublicApi
public interface TlsKeyManagersProvider {
    public KeyManager[] keyManagers();

    public static TlsKeyManagersProvider noneProvider() {
        return NoneTlsKeyManagersProvider.getInstance();
    }
}

