/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.http.nio.netty.internal;

import java.security.KeyStore;
import java.util.Arrays;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.ManagerFactoryParameters;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class StaticKeyManagerFactorySpi
extends KeyManagerFactorySpi {
    private final KeyManager[] keyManagers;

    public StaticKeyManagerFactorySpi(KeyManager[] keyManagers) {
        Validate.paramNotNull((Object)keyManagers, (String)"keyManagers");
        this.keyManagers = Arrays.copyOf(keyManagers, keyManagers.length);
    }

    @Override
    protected void engineInit(KeyStore ks, char[] password) {
        throw new UnsupportedOperationException("engineInit not supported by this KeyManagerFactory");
    }

    @Override
    protected void engineInit(ManagerFactoryParameters spec) {
        throw new UnsupportedOperationException("engineInit not supported by this KeyManagerFactory");
    }

    @Override
    protected KeyManager[] engineGetKeyManagers() {
        return this.keyManagers;
    }
}

