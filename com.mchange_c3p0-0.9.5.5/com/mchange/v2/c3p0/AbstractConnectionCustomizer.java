/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0;

import com.mchange.v2.c3p0.C3P0Registry;
import com.mchange.v2.c3p0.ConnectionCustomizer;
import java.sql.Connection;
import java.util.Map;

public abstract class AbstractConnectionCustomizer
implements ConnectionCustomizer {
    protected Map extensionsForToken(String parentDataSourceIdentityToken) {
        return C3P0Registry.extensionsForToken(parentDataSourceIdentityToken);
    }

    @Override
    public void onAcquire(Connection c, String parentDataSourceIdentityToken) throws Exception {
    }

    @Override
    public void onDestroy(Connection c, String parentDataSourceIdentityToken) throws Exception {
    }

    @Override
    public void onCheckOut(Connection c, String parentDataSourceIdentityToken) throws Exception {
    }

    @Override
    public void onCheckIn(Connection c, String parentDataSourceIdentityToken) throws Exception {
    }
}

