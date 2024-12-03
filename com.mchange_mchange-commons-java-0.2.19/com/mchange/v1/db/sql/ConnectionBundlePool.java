/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql;

import com.mchange.v1.db.sql.ConnectionBundle;
import com.mchange.v1.util.BrokenObjectException;
import com.mchange.v1.util.ClosableResource;
import java.sql.SQLException;

public interface ConnectionBundlePool
extends ClosableResource {
    public ConnectionBundle checkoutBundle() throws SQLException, InterruptedException, BrokenObjectException;

    public void checkinBundle(ConnectionBundle var1) throws SQLException, BrokenObjectException;

    @Override
    public void close() throws SQLException;
}

