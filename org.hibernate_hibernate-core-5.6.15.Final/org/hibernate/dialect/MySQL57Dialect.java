/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.MySQL55Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.StaticPrecisionFspTimestampFunction;
import org.hibernate.type.StandardBasicTypes;

public class MySQL57Dialect
extends MySQL55Dialect {
    public MySQL57Dialect() {
        this.registerColumnType(93, "datetime(6)");
        this.registerColumnType(2000, "json");
        StaticPrecisionFspTimestampFunction currentTimestampFunction = new StaticPrecisionFspTimestampFunction("now", 6);
        this.registerFunction("now", currentTimestampFunction);
        this.registerFunction("current_timestamp", currentTimestampFunction);
        this.registerFunction("localtime", currentTimestampFunction);
        this.registerFunction("localtimestamp", currentTimestampFunction);
        this.registerFunction("sysdate", new StaticPrecisionFspTimestampFunction("sysdate", 6));
        this.registerFunction("weight_string", new StandardSQLFunction("weight_string", StandardBasicTypes.STRING));
        this.registerFunction("to_base64", new StandardSQLFunction("to_base64", StandardBasicTypes.STRING));
        this.registerFunction("from_base64", new StandardSQLFunction("from_base64", StandardBasicTypes.STRING));
    }

    @Override
    public boolean supportsRowValueConstructorSyntaxInInList() {
        return true;
    }
}

