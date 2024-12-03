/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;
import net.java.ao.DisposableDataSource;

public class H2VersionUtil {
    private static final AtomicReference<Boolean> H2_LATEST_2_1_X = new AtomicReference();

    public H2VersionUtil(DisposableDataSource dataSource) {
        if (H2_LATEST_2_1_X.get() == null) {
            H2_LATEST_2_1_X.set(this.getH2VersionFlag(dataSource));
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private boolean getH2VersionFlag(DisposableDataSource dataSource) {
        try (Connection connection = dataSource.getConnection();){
            int majorVersion = connection.getMetaData().getDatabaseMajorVersion();
            boolean bl = majorVersion >= 2;
            return bl;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isH2Latest2_1_X() {
        return H2_LATEST_2_1_X.get();
    }
}

