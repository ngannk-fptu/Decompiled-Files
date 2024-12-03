/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.jce.provider.BouncyCastleProvider
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

class SQLServerBouncyCastleLoader {
    private SQLServerBouncyCastleLoader() {
        throw new UnsupportedOperationException(SQLServerException.getErrString("R_notSupported"));
    }

    static void loadBouncyCastle() {
        BouncyCastleProvider p = new BouncyCastleProvider();
        if (null == Security.getProvider(p.getName())) {
            Security.addProvider((Provider)p);
        }
    }
}

