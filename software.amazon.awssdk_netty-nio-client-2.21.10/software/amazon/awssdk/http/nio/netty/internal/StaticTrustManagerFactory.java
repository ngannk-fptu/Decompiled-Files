/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.handler.ssl.util.SimpleTrustManagerFactory
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.handler.ssl.util.SimpleTrustManagerFactory;
import java.security.KeyStore;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public final class StaticTrustManagerFactory
extends SimpleTrustManagerFactory {
    private final TrustManager[] trustManagers;

    private StaticTrustManagerFactory(TrustManager[] trustManagers) {
        this.trustManagers = trustManagers;
    }

    protected void engineInit(KeyStore keyStore) {
    }

    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) {
    }

    protected TrustManager[] engineGetTrustManagers() {
        return this.trustManagers;
    }

    public static TrustManagerFactory create(TrustManager[] trustManagers) {
        return new StaticTrustManagerFactory(trustManagers);
    }
}

