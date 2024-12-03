/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.sql.SQLException;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.hint.IndexQueryHintHandler;
import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
import org.hibernate.internal.util.JdbcExceptionHelper;

public class MySQL5Dialect
extends MySQLDialect {
    private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter(){

        @Override
        protected String doExtractConstraintName(SQLException sqle) throws NumberFormatException {
            int sqlState = Integer.parseInt(JdbcExceptionHelper.extractSqlState(sqle));
            switch (sqlState) {
                case 23000: {
                    return this.extractUsingTemplate(" for key '", "'", sqle.getMessage());
                }
            }
            return null;
        }
    };

    @Override
    protected void registerVarcharTypes() {
        this.registerColumnType(12, "longtext");
        this.registerColumnType(12, 65535L, "varchar($l)");
        this.registerColumnType(-1, "longtext");
    }

    @Override
    public boolean supportsColumnCheck() {
        return false;
    }

    @Override
    public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
        return EXTRACTER;
    }

    @Override
    protected String getEngineKeyword() {
        return "engine";
    }

    @Override
    public String getQueryHintString(String query, String hints) {
        return IndexQueryHintHandler.INSTANCE.addQueryHints(query, hints);
    }

    @Override
    public boolean supportsUnionAll() {
        return true;
    }
}

