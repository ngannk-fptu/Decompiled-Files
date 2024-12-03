/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.hibernate.dialect.InnoDBStorageEngine;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.MySQLStorageEngine;
import org.hibernate.engine.jdbc.env.spi.IdentifierCaseStrategy;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelperBuilder;

public class MariaDBDialect
extends MySQL5Dialect {
    @Override
    public boolean supportsRowValueConstructorSyntaxInInList() {
        return true;
    }

    @Override
    protected MySQLStorageEngine getDefaultMySQLStorageEngine() {
        return InnoDBStorageEngine.INSTANCE;
    }

    @Override
    public IdentifierHelper buildIdentifierHelper(IdentifierHelperBuilder builder, DatabaseMetaData dbMetaData) throws SQLException {
        builder.setUnquotedCaseStrategy(IdentifierCaseStrategy.MIXED);
        builder.setQuotedCaseStrategy(IdentifierCaseStrategy.MIXED);
        return super.buildIdentifierHelper(builder, dbMetaData);
    }
}

