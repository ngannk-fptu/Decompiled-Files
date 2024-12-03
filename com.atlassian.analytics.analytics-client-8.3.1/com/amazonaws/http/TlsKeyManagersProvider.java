/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.http;

import javax.net.ssl.KeyManager;

public interface TlsKeyManagersProvider {
    public KeyManager[] getKeyManagers();
}

