/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql;

import com.mchange.v1.db.sql.ConnectionBundle;
import com.mchange.v1.db.sql.ConnectionBundlePool;

public class CBPUtils {
    public static void attemptCheckin(ConnectionBundle connectionBundle, ConnectionBundlePool connectionBundlePool) {
        try {
            connectionBundlePool.checkinBundle(connectionBundle);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

