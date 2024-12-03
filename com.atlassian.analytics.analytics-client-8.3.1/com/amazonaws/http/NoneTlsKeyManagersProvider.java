/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.http;

import com.amazonaws.http.TlsKeyManagersProvider;
import javax.net.ssl.KeyManager;

public class NoneTlsKeyManagersProvider
implements TlsKeyManagersProvider {
    private static final NoneTlsKeyManagersProvider INSTANCE = new NoneTlsKeyManagersProvider();

    private NoneTlsKeyManagersProvider() {
    }

    @Override
    public KeyManager[] getKeyManagers() {
        return null;
    }

    public static NoneTlsKeyManagersProvider getInstance() {
        return INSTANCE;
    }
}

