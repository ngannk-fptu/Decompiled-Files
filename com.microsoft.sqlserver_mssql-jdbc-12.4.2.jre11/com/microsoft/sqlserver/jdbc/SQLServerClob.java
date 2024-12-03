/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.BaseInputStream;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.SQLServerClobBase;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.TypeInfo;
import java.sql.Clob;
import java.util.logging.Logger;

public class SQLServerClob
extends SQLServerClobBase
implements Clob {
    private static final long serialVersionUID = 2872035282200133865L;
    private static final Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerClob");

    @Deprecated
    public SQLServerClob(SQLServerConnection connection, String data) {
        super(connection, data, null == connection ? null : connection.getDatabaseCollation(), logger, null);
        if (null == data) {
            throw new NullPointerException(SQLServerException.getErrString("R_cantSetNull"));
        }
    }

    SQLServerClob(SQLServerConnection connection) {
        super(connection, "", connection.getDatabaseCollation(), logger, null);
    }

    SQLServerClob(BaseInputStream stream, TypeInfo typeInfo) {
        super(null, stream, typeInfo.getSQLCollation(), logger, typeInfo);
    }

    @Override
    final JDBCType getJdbcType() {
        return JDBCType.CLOB;
    }
}

