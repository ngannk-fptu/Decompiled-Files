/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.internal.http;

import javax.net.ssl.KeyManager;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.TlsKeyManagersProvider;

@SdkInternalApi
public final class NoneTlsKeyManagersProvider
implements TlsKeyManagersProvider {
    private static final NoneTlsKeyManagersProvider INSTANCE = new NoneTlsKeyManagersProvider();

    private NoneTlsKeyManagersProvider() {
    }

    @Override
    public KeyManager[] keyManagers() {
        return null;
    }

    public static NoneTlsKeyManagersProvider getInstance() {
        return INSTANCE;
    }
}

