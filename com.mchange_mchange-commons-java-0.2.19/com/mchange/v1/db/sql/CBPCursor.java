/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql;

import com.mchange.v1.db.sql.ConnectionBundle;
import com.mchange.v1.db.sql.ConnectionBundlePool;
import com.mchange.v1.db.sql.SimpleCursor;
import java.sql.ResultSet;

public abstract class CBPCursor
extends SimpleCursor {
    ConnectionBundle returnMe;
    ConnectionBundlePool home;

    public CBPCursor(ResultSet resultSet, ConnectionBundle connectionBundle, ConnectionBundlePool connectionBundlePool) {
        super(resultSet);
        this.returnMe = connectionBundle;
        this.home = connectionBundlePool;
    }

    @Override
    public void close() throws Exception {
        try {
            super.close();
        }
        finally {
            this.home.checkinBundle(this.returnMe);
        }
    }
}

