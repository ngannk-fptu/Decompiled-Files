/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.dialect;

import java.util.Locale;
import org.hibernate.dialect.Oracle9Dialect;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.sql.CaseFragment;
import org.hibernate.sql.DecodeCaseFragment;
import org.hibernate.sql.JoinFragment;
import org.hibernate.sql.OracleJoinFragment;
import org.jboss.logging.Logger;

@Deprecated
public class OracleDialect
extends Oracle9Dialect {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)OracleDialect.class.getName());

    public OracleDialect() {
        LOG.deprecatedOracleDialect();
        this.registerColumnType(93, "date");
        this.registerColumnType(1, "char(1)");
        this.registerColumnType(12, 4000L, "varchar2($l)");
    }

    @Override
    public JoinFragment createOuterJoinFragment() {
        return new OracleJoinFragment();
    }

    @Override
    public CaseFragment createCaseFragment() {
        return new DecodeCaseFragment();
    }

    @Override
    public String getLimitString(String sql, boolean hasOffset) {
        sql = sql.trim();
        boolean isForUpdate = false;
        if (sql.toLowerCase(Locale.ROOT).endsWith(" for update")) {
            sql = sql.substring(0, sql.length() - 11);
            isForUpdate = true;
        }
        StringBuilder pagingSelect = new StringBuilder(sql.length() + 100);
        if (hasOffset) {
            pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
        } else {
            pagingSelect.append("select * from ( ");
        }
        pagingSelect.append(sql);
        if (hasOffset) {
            pagingSelect.append(" ) row_ ) where rownum_ <= ? and rownum_ > ?");
        } else {
            pagingSelect.append(" ) where rownum <= ?");
        }
        if (isForUpdate) {
            pagingSelect.append(" for update");
        }
        return pagingSelect.toString();
    }

    @Override
    public String getSelectClauseNullString(int sqlType) {
        switch (sqlType) {
            case 1: 
            case 12: {
                return "to_char(null)";
            }
            case 91: 
            case 92: 
            case 93: {
                return "to_date(null)";
            }
        }
        return "to_number(null)";
    }

    @Override
    public String getCurrentTimestampSelectString() {
        return "select sysdate from dual";
    }

    @Override
    public String getCurrentTimestampSQLFunctionName() {
        return "sysdate";
    }
}

