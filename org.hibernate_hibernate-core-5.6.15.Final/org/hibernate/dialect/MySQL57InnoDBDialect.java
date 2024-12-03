/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.dialect.function.StaticPrecisionFspTimestampFunction;

@Deprecated
public class MySQL57InnoDBDialect
extends MySQL5InnoDBDialect {
    public MySQL57InnoDBDialect() {
        this.registerColumnType(93, "datetime(6)");
        this.registerColumnType(2000, "json");
        StaticPrecisionFspTimestampFunction currentTimestampFunction = new StaticPrecisionFspTimestampFunction("now", 6);
        this.registerFunction("now", currentTimestampFunction);
        this.registerFunction("current_timestamp", currentTimestampFunction);
        this.registerFunction("localtime", currentTimestampFunction);
        this.registerFunction("localtimestamp", currentTimestampFunction);
        this.registerFunction("sysdate", new StaticPrecisionFspTimestampFunction("sysdate", 6));
    }

    @Override
    public boolean supportsRowValueConstructorSyntaxInInList() {
        return true;
    }
}

