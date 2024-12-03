/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import net.java.ao.Common;
import net.java.ao.DisposableDataSource;
import net.java.ao.db.DerbyDatabaseProvider;

public class EmbeddedDerbyDatabaseProvider
extends DerbyDatabaseProvider {
    private final String uri;

    public EmbeddedDerbyDatabaseProvider(DisposableDataSource dataSource, String uri) {
        super(dataSource);
        this.uri = Objects.requireNonNull(uri, "uri can't be null");
    }

    @Override
    public void dispose() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(this.uri + ";shutdown=true");
        }
        catch (SQLException sQLException) {
            Common.closeQuietly(conn);
        }
        catch (Throwable throwable) {
            Common.closeQuietly(conn);
            throw throwable;
        }
        Common.closeQuietly(conn);
        super.dispose();
    }
}

