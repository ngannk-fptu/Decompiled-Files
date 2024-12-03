/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.DB2400Dialect;
import org.hibernate.dialect.identity.DB2IdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.unique.DefaultUniqueDelegate;
import org.hibernate.dialect.unique.UniqueDelegate;

public class DB2400V7R3Dialect
extends DB2400Dialect {
    private final UniqueDelegate uniqueDelegate = new DefaultUniqueDelegate(this);

    @Override
    public UniqueDelegate getUniqueDelegate() {
        return this.uniqueDelegate;
    }

    @Override
    public boolean supportsSequences() {
        return true;
    }

    @Override
    public String getQuerySequencesString() {
        return "select distinct sequence_name from qsys2.syssequences where ( current_schema = '*LIBL' and sequence_schema in ( select schema_name from qsys2.library_list_info ) ) or sequence_schema = current_schema";
    }

    @Override
    public String getLimitString(String sql, int offset, int limit) {
        if (offset == 0) {
            return sql + " fetch first " + limit + " rows only";
        }
        return "select * from ( select inner2_.*, rownumber() over(order by order of inner2_) as rownumber_ from ( " + sql + " fetch first " + limit + " rows only ) as inner2_ ) as inner1_ where rownumber_ > " + offset + " order by rownumber_";
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new DB2IdentityColumnSupport();
    }
}

