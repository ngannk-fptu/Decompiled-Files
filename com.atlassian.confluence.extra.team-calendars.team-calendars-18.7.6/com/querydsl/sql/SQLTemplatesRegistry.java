/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.sql.CUBRIDTemplates;
import com.querydsl.sql.DerbyTemplates;
import com.querydsl.sql.FirebirdTemplates;
import com.querydsl.sql.H2Templates;
import com.querydsl.sql.HSQLDBTemplates;
import com.querydsl.sql.Keywords;
import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.OracleTemplates;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLServer2005Templates;
import com.querydsl.sql.SQLServer2008Templates;
import com.querydsl.sql.SQLServer2012Templates;
import com.querydsl.sql.SQLServerTemplates;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.SQLiteTemplates;
import com.querydsl.sql.TeradataTemplates;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class SQLTemplatesRegistry {
    public SQLTemplates getTemplates(DatabaseMetaData md) throws SQLException {
        return this.getBuilder(md).build();
    }

    public SQLTemplates.Builder getBuilder(DatabaseMetaData md) throws SQLException {
        String name = md.getDatabaseProductName().toLowerCase();
        if (name.equals("cubrid")) {
            return CUBRIDTemplates.builder();
        }
        if (name.equals("apache derby")) {
            return DerbyTemplates.builder();
        }
        if (name.startsWith("firebird")) {
            return FirebirdTemplates.builder();
        }
        if (name.equals("h2")) {
            return H2Templates.builder();
        }
        if (name.equals("hsql")) {
            return HSQLDBTemplates.builder();
        }
        if (name.equals("mysql")) {
            return MySQLTemplates.builder();
        }
        if (name.equals("oracle")) {
            return OracleTemplates.builder();
        }
        if (name.equals("postgresql")) {
            return PostgreSQLTemplates.builder();
        }
        if (name.equals("sqlite")) {
            return SQLiteTemplates.builder();
        }
        if (name.startsWith("teradata")) {
            return TeradataTemplates.builder();
        }
        if (name.equals("microsoft sql server")) {
            switch (md.getDatabaseMajorVersion()) {
                case 11: 
                case 12: {
                    return SQLServer2012Templates.builder();
                }
                case 10: {
                    return SQLServer2008Templates.builder();
                }
                case 9: {
                    return SQLServer2005Templates.builder();
                }
            }
            return SQLServerTemplates.builder();
        }
        return new SQLTemplates.Builder(){

            @Override
            protected SQLTemplates build(char escape, boolean quote) {
                return new SQLTemplates(Keywords.DEFAULT, "\"", escape, quote);
            }
        };
    }
}

