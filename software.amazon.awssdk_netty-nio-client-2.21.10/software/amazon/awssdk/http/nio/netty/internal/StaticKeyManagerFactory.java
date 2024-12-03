/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.StaticKeyManagerFactorySpi;

@SdkInternalApi
public final class StaticKeyManagerFactory
extends KeyManagerFactory {
    private StaticKeyManagerFactory(KeyManager[] keyManagers) {
        super(new StaticKeyManagerFactorySpi(keyManagers), null, null);
    }

    public static StaticKeyManagerFactory create(KeyManager[] keyManagers) {
        return new StaticKeyManagerFactory(keyManagers);
    }
}

