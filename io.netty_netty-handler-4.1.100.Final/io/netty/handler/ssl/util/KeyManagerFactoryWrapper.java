/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.ssl.util;

import io.netty.handler.ssl.util.SimpleKeyManagerFactory;
import io.netty.util.internal.ObjectUtil;
import java.security.KeyStore;
import javax.net.ssl.KeyManager;
import javax.net.ssl.ManagerFactoryParameters;

public final class KeyManagerFactoryWrapper
extends SimpleKeyManagerFactory {
    private final KeyManager km;

    public KeyManagerFactoryWrapper(KeyManager km) {
        this.km = (KeyManager)ObjectUtil.checkNotNull((Object)km, (String)"km");
    }

    @Override
    protected void engineInit(KeyStore keyStore, char[] var2) throws Exception {
    }

    @Override
    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws Exception {
    }

    @Override
    protected KeyManager[] engineGetKeyManagers() {
        return new KeyManager[]{this.km};
    }
}

