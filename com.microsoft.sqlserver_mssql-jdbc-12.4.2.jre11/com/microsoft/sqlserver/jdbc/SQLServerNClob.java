/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.BaseInputStream;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.SQLServerClobBase;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.TypeInfo;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.NClob;
import java.sql.SQLException;
import java.util.logging.Logger;

public final class SQLServerNClob
extends SQLServerClobBase
implements NClob {
    private static final long serialVersionUID = 3593610902551842327L;
    private static final Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerNClob");

    SQLServerNClob(SQLServerConnection connection) {
        super(connection, "", connection.getDatabaseCollation(), logger, null);
        this.setDefaultCharset(StandardCharsets.UTF_16LE);
    }

    SQLServerNClob(BaseInputStream stream, TypeInfo typeInfo) {
        super(null, stream, typeInfo.getSQLCollation(), logger, typeInfo);
        this.setDefaultCharset(StandardCharsets.UTF_16LE);
    }

    @Override
    public InputStream getAsciiStream() throws SQLException {
        this.fillFromStream();
        return super.getAsciiStream();
    }

    @Override
    final JDBCType getJdbcType() {
        return JDBCType.NCLOB;
    }
}

