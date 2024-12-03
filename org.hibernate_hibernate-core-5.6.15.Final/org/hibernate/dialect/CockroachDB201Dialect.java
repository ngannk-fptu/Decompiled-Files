/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.CockroachDB192Dialect;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class CockroachDB201Dialect
extends CockroachDB192Dialect {
    public CockroachDB201Dialect() {
        this.registerFunction("current_time", new NoArgSQLFunction("current_time", StandardBasicTypes.TIME, false));
        this.registerFunction("localtime", new NoArgSQLFunction("localtime", StandardBasicTypes.TIME, false));
        this.registerFunction("localtimestamp", new NoArgSQLFunction("localtimestamp", StandardBasicTypes.TIMESTAMP, false));
        this.registerFunction("timeofday", new NoArgSQLFunction("timeofday", StandardBasicTypes.STRING));
    }
}

