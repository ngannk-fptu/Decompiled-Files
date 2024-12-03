/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.MariaDBDialect;
import org.hibernate.dialect.function.StaticPrecisionFspTimestampFunction;

public class MariaDB53Dialect
extends MariaDBDialect {
    public MariaDB53Dialect() {
        this.registerColumnType(93, "datetime(6)");
        StaticPrecisionFspTimestampFunction currentTimestampFunction = new StaticPrecisionFspTimestampFunction("now", 6);
        this.registerFunction("now", currentTimestampFunction);
        this.registerFunction("current_timestamp", currentTimestampFunction);
        this.registerFunction("localtime", currentTimestampFunction);
        this.registerFunction("localtimestamp", currentTimestampFunction);
        this.registerFunction("sysdate", new StaticPrecisionFspTimestampFunction("sysdate", 6));
    }
}

